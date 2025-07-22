package com.kamsan.book.user.application.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.domain.Token;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	
	private final TokenRepository tokenRepository;
	private final UserMapper userMapper;
	
	public Map<String, String> generateAndSaveActivationCode(User user) {
		String generatedCode = generateActivationCode(6);
		String verificationToken = UUID.randomUUID().toString();
		Token token = Token
				.builder()
				.user(user)
				.code(generatedCode)
				.verificationToken(verificationToken)
				.expiresAt(OffsetDateTime.now().plusMinutes(15)).build();
		
		tokenRepository.save(token);
		return Map.of(
			    "code", generatedCode,
			    "token", verificationToken
			);
	}

	private String generateActivationCode(int length) {
		String characters = "0123456789";
		StringBuilder sb = new StringBuilder();
		SecureRandom sr = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int randomIndex = sr.nextInt(characters.length());
			sb.append(characters.charAt(randomIndex));
		}
		return sb.toString();
	}
	
	@Transactional
	public ReadUserDTO isVerificationAccountTokenValid(String verificationToken, String code) {
		Token token = tokenRepository.findByVerificationToken(verificationToken)
				.orElseThrow(() -> new ApiException(String.format("Token %s is not valid", verificationToken)));
		
		if (!token.getCode().equals(code)) {
			throw new ApiException(String.format("Code %s provided is not correct. Please try again.", code));
		}
		
		if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
			throw new ApiException("This code has expired");
		}
		token.setValidatedAt(OffsetDateTime.now());
		tokenRepository.save(token);
		return userMapper.userToReadUserDTO(token.getUser());
	}

}

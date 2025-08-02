package com.kamsan.book.user.application.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.email.EmailService;
import com.kamsan.book.email.EmailTemplateName;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.TokenValidationDTO;
import com.kamsan.book.user.domain.Token;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
	
	private final TokenRepository tokenRepository;
	private final UserMapper userMapper;
	private final EmailService emailService;
	@Value("${application.mailing.frontend.activation-account-url}")
	private String baseConfirmationUrl;

	public TokenValidationDTO isVerificationAccountTokenValid(String verificationToken, String code) {
		Token token = tokenRepository.findByVerificationToken(verificationToken)
				.orElseThrow(() -> new ApiException(String.format("Token %s is not valid", verificationToken)));
		
		if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
			sendAccountValidationEmail(token.getUser());
			return new TokenValidationDTO("This code has expired. A new code has been sent to your email address.");
		}
		
		if (!token.getCode().equals(code)) {
			return new TokenValidationDTO(String.format("Code %s provided is not correct. Please try again.", code));
		}
		
		token.setValidatedAt(OffsetDateTime.now());
		tokenRepository.save(token);
		return new TokenValidationDTO(userMapper.userToReadUserDTO(token.getUser()));
	}
	
	public void sendAccountValidationEmail(User user) {
		Map<String, String> codeToken = generateAndSaveActivationCode(user);
		String confirmationUrl = baseConfirmationUrl + String.format("?code=%s", codeToken.get("token"));
		emailService.sendEmail(
				user.getUsername(), 
				user.getFullName(), 
				EmailTemplateName.ACTIVATE_ACCOUNT, 
				confirmationUrl, 
				codeToken.get("code"), 
				"Account Activation");
	}
	
	private Map<String, String> generateAndSaveActivationCode(User user) {
		log.info("Generating code & token uuid url");
		String generatedCode = generateActivationCode(6);
		String verificationToken = UUID.randomUUID().toString();
		Token token = Token
				.builder()
				.user(user)
				.code(generatedCode)
				.verificationToken(verificationToken)
				.expiresAt(OffsetDateTime.now().plusMinutes(15)).build();
		
		tokenRepository.saveAndFlush(token);
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

}

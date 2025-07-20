package com.kamsan.book.user.application.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.email.EmailService;
import com.kamsan.book.email.EmailTemplateName;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.RegisterUserDTO;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.domain.Token;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.RoleRepository;
import com.kamsan.book.user.repository.TokenRepository;
import com.kamsan.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;
	
	@Value("${application.mailing.frontend.activation-account-url}")
	private String confirmationUrl;

	@Transactional
	public void register(RegisterUserDTO registerUserDTO) {

		Role userRole = roleRepository.findByName("USER").orElseThrow(
				() -> new ApiException(String.format("Could not find any role matching the name %s", "USER")));

		User newUser = userMapper.registerUserDTOToUser(registerUserDTO);
		newUser.setAccountLocked(false);
		newUser.setEnabled(false);
		newUser.setRoles(Set.of(userRole));
		newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
		userRepository.save(newUser);

		sendValidationEmail(newUser);
	}
	
	private void sendValidationEmail(User user) {
		final String token = generateAndSaveActivationToken(user);
		emailService.sendEmail(
				user.getUsername(), 
				user.getFullName(), 
				EmailTemplateName.ACTIVATE_ACCOUNT, 
				confirmationUrl, 
				token, 
				"Account Activation");
	}

	private String generateAndSaveActivationToken(User user) {
		String generatedToken = generateActivationCode(6);
		Token token = Token
				.builder()
				.user(user)
				.token(generatedToken)
				.expiresAt(OffsetDateTime.now().plusMinutes(15)).build();
		
		tokenRepository.save(token);
		return generatedToken;
		
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

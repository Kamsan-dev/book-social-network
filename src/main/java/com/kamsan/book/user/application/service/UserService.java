package com.kamsan.book.user.application.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.email.EmailService;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.RegisterUserDTO;
import com.kamsan.book.user.application.dto.AccountValidationCodeDTO;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.RoleRepository;
import com.kamsan.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final TokenService tokenService;

	@Transactional
	public void register(RegisterUserDTO registerUserDTO) {
		
		String email = registerUserDTO.getEmail();
		if (userRepository.findByEmail(email).isPresent()) {
		    throw new ApiException("Email address is already taken");
		}

		Role userRole = roleRepository.findByName("USER").orElseThrow(
				() -> new ApiException(String.format("Could not find any role matching the name %s", "USER")));

		User newUser = userMapper.registerUserDTOToUser(registerUserDTO);
		newUser.setAccountLocked(false);
		newUser.setEnabled(false);
		newUser.setRoles(Set.of(userRole));
		newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
		userRepository.save(newUser);
		
		emailService.sendAccountValidationEmail(newUser);
	}
	
	@Transactional
	public ReadUserDTO enableUserAccount(AccountValidationCodeDTO dto) {
		ReadUserDTO userDTO = tokenService.isVerificationAccountTokenValid(dto.verificationToken(), dto.code());	
		// Account already enabled
		if (userDTO.enabled()) {
			return userDTO;
		} else {
			User user = userRepository.findByEmail(userDTO.email())
					.orElseThrow(() -> new ApiException(String.format("Could not find user with email address %s", userDTO.email())));
			
			user.setEnabled(true);
			userRepository.save(user);
			return userMapper.userToReadUserDTO(user);
		}
	}
}

package com.kamsan.book.user.application.service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.config.security.JwtService;
import com.kamsan.book.email.EmailService;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.AccountValidationCodeDTO;
import com.kamsan.book.user.application.dto.account.AuthenticationFormDTO;
import com.kamsan.book.user.application.dto.account.AuthenticationSuccessDTO;
import com.kamsan.book.user.application.dto.account.RegisterUserDTO;
import com.kamsan.book.user.application.dto.account.TokenValidationDTO;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.RoleRepository;
import com.kamsan.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final TokenService tokenService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

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
		
		tokenService.sendAccountValidationEmail(newUser);
	}
	
	@Transactional
	public TokenValidationDTO enableUserAccount(AccountValidationCodeDTO dto) {
		TokenValidationDTO isTokenValid = tokenService.isVerificationAccountTokenValid(dto.verificationToken(), dto.code());
		ReadUserDTO userToEnable = isTokenValid.getUser();
		if (userToEnable != null && !userToEnable.enabled()) {
			User user = userRepository.findByEmail(userToEnable.email())
					.orElseThrow(() -> new ApiException(String.format("Could not find user with email address %s", userToEnable.email())));
			
			user.setEnabled(true);
			userRepository.save(user);
		}
		return isTokenValid;
	}
	
	@Transactional(readOnly = true)
	public AuthenticationSuccessDTO authenticateUser(AuthenticationFormDTO dto) {
		log.info(dto.email());
			User user = userRepository.findByEmail(dto.email())
					.orElseThrow(() -> new ApiException(String.format("Could not find user with email address %s", dto.email())));
			
			try {
			Authentication authenticate = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), dto.password()));
			// Optionally set authentication in context if needed
	        SecurityContextHolder.getContext().setAuthentication(authenticate);
	        
	        HashMap<String, Object> claims = new HashMap<>();
	        claims.put("fullName", user.getFullName());
			
			String accessToken = jwtService.createAccessToken(claims, user);
			String refreshToken = jwtService.createRefreshToken(user);
			return new AuthenticationSuccessDTO(
					accessToken, 
					refreshToken,
					userMapper.userToReadUserDTO(user));
			
		} catch (DisabledException exception) {
			throw new ApiException("This account is disabled. Please contact our support.");
		}
		catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ApiException("Incorrect email or password.");
		}
	}
	
	@Transactional(readOnly = true)
	public ReadUserDTO getAuthenticatedUserFromSecurityContext() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
		if (userOpt.isEmpty()) {
			SecurityContextHolder.clearContext();
			throw new ApiException("Something went wrong with your session. Please loggin again.");
		}
		return userMapper.userToReadUserDTO(userOpt.get());
	}
}

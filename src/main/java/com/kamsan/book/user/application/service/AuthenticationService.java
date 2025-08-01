package com.kamsan.book.user.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamsan.book.config.security.JwtService;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.*;
import com.kamsan.book.user.domain.AccessToken;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.enums.RoleType;
import com.kamsan.book.user.enums.TokenType;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.AccessTokenRepository;
import com.kamsan.book.user.repository.RoleRepository;
import com.kamsan.book.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static com.kamsan.book.sharedkernel.utils.Constants.USER_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final AccessTokenRepository accessTokenRepository;

	@Transactional
	public void register(RegisterUserDTO registerUserDTO) {
		
		String email = registerUserDTO.getEmail();
		if (userRepository.findByEmail(email).isPresent()) {
		    throw new ApiException("Email address is already taken");
		}

		Role userRole = roleRepository.findByName(RoleType.ROLE_ADMIN).orElseThrow(
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
		// we enable the user if it is not already the case
		if (userToEnable != null && !userToEnable.enabled()) {
			User user = userRepository.findByEmail(userToEnable.email())
					.orElseThrow(() -> new ApiException(String.format(USER_NOT_FOUND_MSG, userToEnable.email())));
			
			user.setEnabled(true);
			userRepository.save(user);
			isTokenValid.setUser(userMapper.userToReadUserDTO(user));
		}
		return isTokenValid;
	}
	
	@Transactional
	public AuthenticationSuccessDTO authenticateUser(AuthenticationFormDTO dto) {
			User user = userRepository.findByEmail(dto.email())
					.orElseThrow(() -> new ApiException(String.format(USER_NOT_FOUND_MSG, dto.email())));
			
			try {
			Authentication authenticate = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), dto.password()));
			// Optionally set authentication in context if needed
	        SecurityContextHolder.getContext().setAuthentication(authenticate);
	        
	        HashMap<String, Object> claims = new HashMap<>();
	        claims.put("fullName", user.getFullName());
			
			String accessToken = jwtService.createAccessToken(claims, user);
			String refreshToken = jwtService.createRefreshToken(user);
			
			revokeAllUserTokens(user);
			saveUserAccessToken(user, accessToken);
			
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

	@Transactional(readOnly = true)
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(
							() -> new ApiException(
									String.format(USER_NOT_FOUND_MSG, 
											userEmail)));
			if (jwtService.isTokenValid(refreshToken, user)) {
		        HashMap<String, Object> claims = new HashMap<>();
		        claims.put("fullName", user.getFullName());
				String accessToken = jwtService.createAccessToken(claims, user);
				
				revokeAllUserTokens(user);
				saveUserAccessToken(user, accessToken);
				
				AuthenticationSuccessDTO authResponse = new AuthenticationSuccessDTO(
						accessToken, 
						refreshToken,
						userMapper.userToReadUserDTO(user));
			
				try {
					new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
				} catch (IOException e) {
					throw new ApiException(
							String.format("Something went wrong when creating a new access token. %s", e.getMessage()));
				}
			}
		}
	}
	
	private void saveUserAccessToken(User user, String accessToken) {
		AccessToken newAccessToken = AccessToken.builder()
									.token(accessToken)
									.expired(false)
									.revoked(false)
									.tokenType(TokenType.BEARER)
									.user(user)
									.build();
		accessTokenRepository.save(newAccessToken);
	}
	
	
	private void revokeAllUserTokens(User user) {
		Set<AccessToken> allAccessTokens = accessTokenRepository.findAllValidAccessTokens(user.getPublicId());
		allAccessTokens.forEach(t -> {
			t.setRevoked(true);
			t.setExpired(true);
		});
		accessTokenRepository.saveAll(allAccessTokens);
	}
}

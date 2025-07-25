package com.kamsan.book.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.kamsan.book.user.domain.AccessToken;
import com.kamsan.book.user.repository.AccessTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler{
	
	private final AccessTokenRepository accessTokenRepository;

	@Override
	public void logout(
			HttpServletRequest request, 
			HttpServletResponse response, 
			Authentication authentication) {
		
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		jwt = authHeader.substring(7);
		AccessToken storeToken = accessTokenRepository.findByToken(jwt)
				.orElse(null);
		
		if (storeToken != null) {
			storeToken.setExpired(true);
			storeToken.setRevoked(true);
			accessTokenRepository.save(storeToken);
		}
	}

}

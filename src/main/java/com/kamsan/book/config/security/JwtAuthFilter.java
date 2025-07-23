package com.kamsan.book.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kamsan.book.user.application.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

	private static final List<String> WHITELIST = List.of("/api/v1/auth");
	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsServiceImpl;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String path = request.getRequestURI();
		if (WHITELIST.stream().anyMatch(path::startsWith)) {
			log.info("whitelist reached");
			filterChain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(7);
		userEmail = jwtService.extractUsername(jwt);
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userEmail);
			if (jwtService.isTokenValid(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);

			}
		}

		filterChain.doFilter(request, response);

	}

}

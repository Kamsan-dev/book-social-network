package com.kamsan.book.config.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.domain.AccessToken;
import com.kamsan.book.user.repository.AccessTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_800; // 432_000_000;
	// 5 days expiration
	public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

	private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

	private final AccessTokenRepository accessTokenRepository;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public String createAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return generateToken(extraClaims, userDetails, true);
	}

	public String createRefreshToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails, false);
	}

	private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean isAccessToken) {
		long expiration = isAccessToken ? ACCESS_TOKEN_EXPIRATION_TIME : REFRESH_TOKEN_EXPIRATION_TIME;

		Map<String, Object> claims = new HashMap<>();
		if (isAccessToken) {
			claims.putAll(claimsWithAuthorities(userDetails));
			claims.putAll(extraClaims);
		}
		return buildToken(claims, userDetails, expiration);
	}

	private Map<String, Object> claimsWithAuthorities(UserDetails userDetails) {
		List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities", authorities);
		return claims;
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails,
			long accessTokenExpirationTime) {
		return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime)).signWith(secretKey)
				.compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {

		boolean isNotExpiredNorRevoked = accessTokenRepository.findByToken(token)
				.map(t -> !t.isExpired() && !t.isRevoked())
				.orElse(false);

		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isNotExpiredNorRevoked);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}

}

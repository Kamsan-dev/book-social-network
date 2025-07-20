package com.kamsan.book.config.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_800; // 432_000_000;
	// 5 days expiration
	public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;
	
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	public String createAccessToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
		return buildToken(claims, userDetails, ACCESS_TOKEN_EXPIRATION_TIME);
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long accessTokenExpirationTime) {

		List<String> authorities = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
				.claim("authorities", authorities)
				.signWith(getSignInKey())
				.compact();
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
	
    private Claims extractAllClaims(String token) {
    	return Jwts.parser()
        .verifyWith((SecretKey) getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}

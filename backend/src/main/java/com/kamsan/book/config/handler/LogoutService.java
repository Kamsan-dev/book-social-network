package com.kamsan.book.config.handler;

import com.kamsan.book.user.domain.AccessToken;
import com.kamsan.book.user.repository.AccessTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final AccessTokenRepository accessTokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if ("access-token".equals(cookie.getName())){
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (jwt == null){
            log.warn("No access-token cookie found during logout");
            return;
        }

        log.info("token : {}", jwt);

        AccessToken storeToken = accessTokenRepository.findByToken(jwt)
                .orElse(null);

        if (storeToken != null) {
            storeToken.setExpired(true);
            storeToken.setRevoked(true);
            accessTokenRepository.save(storeToken);
        }

        // Invalidate cookies
        ResponseCookie expiredAccessToken = ResponseCookie.from("access-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie expiredRefreshToken = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, expiredAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString());
        response.setHeader(HttpHeaders.AUTHORIZATION, "");
    }

}

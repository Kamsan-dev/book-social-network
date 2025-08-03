package com.kamsan.book.user.application.service;

import com.kamsan.book.email.EmailService;
import com.kamsan.book.email.EmailTemplateName;
import com.kamsan.book.sharedkernel.exception.ApiException;
import com.kamsan.book.user.application.dto.account.TokenValidationDTO;
import com.kamsan.book.user.domain.Token;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

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

        if (token.getValidatedAt() != null) {
            return new TokenValidationDTO(userMapper.userToReadUserDTO(token.getUser()));
        }

        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            sendAccountValidationEmail(token.getUser());
            return new TokenValidationDTO("This code has expired. A new code has been sent to your email address.");
        }

        if (!token.getCode().equals(code)) {
            return new TokenValidationDTO(String.format("Code %s provided is not correct. Please try again.", code));
        }

        token.setValidatedAt(OffsetDateTime.now());
        token.setExpiresAt(OffsetDateTime.now());
        tokenRepository.save(token);
        return new TokenValidationDTO(userMapper.userToReadUserDTO(token.getUser()));
    }

    public void sendAccountValidationEmail(User user) {
        Map<String, String> codeToken = generateAndSaveActivationCode(user);
        String token = codeToken.get("token");
        String expiresAt = codeToken.get("expiresAt");
        String confirmationUrl = baseConfirmationUrl +
                String.format("?token=%s&expiresAt=%s", token, URLEncoder.encode(expiresAt, StandardCharsets.UTF_8));
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
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(15);
        Token token = Token
                .builder()
                .user(user)
                .code(generatedCode)
                .verificationToken(verificationToken)
                .expiresAt(expiresAt).build();

        tokenRepository.saveAndFlush(token);
        return Map.of(
                "code", generatedCode,
                "token", verificationToken,
                "expiresAt", expiresAt.toString()
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

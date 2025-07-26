package com.kamsan.book.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.kamsan.book.email.EmailService;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.AccountValidationCodeDTO;
import com.kamsan.book.user.application.dto.account.TokenValidationDTO;
import com.kamsan.book.user.application.service.AuthenticationService;
import com.kamsan.book.user.application.service.TokenService;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthUnitTest {

	@MockitoBean
    private JavaMailSender javaMailSender;

    @MockitoBean
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserMapper userMapper;


    @Test
    @DisplayName("Enable user that is not enabled yet")
    void shouldEnableUserWhenTokenValidAndUserNotEnabled() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String code = "123456";
        AccountValidationCodeDTO dto = new AccountValidationCodeDTO(code, token);

        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setEnabled(false);

        ReadUserDTO userDTO = new ReadUserDTO(
            UUID.randomUUID(),
            "John",
            "Doe",
            "john.doe@example.com",
            "https://example.com/avatar.jpg",
            false,
            false
        );
        
        ReadUserDTO updatedUserDTO = new ReadUserDTO(
                UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.com",
                "https://example.com/avatar.jpg",
                false,
                true
           );

        when(tokenService.isVerificationAccountTokenValid(token, code))
            .thenReturn(new TokenValidationDTO(userDTO));

        when(userRepository.findByEmail(userDTO.email()))
            .thenReturn(Optional.of(user));
        
        when(userMapper.userToReadUserDTO(user))
        	.thenReturn(updatedUserDTO);

        // Act
        TokenValidationDTO result = authenticationService.enableUserAccount(dto);

        // Assert
        verify(tokenService).isVerificationAccountTokenValid(token, code);
        verify(userRepository).save(user);
        assertNotNull(result.getUser());
        assertTrue(result.getUser().enabled());
    }
    
    @Test
    @DisplayName("Case where user already enabled")
    void shouldNotEnableUserWhenTokenValidAndUserAlreadyEnabled() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String code = "123456";
        AccountValidationCodeDTO dto = new AccountValidationCodeDTO(code, token);

        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setEnabled(false);

        ReadUserDTO userDTO = new ReadUserDTO(
            UUID.randomUUID(),
            "John",
            "Doe",
            "john.doe@example.com",
            "https://example.com/avatar.jpg",
            false,
            true
        );
        
        ReadUserDTO updatedUserDTO = new ReadUserDTO(
                UUID.randomUUID(),
                "John",
                "Doe",
                "john.doe@example.com",
                "https://example.com/avatar.jpg",
                false,
                true
           );

        when(tokenService.isVerificationAccountTokenValid(token, code))
            .thenReturn(new TokenValidationDTO(userDTO));

        when(userRepository.findByEmail(userDTO.email()))
            .thenReturn(Optional.of(user));
        
        when(userMapper.userToReadUserDTO(user))
        	.thenReturn(updatedUserDTO);

        // Act
        TokenValidationDTO result = authenticationService.enableUserAccount(dto);

        // Assert
        verify(tokenService).isVerificationAccountTokenValid(token, code);
        verify(userRepository, times(0)).findByEmail(user.getEmail());
        verify(userRepository, times(0)).save(user);
        assertNotNull(result.getUser());
        assertTrue(result.getUser().enabled());
    }

}

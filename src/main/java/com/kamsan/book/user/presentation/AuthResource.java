package com.kamsan.book.user.presentation;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.AccountValidationCodeDTO;
import com.kamsan.book.user.application.dto.account.AuthenticationFormDTO;
import com.kamsan.book.user.application.dto.account.AuthenticationSuccessDTO;
import com.kamsan.book.user.application.dto.account.RegisterUserDTO;
import com.kamsan.book.user.application.dto.account.TokenValidationDTO;
import com.kamsan.book.user.application.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthResource {

	private final AuthenticationService authenticationService;
	
	@PostMapping("register")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDTO registerUserDTO){
		authenticationService.register(registerUserDTO);
		return ResponseEntity.accepted().build();
	}
	
	@PostMapping("account-validation")
	public ResponseEntity<TokenValidationDTO> accountValidation(@RequestBody @Valid AccountValidationCodeDTO dto){
		TokenValidationDTO response = authenticationService.enableUserAccount(dto);
		HttpStatus status = (response.isValid()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(response, status);
	}
	
	@PostMapping("authenticate")
	public ResponseEntity<AuthenticationSuccessDTO> authenticateUser(@RequestBody @Valid AuthenticationFormDTO dto){
			return ResponseEntity.ok(authenticationService.authenticateUser(dto));
	}
	
	@PostMapping("refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		authenticationService.refreshToken(request, response);
	}
}

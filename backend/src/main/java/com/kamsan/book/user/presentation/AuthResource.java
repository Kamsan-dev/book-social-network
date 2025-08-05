package com.kamsan.book.user.presentation;

import com.kamsan.book.user.application.dto.account.*;
import com.kamsan.book.user.application.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
	public ResponseEntity<AuthenticationSuccessDTO> authenticateUser(@RequestBody @Valid AuthenticationFormDTO dto, HttpServletResponse response){
			return ResponseEntity.ok(authenticationService.authenticateUser(dto, response));
	}
	
	@PostMapping("refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		authenticationService.refreshToken(request, response);
	}
}

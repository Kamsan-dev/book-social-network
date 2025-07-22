package com.kamsan.book.user.presentation;

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
import com.kamsan.book.user.application.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthResource {

	private final AuthenticationService userService;
	
	@PostMapping("register")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDTO registerUserDTO){
		userService.register(registerUserDTO);
		return ResponseEntity.accepted().build();
	}
	
	@PostMapping("account-validation")
	public ResponseEntity<ReadUserDTO> accountValidation(@RequestBody @Valid AccountValidationCodeDTO dto){
		return ResponseEntity.ok(userService.enableUserAccount(dto));
	}
	
	@PostMapping("authenticate")
	public ResponseEntity<AuthenticationSuccessDTO> authenticateUser(@RequestBody @Valid AuthenticationFormDTO dto){
			return ResponseEntity.ok(userService.authenticateUser(dto));
	}
}

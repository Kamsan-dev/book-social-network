package com.kamsan.book.user.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kamsan.book.user.application.dto.AccountValidationCodeDTO;
import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.RegisterUserDTO;
import com.kamsan.book.user.application.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthResource {

	private final UserService userService;
	
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

}

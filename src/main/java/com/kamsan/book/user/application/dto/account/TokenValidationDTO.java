package com.kamsan.book.user.application.dto.account;

import com.kamsan.book.user.application.dto.ReadUserDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenValidationDTO {
	
	private String message;
	private ReadUserDTO user;
	private boolean isValid;
	
	public TokenValidationDTO(ReadUserDTO user) {
		this.message = "You account is activated";
		this.user = user;
		this.isValid = true;
	}
	
	public TokenValidationDTO(String message) {
		this.message = message;
		this.user = null;
		this.isValid = false;
	}
}

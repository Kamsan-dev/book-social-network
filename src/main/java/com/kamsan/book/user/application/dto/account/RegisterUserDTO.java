package com.kamsan.book.user.application.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterUserDTO {

	@NotEmpty(message = "Firstname is mandatory")
	@NotNull(message = "Firstname is mandatory")
	private String firstName;
	
	@NotEmpty(message = "Lastname is mandatory")
	@NotNull(message = "Lastname is mandatory")
	private String lastName;
	
	@Email(message = "Email is not well formatted")
	@NotEmpty(message = "Email is mandatory")
	@NotNull(message = "Email is mandatory")
	private String email;
	
	@NotEmpty(message = "Password is mandatory")
	@NotNull(message = "Password is mandatory")
	@Size(min = 8, message = "Password should be 8 characters long minimum")
	private String password;

}

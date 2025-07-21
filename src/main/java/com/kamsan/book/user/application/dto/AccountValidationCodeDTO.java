package com.kamsan.book.user.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountValidationCodeDTO(
	    
	    @NotEmpty(message = "Code must not be empty")
	    @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
	    String code,

	    @NotEmpty(message = "Verification token must not be empty")
	    @Pattern(
	        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
	        message = "Verification token has an invalid format"
	    )
	    String verificationToken

	) {}
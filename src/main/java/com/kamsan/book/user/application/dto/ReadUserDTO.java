package com.kamsan.book.user.application.dto;

import java.util.UUID;

public record ReadUserDTO(UUID publicId, 
		String firstName, 
		String lastName, 
		String email, 
		String imageUrl,
		boolean accountLocked,
		boolean enabled) {

}
package com.kamsan.book.user.application.dto;

import java.util.List;
import java.util.UUID;

public record ReadUserDTO(UUID publicId, 
		String firstName, 
		String lastName, 
		String email, 
		String profileImageId,
		boolean accountLocked,
		boolean enabled,
		List<String> roles,
		List<String> authorities) {

}
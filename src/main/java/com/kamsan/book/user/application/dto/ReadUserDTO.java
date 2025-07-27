package com.kamsan.book.user.application.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.kamsan.book.user.domain.Role;

public record ReadUserDTO(UUID publicId, 
		String firstName, 
		String lastName, 
		String email, 
		String imageUrl,
		boolean accountLocked,
		boolean enabled,
		List<String> roles,
		List<String> authorities) {

}
package com.kamsan.book.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.RegisterUserDTO;
import com.kamsan.book.user.domain.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	ReadUserDTO userToReadUserDTO(User user);
	
	@Mapping(target="accountLocked", ignore = true)
	@Mapping(target="enabled", ignore = true)
	@Mapping(target="id", ignore = true)
	@Mapping(target="imageUrl", ignore = true)
	@Mapping(target="publicId", ignore = true)
	@Mapping(target="roles", ignore = true)
	User registerUserDTOToUser(RegisterUserDTO registerUserDTO);
}
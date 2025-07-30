package com.kamsan.book.user.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kamsan.book.user.application.dto.ReadUserDTO;
import com.kamsan.book.user.application.dto.account.RegisterUserDTO;
import com.kamsan.book.user.domain.Role;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.enums.PermissionType;

import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	@Mapping(target = "authorities", expression = "java(mapAuthorities(user))")
	@Mapping(target = "roles", expression = "java(mapRoles(user))")
	ReadUserDTO userToReadUserDTO(User user);
	
	@Mapping(target="accountLocked", ignore = true)
	@Mapping(target="enabled", ignore = true)
	@Mapping(target="id", ignore = true)
	@Mapping(target="imageUrl", ignore = true)
	@Mapping(target="publicId", ignore = true)
	@Mapping(target="roles", ignore = true)
	@Mapping(target="tokens", ignore = true)
	@Mapping(target="accessTokens", ignore = true)
	User registerUserDTOToUser(RegisterUserDTO registerUserDTO);
	
	
    default List<String> mapAuthorities(User user) {
        if (user == null || user.getRoles().isEmpty()) return Collections.emptyList();
        
        return user.getRoles().stream()
            .flatMap(role -> role.getName().getPermissions().stream())
            .map(PermissionType::getPermission)
            .toList();
    }
    
    default List<String> mapRoles(User user) {
    	return user.getRoles().stream().map(r -> r.getName().toString()).toList();
    }
}
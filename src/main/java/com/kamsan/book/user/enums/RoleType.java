package com.kamsan.book.user.enums;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static com.kamsan.book.user.enums.PermissionType.ADMIN_CREATE;
import static com.kamsan.book.user.enums.PermissionType.ADMIN_READ;
import static com.kamsan.book.user.enums.PermissionType.ADMIN_DELETE;
import static com.kamsan.book.user.enums.PermissionType.ADMIN_UPDATE;
import static com.kamsan.book.user.enums.PermissionType.MANAGER_CREATE;
import static com.kamsan.book.user.enums.PermissionType.MANAGER_DELETE;
import static com.kamsan.book.user.enums.PermissionType.MANAGER_READ;
import static com.kamsan.book.user.enums.PermissionType.MANAGER_UPDATE;

@Getter
@RequiredArgsConstructor
public enum RoleType {

	ROLE_USER(
			Collections.emptySet()), 
	ROLE_ADMIN(Set.of(
			ADMIN_READ, 
			ADMIN_UPDATE, 
			ADMIN_DELETE, 
			ADMIN_CREATE, 
			MANAGER_READ,
			MANAGER_UPDATE, 
			MANAGER_DELETE, 
			MANAGER_CREATE)),
	ROLE_MANAGER(Set.of(
			MANAGER_READ, 
			MANAGER_UPDATE, 
			MANAGER_DELETE, 
			MANAGER_CREATE)
	);

	
	private final Set<PermissionType> permissions;
}

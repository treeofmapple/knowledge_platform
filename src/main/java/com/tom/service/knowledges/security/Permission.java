package com.tom.web.knowledge.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

	USER_READ("user:read"),
	USER_UPDATE("user:update"),
	USER_DELETE("user:delete"),
	USER_CREATE("user:create"),
	ADMIN_READ("admin:read"),
	ADMIN_UPDATE("admin:update"),
	ADMIN_DELETE("admin:delete"),
	ADMIN_CREATE("admin:create"),
	MANAGER_READ("manager:read"),
	MANAGER_UPDATE("manager:update"),
	MANAGER_CREATE("manager:create"),
	MANAGER_DELETE("manager:delete")
	;
	
	@Getter
	private final String permission;
	
}

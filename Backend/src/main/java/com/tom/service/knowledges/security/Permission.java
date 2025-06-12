package com.tom.service.knowledges.security;

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
	;
	
	@Getter
	private final String permission;
	
}

package com.tom.tool.knowledge.security;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationMapper {
	AuthenticationMapper INSTANCE = Mappers.getMapper(AuthenticationMapper.class);
	
	// @Mapping(source = "", target = "")
	
	@Mapping(source = "name", target = "name")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "age", target = "age")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "password", target = "password")
	User buildAttributes(String name, String username, int age, String email, String password);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "user", target = "user")
	@Mapping(source = "token", target = "token")
	@Mapping(source = "tokenType", target = "tokenType")
	@Mapping(source = "revoked", target = "revoked")
	@Mapping(source = "expired", target = "expired")
	Token buildAttributes(User user, String token, TokenType tokenType, boolean revoked, boolean expired);
	
	@Mapping(source = "name", target = "name")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "email", target = "email")
	UserResponse buildUserResponse(User user);
	
	@Mapping(source = "jwtToken", target = "accessToken")
	@Mapping(source = "refreshToken", target = "refreshToken")
	AuthenticationResponse buildResponse(String jwtToken, String refreshToken);
	
}

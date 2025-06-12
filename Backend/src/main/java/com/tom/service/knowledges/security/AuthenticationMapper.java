package com.tom.service.knowledges.security;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.user.RegisterRequest;
import com.tom.service.knowledges.user.UpdateRequest;
import com.tom.service.knowledges.user.User;
import com.tom.service.knowledges.user.UserResponse;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationMapper {
	AuthenticationMapper INSTANCE = Mappers.getMapper(AuthenticationMapper.class);
	
	// @Mapping(source = "", target = "")
	
	@Mapping(target = "password", ignore = true)
	User toUser(RegisterRequest request);
	
	@Mapping(target = "id", ignore = true)
	Token buildToken(User user, String token, TokenType tokenType, boolean revoked, boolean expired);
	
	UserResponse toUserResponse(User user);
	
	@Mapping(source = "jwtToken", target = "accessToken")
	@Mapping(source = "refreshToken", target = "refreshToken")
	AuthenticationResponse toAuthenticationResponse(String jwtToken, String refreshToken);
	
	User mergeUser(@MappingTarget User users, UpdateRequest request);
}

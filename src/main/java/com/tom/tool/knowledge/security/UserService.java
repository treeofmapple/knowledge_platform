package com.tom.tool.knowledge.security;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tom.tool.knowledge.common.EntityUpdater;
import com.tom.tool.knowledge.common.Operations;
import com.tom.tool.knowledge.common.ServiceLogger;
import com.tom.tool.knowledge.exception.AlreadyExistsException;
import com.tom.tool.knowledge.exception.IllegalStatusException;
import com.tom.tool.knowledge.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	@Value("${application.security.expiration}")
	private String jwtExpiration;

	@Value("${application.security.refresh-token.expiration}")
	private String refreshExpiration;

	private final AuthenticationManager authManager;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationMapper mapper;
	private final UserRepository repository;
	private final Operations operations;
	private final EntityUpdater updater;
	private final JwtService jwtService;
	
	public UserResponse getCurrentUser(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		return mapper.buildUserResponse(user);
	}

	// get user by name or email
	public List<UserResponse> findUser(String userInfo, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		ServiceLogger.info("IP {}, user {}, is searching for: {}", operations.getUserIp(), user.getUsername(), userInfo);

		var users = repository.findByUsernameOrEmailContainingIgnoreCase(userInfo);
		if (users.isEmpty()) {
			throw new NotFoundException("No users found matching: " + userInfo);
		}
		return users.stream().map(mapper::buildUserResponse).toList();
	}

	// edit connected user
	@Transactional
	public void editUser(UpdateRequest request, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		if (!request.password().equals(request.confirmPassword())) {
			throw new IllegalStatusException("Wrong Password");
		}
		var data = updater.mergeData(user, request);
		repository.save(data);
		ServiceLogger.info("IP {}, user {} changed their password", operations.getUserIp(), user.getUsername());
	}

	// logout connected user
	public void logout(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		updater.revokeAllUserTokens(user);
		ServiceLogger.info("IP {}, user {} has logged out. All valid tokens revoked.", operations.getUserIp(), user.getUsername());
	}

	// connected user
	@Transactional
	public void changePassword(PasswordRequest request, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		if (!passwordEncoder.matches(request.confirmationpassword(), user.getPassword())) {
			ServiceLogger.warn("Wrong Password");
			throw new IllegalStatusException("Wrong Password");
		}

		if (!request.newpassword().equals(request.confirmationpassword())) {
			ServiceLogger.warn("Passwords are not the same");
			throw new IllegalStatusException("Passwords are not the same");
		}

		user.setPassword(passwordEncoder.encode(request.newpassword()));
		repository.save(user);
		ServiceLogger.info("IP {}, user {} changed their password", operations.getUserIp(), user.getUsername());
	}

	@Transactional
	public AuthenticationResponse register(RegisterRequest request) {
		if (repository.existsByUsername(request.username()) || repository.existsByEmail(request.email())) {
			throw new AlreadyExistsException("User already exists");
		}

		if (request.password().equals(request.confirmpassword())) {
			throw new IllegalStatusException("Passwords are not the same");
		}

		var user = mapper.buildAttributes(request.name(), request.username(), request.age(), request.email(),
				passwordEncoder.encode(request.password()));
		user.setRole(Role.USER);
		var savedUser = repository.save(user);
		
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		updater.saveUserToken(savedUser, jwtToken);

		ServiceLogger.info("IP {}, user registered: {}", operations.getUserIp(), request.username());
		var response = mapper.buildResponse(jwtToken, refreshToken);
		return response;
	}

	@Transactional
	public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
		String userIdentifier = request.userinfo();

		var user = repository.findByUsername(userIdentifier).or(() -> repository.findByEmail(userIdentifier))
				.orElseThrow(() -> new NotFoundException("Username or email wasn't found"));
	    
		authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		updater.revokeAllUserTokens(user);
		updater.saveUserToken(user, jwtToken);
		ServiceLogger.info("IP {}, user authenticated: {}", operations.getUserIp(), userIdentifier);

		var responses = mapper.buildResponse(jwtToken, refreshToken);
		return responses;
	}

	@Transactional
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userInfo;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new NotFoundException("Auth token was not found");
		}
		refreshToken = authHeader.substring(7);
		userInfo = jwtService.extractUsername(refreshToken);
		if (userInfo != null) {
			var user = repository.findByUsername(userInfo).or(() -> repository.findByEmail(userInfo))
					.orElseThrow(() -> new NotFoundException("User username or email not found"));
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				updater.revokeAllUserTokens(user);
				updater.saveUserToken(user, accessToken);
				var authResponse = mapper.buildResponse(accessToken, refreshToken);
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
				ServiceLogger.info("Access token refreshed for user {}", userInfo);
			}
		}
	}

	@Transactional
	public String deleteMe(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		updater.revokeAllUserTokens(user);
		repository.deleteById(user.getId());
		ServiceLogger.info("IP {}, user {} has deleted their account", operations.getUserIp(), user.getUsername());
		return "The user" + user.getUsername() + "was deleted";
	}

}

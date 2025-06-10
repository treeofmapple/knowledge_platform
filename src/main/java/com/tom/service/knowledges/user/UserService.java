package com.tom.service.knowledges.user;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.exception.AlreadyExistsException;
import com.tom.service.knowledges.exception.IllegalStatusException;
import com.tom.service.knowledges.exception.NotFoundException;
import com.tom.service.knowledges.security.AuthenticationMapper;
import com.tom.service.knowledges.security.AuthenticationRequest;
import com.tom.service.knowledges.security.AuthenticationResponse;
import com.tom.service.knowledges.security.JwtService;
import com.tom.service.knowledges.security.Role;

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
	private final SystemUtils operations;
	private final UserUtils utils;
	private final JwtService jwtService;
	
	public UserResponse getCurrentUser(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		return mapper.toUserResponse(user);
	}

	// get user by name or email
	public List<UserResponse> findUser(String userInfo, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		ServiceLogger.info("IP {}, user {}, is searching for: {}", operations.getUserIp(), user.getUsername(), userInfo);

		var users = repository.findByUsernameOrEmailContainingIgnoreCase(userInfo);
		if (users.isEmpty()) {
			throw new NotFoundException("No users found matching: " + userInfo);
		}
		return users.stream().map(mapper::toUserResponse).collect(Collectors.toList());
	}

	// edit connected user
	@Transactional
	public void editUser(UpdateRequest request, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		if (!request.password().equals(request.confirmPassword())) {
			throw new IllegalStatusException("Wrong Password");
		}
		var data = utils.mergeData(user, request);
		repository.save(data);
		ServiceLogger.info("IP {}, user {} changed their password", operations.getUserIp(), user.getUsername());
	}

	// logout connected user
	public void logout(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		utils.revokeAllUserTokens(user);
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
		if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
			throw new AlreadyExistsException("User already exists");
		}

		if (request.password().equals(request.confirmpassword())) {
			throw new IllegalStatusException("Passwords are not the same");
		}

		var user = mapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(Role.USER);
		var savedUser = repository.save(user);
		
		var jwtToken = jwtService.generateToken(savedUser);
		var refreshToken = jwtService.generateRefreshToken(savedUser);
		utils.saveUserToken(savedUser, jwtToken);

		ServiceLogger.info("IP {}, user registered: {}", operations.getUserIp(), request.username());
		return mapper.toAuthenticationResponse(jwtToken, refreshToken);
	}

	@Transactional
	public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
		String userIdentifier = request.userinfo();

		var user = repository.findByUsername(userIdentifier).or(() -> repository.findByEmail(userIdentifier))
				.orElseThrow(() -> new NotFoundException("Username or email wasn't found"));

		authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		utils.revokeAllUserTokens(user);
		utils.saveUserToken(user, jwtToken);
		ServiceLogger.info("IP {}, user authenticated: {}", operations.getUserIp(), userIdentifier);

		return mapper.toAuthenticationResponse(jwtToken, refreshToken);
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
				utils.revokeAllUserTokens(user);
				utils.saveUserToken(user, accessToken);
				var authResponse = mapper.toAuthenticationResponse(accessToken, refreshToken);
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
				ServiceLogger.info("Access token refreshed for user {}", userInfo);
			}
		}
	}

	@Transactional
	public String deleteMe(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		utils.revokeAllUserTokens(user);
		repository.deleteById(user.getId());
		ServiceLogger.info("IP {}, user {} has deleted their account", operations.getUserIp(), user.getUsername());
		return "The user" + user.getUsername() + "was deleted";
	}

}

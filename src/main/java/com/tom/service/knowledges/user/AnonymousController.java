package com.tom.service.knowledges.user;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tom.service.knowledges.security.AuthenticationRequest;
import com.tom.service.knowledges.security.AuthenticationResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AnonymousController {

	private final UserService service;
	
	@PostMapping("/sign-up")
	public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
		var register = service.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(register);
	}
	
	@PostMapping("/sign-in")
	public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
		var authenticate = service.authenticate(request, response);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(authenticate);
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) throws IOException {
		var response = service.refreshToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}

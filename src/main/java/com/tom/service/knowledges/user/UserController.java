package com.tom.service.knowledges.user;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;
	
	@GetMapping("/me")
    @PreAuthorize("hasAuthority('user:read')")
    public UserResponse getCurrentUser(Principal principal) {
        return service.getCurrentUser(principal);
    }
	
	@PutMapping("/logout")
    @PreAuthorize("hasAuthority('user:update')")
	public ResponseEntity<String> logout(Principal connectedUser) {
		service.logout(connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body("User logout");
	}
	
	@PutMapping("/password/connected")
	@PreAuthorize("hasAuthority('user:update')")
	public ResponseEntity<String> changePassword(@RequestBody PasswordRequest request, Principal connectedUser) {
		service.changePassword(request, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body("User password was changed");
	}
	
	@PostMapping("/")
	@PreAuthorize("hasAuthority('user:read')")
	public ResponseEntity <List<UserResponse>> findUser(@RequestParam String user, Principal connectedUser) {
		var data = service.findUser(user, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(data);
	}
	
	@PostMapping("/remove")
	@PreAuthorize("hasAuthority('user:delete')")
	public ResponseEntity <String> deleteMe(HttpServletRequest request, HttpServletResponse response, Principal connectedUser) {
		var data = service.deleteMe(connectedUser);
		request.getSession().invalidate();
		return ResponseEntity.status(HttpStatus.OK).body(data);
	}
}

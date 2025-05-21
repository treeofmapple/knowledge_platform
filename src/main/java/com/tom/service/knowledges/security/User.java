package com.tom.web.knowledge.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class User extends Auditable implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "name", unique = false, updatable = true, nullable = true)
	private String name;
	
	@Column(name = "username", unique = true, updatable = true, nullable = false)
	private String username;

	@Column(name = "email", unique = false, updatable = true, nullable = true)
	private String email;

	@Column(name = "age", unique = false, updatable = true, nullable = true)
	private int age;
	
	@Column(name = "password", unique = false, updatable = true, nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@OneToMany(mappedBy = "user")
	private List<Token> tokens;
	
	public String getIdentifier() {
	    return (email != null && !email.isEmpty()) ? email : username;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.getAuthorities();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

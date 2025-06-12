package com.tom.service.knowledges.security;

import com.tom.service.knowledges.model.Auditable;
import com.tom.service.knowledges.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "tokens")
public class Token extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "token", updatable = true)
	private String token;

	@Enumerated(EnumType.STRING)
	private TokenType tokenType = TokenType.BEARER;

	@Column(name = "revoked", updatable = true)
	private boolean revoked;

	@Column(name = "expired", updatable = true)
	private boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
}

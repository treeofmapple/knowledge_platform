package com.tom.service.knowledges.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsernameOrEmail(String username);
	
	Optional<User> findByVerificationToken(String token);

	List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(@Param("input") String input);
	
	boolean existsByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
	@Modifying
	@Transactional
	void deleteByUsername(String username);
	
    @Modifying
    @Transactional
    void deleteByEmail(String email);
	
}

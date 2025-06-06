package com.tom.service.knowledges.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsernameOrEmail(String username);

	List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String input);
	
	boolean existsByUsernameOrEmail(String username, String email);
	
}

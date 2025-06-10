package com.tom.service.knowledges.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :input, '%')) " +
		       "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :input, '%'))")
	List<User> findByUsernameOrEmailContainingIgnoreCase(@Param("input") String input);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username OR u.email = :email")
    boolean existsByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
	@Modifying
	@Transactional
	void deleteByUsername(String username);
	
    @Modifying
    @Transactional
    void deleteByEmail(String email);
	
}

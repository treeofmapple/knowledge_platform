package com.tom.service.knowledges.security;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	@Query(value = """
			select t from Token t inner join User u\s
			on t.user.id = u.id\s
			where u.id = :id and (t.expired = false or t.revoked = false)\s
			""")
	List<Token> findAllValidTokenByUser(UUID id);
	
	@Query(value = """
			select t from Token t where t.user.id = :id
			order by t.id desc
			limit 1
		""")
	Optional<Token> findLatestTokenByUserId(UUID id);
	
	Optional<Token> findByToken(String token);
	
}

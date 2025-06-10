package com.tom.service.knowledges.tag;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByNameIgnoreCase(String name);
	
	Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
	boolean existsByNameIgnoreCase(String name);
	
}

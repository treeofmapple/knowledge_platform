package com.tom.service.knowledges.notes;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

	Optional<Note> findByName(String name);
	
	Page<Note> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
	boolean existsByName(String name);
	
}

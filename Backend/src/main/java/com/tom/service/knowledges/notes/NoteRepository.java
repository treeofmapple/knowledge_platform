package com.tom.service.knowledges.notes;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

	Optional<Note> findByName(String name);
	
    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.image LEFT JOIN FETCH n.attachments WHERE n.user.id = :userId OR n.notePrivated = true")
    Page<Note> findAllAccessibleNotes(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.image LEFT JOIN FETCH n.attachments WHERE (n.user.id = :userId OR n.notePrivated = true) AND lower(n.name) LIKE lower(concat('%', :name, '%'))")
    Page<Note> findByNameContainingIgnoreCaseAndAccessible(@Param("name") String name, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT n FROM Note n JOIN n.tags t WHERE UPPER(t.name) LIKE UPPER(CONCAT('%', :tagName, '%')) AND (n.notePrivated = FALSE OR (n.notePrivated = TRUE AND n.user.id = :userId))")
    Page<Note> findByTags_NameContainingIgnoreCaseAndAccessible(@Param("tagName") String tagName, @Param("userId") UUID userId, Pageable pageable);

	Page<Note> findByNotePrivatedIsTrue(Pageable pageable);
	
	Page<Note> findByNameContainingIgnoreCaseAndNotePrivatedIsTrue(String name, Pageable pageable);
	
	Page<Note> findByTags_NameContainingIgnoreCaseAndNotePrivatedIsTrue(String tagName, Pageable pageable);
    
	boolean existsByName(String name);
	
}

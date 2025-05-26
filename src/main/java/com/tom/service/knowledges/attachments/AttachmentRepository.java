package com.tom.service.knowledges.attachments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository	
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	// Page<Attachment> findByArchivated(boolean archivated, Pageable pageable); @Me -- User
	// Page<Attachment> findByContentType(String contentType, Pageable pageable); @Me -- User
	Page<Attachment> findByNameContainingIgnoreCase(String name, Pageable pageable);

	boolean existsByName(String name);
	
	@Modifying
	@Transactional
	void deleteByName(String name);
	
}

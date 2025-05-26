package com.tom.service.knowledges.attachments;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository	
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	Page<Attachment> findByArchivated(boolean archivated, Pageable pageable);
	Page<Attachment> findByContentType(String contentType, Pageable pageable);
	Page<Attachment> findByNameContainingIgnoreCase(String name, Pageable pageable);
	Page<Attachment> findByArchivatedFalseOrderByAccessCountDesc(Pageable pageable);

	List<Attachment> findByArchivated(boolean archivated);
	List<Attachment> findByContentType(String contentType);
	
	boolean existsByName(String name);
	
	Optional<Attachment> findByNameContainingIgnoreCase(String name);
	
	@Modifying
	@Transactional
	void deleteByName(String name);
	
}

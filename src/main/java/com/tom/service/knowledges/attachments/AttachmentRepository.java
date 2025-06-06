package com.tom.service.knowledges.attachments;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository	
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

	Optional<Attachment> findByName(String name);
	
	boolean existsByName(String name);
	
}

package com.tom.service.knowledges.image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;

public interface ImageRepository extends JpaRepository<Image, Long> {

	boolean existsByName(String name);
	
	Optional<Image> findByName(String name);
	
	@Modifying
	@Transactional
	void deleteByName(String name);
	
}

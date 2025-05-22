package com.tom.service.knowledges.imagestag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;

public interface ImageTagRepository extends JpaRepository<ImageTag, Integer> {

	List<ImageTag> findByCategory(String name);
	
	List<ImageTag> findBySubCategory(String name);
	
	boolean existsByCategory(String name);
	
	boolean existsBySubCategory(String name);
	
	@Modifying
	@Transactional
	void deleteByCategory(String name);
	
	@Modifying
	@Transactional
	void deleteBySubCategory(String name);
	
}

package com.tom.service.knowledges.imagestag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.tom.aws.awstest.image.Image;

import jakarta.transaction.Transactional;

public interface ImageTagRepository extends JpaRepository<ImageTag, Integer> {

	List<Image> findByCategory(String name);
	
	List<Image> findBySubCategory(String name);
	
	boolean existsByCategory(String name);
	
	boolean existsBySubCategory(String name);
	
	@Modifying
	@Transactional
	void deleteByCategory(String name);
	
	@Modifying
	@Transactional
	void deleteBySubCategory(String name);
	
}

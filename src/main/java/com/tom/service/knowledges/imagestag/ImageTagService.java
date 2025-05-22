package com.tom.service.knowledges.imagestag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tom.aws.awstest.common.DataMerger;
import com.tom.aws.awstest.common.ServiceLogger;
import com.tom.aws.awstest.common.SystemUtils;
import com.tom.aws.awstest.image.ImageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageTagService {

	private final ImageTagRepository repository;
	private final ImageMapper mapper;
	private final DataMerger merger;
	private final SystemUtils utils;
	
	public List<ImageTagResponse> searchAllTags() {
		String userIp = utils.getUserIp();
        ServiceLogger.info("User {} is searching all tags.", userIp);
        List<ImageTag> itemTag = repository.findAll();
        
        
	}

	public List<ImageTagResponse> searchCategory(String name) {
		String userIp = utils.getUserIp();
        ServiceLogger.info("User {} is searching category: {}", userIp, name);
        
        
		return null;
	}

	public List<ImageTagResponse> searchSubcategory(String name) {

		return null;
	}

	public ItemTagResponse createCategory(String name) {

		return null;
	}

	public ItemTagResponse createSubcategory(String name) {

		return null;
	}

	public ItemTagResponse createCategorySubcategory(String category, String subcategory) {

		return null;
	}

	public ItemTagResponse editCategory(String category, String newName) {

		return null;
	}

	public ItemTagResponse deleteCategory(String name) {

		return null;
	}

	public ItemTagResponse deleteSubcategory(String name) {

		return null;
	}
	
}

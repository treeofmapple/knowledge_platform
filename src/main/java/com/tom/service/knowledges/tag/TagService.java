package com.tom.service.knowledges.tag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	@Value("{application.page.size}:10")
	private int PAGE_SIZE;
	
	private final TagRepository repository;
	private final TagMapper mapper;
	private final SystemUtils utils;
	private final TagUtils tagUtils;
	
	public TagPageResponse findAll(int page) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is fetching all tags", userIp);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Tag> tagPage = repository.findAll(pageable);
		return mapper.toTagPageResponse(tagPage);
	}
	
	public TagPageResponse findByName(String name, int page) { 
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is fetching all tags by name {}", userIp, name);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Tag> tagPage = repository.findByNameContainingIgnoreCase(name, pageable);
		return mapper.toTagPageResponse(tagPage);
	}
	
	@Transactional
	public TagResponse createTag(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is creating an tag with name: {}", userIp, name);
		
		String trimName = name.trim();
		tagUtils.ensureTagCanBeCreated(trimName);
		
		var newTag = mapper.build(trimName.toLowerCase());
		var savedTag =repository.save(newTag);
		
		ServiceLogger.info("Tag created successfully with ID: {} ", savedTag.getId());
		return mapper.toResponse(savedTag);
	}
	
	@Transactional
	public TagResponse renameTagByName(String currentName, String newName) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is renaming an tag with name: {}", userIp, currentName);

		String trimmedNewName = newName.trim();
        var tagToUpdate = tagUtils.ensureTagExistsAndGet(currentName);		

        tagUtils.checkIfBothExist(trimmedNewName, tagToUpdate);
		tagUtils.mergeData(tagToUpdate, trimmedNewName);

		var updatedTag = repository.save(tagToUpdate);
        ServiceLogger.info("Tag renamed successfully {} to {}", currentName, trimmedNewName);
		return mapper.toResponse(updatedTag);
	}
	
	@Transactional
	public void deleteTagByName(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is deleting an tag with name: {} ", userIp, name);
		
		var toRemove = tagUtils.ensureTagExistsAndGet(name.trim().toLowerCase());
		repository.delete(toRemove);
		
		ServiceLogger.info("Tag {}, was deleted", name);
	}
	
}

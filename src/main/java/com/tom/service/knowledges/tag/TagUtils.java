package com.tom.service.knowledges.tag;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagUtils {

	private final TagRepository repository;
	
    public Set<Tag> findOrCreateTagsByNames(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Tag> existingTags = repository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                                                   .map(Tag::getName)
                                                   .collect(Collectors.toSet());

        Set<Tag> newTagsToCreate = tagNames.stream()
            .filter(name -> !existingTagNames.contains(name.trim().toLowerCase()))
            .map(name -> {
                Tag newTag = new Tag();
                newTag.setName(name.trim().toLowerCase());
                return newTag;
            })
            .collect(Collectors.toSet());

        if (!newTagsToCreate.isEmpty()) {
            repository.saveAll(newTagsToCreate);
        }

        Set<Tag> allTags = new HashSet<>(existingTags);
        allTags.addAll(newTagsToCreate);
        return allTags;
    }
	
    public Tag ensureTagExistsAndGet(String name) {
        return repository.findByNameIgnoreCase(name).orElseThrow(() -> {
            String message = "Tag with name: '" + name + "' not found.";
            ServiceLogger.warn(message);
            return new NotFoundException(message);
        });
    }
	
	public void checkIfTagAlreadyExists(String name) {
		if(repository.existsByNameIgnoreCase(name)) {
			String message = "Tag already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
	public void checkIfTagIsSame(Tag currentName, String newName) {
		repository.findByNameIgnoreCase(newName).ifPresent(existent -> {
			if(!existent.getId().equals(currentName.getId())) {
                throw new ConflictException("Tag name '" + newName + "' is already in use.");
            }
		});
		
	}
	
	
}

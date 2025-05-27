package com.tom.service.knowledges.tag;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagUtils {

	private final TagRepository repository;
	
    public Tag ensureTagExistsAndGet(String name) {
        return repository.findByNameIgnoreCase(name).orElseThrow(() -> {
            String message = "Tag with name: '" + name + "' not found.";
            ServiceLogger.warn(message);
            return new NotFoundException(message);
        });
    }
    
    public void checkIfBothExist(String first, Tag second) {
        repository.findByNameIgnoreCase(first).ifPresent(existingTag -> {
            if (!existingTag.getId().equals(second.getId())) {
                throw new RuntimeException("Tag name '" + first + "' already exists.");
            }
        });
    }
	
	public void ensureTagCanBeCreated(String name) {
		if(repository.existsByNameIgnoreCase(name)) {
			String message = "Tag already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
	public void ensureTagDoesNotExist(String name) {
		if(!repository.existsByNameIgnoreCase(name)) {
			String message = "Tag with name: " + name + "not exists.";
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
	public void mergeData(Tag tags, String name) {
		tags.setName(name);
	}

}

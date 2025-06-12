package com.tom.service.knowledges.image;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageUtils {

	private final ImageRepository repository;
	
    public Image findImageById(Long id) {
        if (id == null) {
            return null;
        }
        return repository.findById(id).orElse(null);
    }
	
}

package com.tom.service.knowledges.image;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.common.AwsFunctions;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final AwsFunctions functions;
	private final ImageRepository repository;
	private final ImageMapper mapper;
	private final SystemUtils utils;
	private final ImageUtils repoCall;
	
	@Transactional
	public ImageResponse uploadImage(MultipartFile file) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);
		
		String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		repoCall.checkIfImageAlreadyExists(file.getOriginalFilename());

		functions.putObject(key, file);
		String s3Url = functions.buildS3Url(key);

		var image = new Image();
		mapper.mergeFromMultipartFile(image, file, key, s3Url);
	    repository.save(image);

	    return mapper.toResponse(image);
	}
	
	@Transactional
	public void removeImageFromNote(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);

		var images = repoCall.ensureImageExistsAndGet(name);
		functions.deleteObject(images);
        repository.delete(images);

        ServiceLogger.info("Successfully deleted image with name: {}", name);
	}
	
}

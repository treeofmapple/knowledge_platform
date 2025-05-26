package com.tom.service.knowledges.attachments;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tom.service.knowledges.common.AwsFunctions;
import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
	
	// check if something exists on the database then don't allow it to upload it...
	
	private final AwsFunctions functions;
	private final AttachmentRepository repository;
	private final AttachmentMapper mapper;
	private final SystemUtils utils;
	private final AttachmentUtils repoCall;
	private final int PAGE_SIZE = 20; 
	
	// Upgrade based on the image repository

	public List<AttachmentResponse> findAll() {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);
		return repoCall.findObjectList().stream().map(mapper::fromImage).collect(Collectors.toList());
	}
	
	public AttachmentPageResponse findAll(int page){
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Attachment> imagePage = repository.findAll(pageable);
		return mapper.fromPage(repoCall.findObjectListPaged(imagePage), imagePage.getTotalPages(), imagePage.getSize(), imagePage.getTotalPages());
	}
	
	public AttachmentResponse findObjectByName(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for object by name: {}", userIp, name);
		var image = repoCall.findObject(name);
		return mapper.fromImage(image);
	}
	
	@Transactional
	public AttachmentResponse uploadObject(MultipartFile file) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an object", userIp);
		String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
	    
		repoCall.ensureObjectExist(file.getOriginalFilename());
		
		functions.putObject(key, file);
		
		String s3Url = functions.buildS3Url(key);
		
		var image = new Attachment();
	    repoCall.mergeData(image, file.getOriginalFilename(), key, s3Url, file.getContentType(), file.getSize());
	    repository.save(image);
	    return mapper.fromImage(image);
	}
	
	@Transactional
	public byte[] downloadObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is downloading image with name: {}", userIp, name);

		repoCall.ensureObjectNotExist(name);
		Attachment images = repoCall.findObject(name);
		return functions.objectAsBytes(images);
	}
	
	// tweak to upload error per item
	@Transactional
	public List<AttachmentResponse> uploadMultiple(MultipartFile[] files) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is uploading an image", userIp);
	    List<AttachmentResponse> responses = new ArrayList<>();

	    for (MultipartFile file : files) {
	        String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
	        functions.putObject(key, file);
	        String s3Url = functions.buildS3Url(key);
            var image = new Attachment();
            repoCall.mergeData(image, file.getOriginalFilename(), key, s3Url, file.getContentType(), file.getSize());
            repository.save(image);

            responses.add(mapper.fromImage(image));

	    }

	    return responses;
	}

	// tweak to download error per item
	@Transactional
	public byte[][] downloadMultiple(List<String> names) {
	    String userIp = utils.getUserIp();
	    ServiceLogger.info("IP {} is downloading multiple objects: {}", userIp, names);

	    byte[][] result = new byte[names.size()][];

	    for (int i = 0; i < names.size(); i++) {
	        String name = names.get(i);
			Attachment images = repoCall.findObject(name);
	        
	        result[i] = functions.objectAsBytes(images);
	    }

	    return result;
	}
	
	@Transactional
	public String deleteObject(String name) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is searching for all objects", userIp);
		Attachment images = repoCall.findObject(name);
	
		repoCall.ensureObjectNotExist(name);
		functions.deleteObject(images);
        repository.delete(images);
        ServiceLogger.info("Successfully deleted image with name: {}", name);
        return "Image deleted successfully";
	}
	
	@Transactional
	public AttachmentResponse renameObject(String name , String newName) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is renaming object with name: {} to: {}", userIp, name, newName);
		repoCall.ensureObjectExist(name);
		Attachment images = repoCall.findObject(name);
		
		String newKey = functions.renameObject(images, newName);
		String newUrl = functions.buildS3Url(newKey);

		repoCall.mergeData(images, newKey, newUrl);
	    repository.save(images);
		
		return mapper.fromImage(images);
	}
	
}

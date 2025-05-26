package com.tom.service.knowledges.attachments;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class AttachmentController {

	private final AttachmentService service;
	
	@GetMapping(value = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AttachmentResponse>> searchAllObjects() {
		var response = service.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AttachmentResponse> searchObjectByName(@RequestParam("name") String image) {
		var response = service.findObjectByName(image);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(value = "/upload",
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AttachmentResponse> uploadObject(MultipartFile file) {
		var response = service.uploadObject(file);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	@PostMapping(value = "/download", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> downloadObject(@RequestParam("name") String images) {
		var response = service.downloadObject(images);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@PutMapping(value = "/rename", 
			produces = MediaType.APPLICATION_JSON_VALUE
			)
	public ResponseEntity<AttachmentResponse> renameObject(@RequestParam("name") String images, String rename) {
		var response = service.renameObject(images, rename);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	@DeleteMapping(value = "/delete")
	public ResponseEntity<String> deleteObject(@RequestParam("name") String images) {
		var response = service.deleteObject(images);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
	}
}

package com.tom.service.knowledges.attachments;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/attachment")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class AttachmentController {

	private final AttachmentService service;
	
	@PostMapping(value = "/upload",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AttachmentResponse> uploadObjectToNote(MultipartFile file) {
		var response = service.uploadObject(file);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	@PostMapping(value = "/download", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> downloadObjectFromNote(@RequestParam("name") String images) {
		var response = service.downloadObject(images);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteObjectFromNote(@RequestParam("name") String images) {
		service.deleteObject(images);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}

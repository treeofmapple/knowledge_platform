package com.tom.service.knowledges.image;

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
@RequestMapping("/v1/image")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService service;
	
	@PostMapping(value = "/upload",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ImageResponse> uploadImageToNote(MultipartFile file) {
		var response = service.uploadImage(file);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@DeleteMapping(value = "/remove")
	public ResponseEntity<Void> removeImagefromNote(@RequestParam("name") String imageName) {
		service.removeImageFromNote(imageName);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
}

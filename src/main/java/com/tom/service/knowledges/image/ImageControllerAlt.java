package com.tom.service.knowledges.image;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageControllerAlt {

	private final ImageService service;
	
	@PostMapping(value = "/upload/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ImageResponse>> uploadMultiple(@RequestParam MultipartFile[] files) {
		var response = service.uploadMultiple(files);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@PostMapping(value = "/download/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<byte[]>> downloadMultiple(@RequestBody List<String> names) {
	    byte[][] files = service.downloadMultiple(names);
	    return ResponseEntity.status(HttpStatus.ACCEPTED).body(Arrays.asList(files));
	}

}

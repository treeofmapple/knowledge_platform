package com.tom.service.knowledges.common;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.image.Image;

@Service
public class ЕntityUpdater {

	public void mergeData(Image images, String name, String key, String url, String contentType, long size) {
		images.setName(name);
		images.setDescription("No description");
		images.setObjectKey(key);
		images.setObjectUrl(url);
		images.setSize(size);
		images.setContentType(contentType);
	}	
	
	public void mergeData(Image images, String key, String url) {
		images.setObjectKey(key);
		images.setObjectUrl(url);
	}
	

	
	

	
	
	
	
	
}

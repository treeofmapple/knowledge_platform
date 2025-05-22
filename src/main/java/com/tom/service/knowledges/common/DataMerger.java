package com.tom.service.knowledges.common;
import org.springframework.stereotype.Service;

import com.tom.aws.awstest.image.Image;
import com.tom.aws.awstest.product.Product;
import com.tom.aws.awstest.product.ProductRequest;

@Service
public class DataMerger {

	public void mergeData(Product product, ProductRequest request) {
		product.setName(request.name());
		product.setPrice(request.price());
		product.setQuantity(request.quantity());
		product.setManufacturer(request.manufacturer());
	}
	
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

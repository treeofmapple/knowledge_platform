package com.tom.tool.knowledge.common;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tom.aws.awstest.product.Product;
import com.tom.aws.awstest.product.ProductRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemUtils {

	public void mergeData(Product product, ProductRequest request) {
		product.setName(request.name());
		product.setPrice(request.price());
		product.setQuantity(request.quantity());
		product.setManufacturer(request.manufacturer());
	}
	
	public String getUserIp() {
	    var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	        .getRequest();
	    return request.getRemoteAddr();
	}
}

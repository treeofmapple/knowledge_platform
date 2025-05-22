package com.tom.service.knowledges.common;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemUtils {

	public String getUserIp() {
	    var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	        .getRequest();
	    return request.getRemoteAddr();
	}
	
}	

package com.tom.service.knowledges.exception;

import com.tom.service.knowledges.exception.Global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends CustomGlobalException {
	
    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

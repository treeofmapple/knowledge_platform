package com.tom.web.knowledge.exception.Global;

@SuppressWarnings("serial")
public abstract class DateGlobalException extends NumberFormatException {
	
	private final Throwable cause;
	
	public DateGlobalException(String msg) {
        super(msg);
        this.cause = null;
    }

    public DateGlobalException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }
    
    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}

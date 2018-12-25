package com.sjms.exception;

public class SchedulingParserException extends RuntimeException {
	public SchedulingParserException() {
		super();
	}

	public SchedulingParserException(String message) {
		super(message);
	}

	public SchedulingParserException(String message, Throwable t) {
		super(message, t);
	}
}

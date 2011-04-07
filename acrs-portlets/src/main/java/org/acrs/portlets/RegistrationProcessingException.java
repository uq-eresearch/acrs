package org.acrs.portlets;

public class RegistrationProcessingException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistrationProcessingException() {
	}

	public RegistrationProcessingException(String message) {
		super(message);
	}

	public RegistrationProcessingException(Throwable cause) {
		super(cause);
	}

	public RegistrationProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

}

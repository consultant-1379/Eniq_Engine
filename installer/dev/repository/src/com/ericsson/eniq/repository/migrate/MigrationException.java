package com.ericsson.eniq.repository.migrate;

public class MigrationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MigrationException(String message, Exception e) {
		super(message, e);
	}

	public MigrationException(Throwable cause) {
		super(cause);
	}

	public MigrationException(String message) {
		super(message);
	}

}

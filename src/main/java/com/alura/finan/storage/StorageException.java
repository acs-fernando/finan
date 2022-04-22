package com.alura.finan.storage;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 5604883028344214512L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}

package com.alura.finan.storage;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = 1198294590049047883L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

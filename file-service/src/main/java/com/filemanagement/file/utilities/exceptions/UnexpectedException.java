package com.filemanagement.file.utilities.exceptions;

/**
 * Exception thrown when an unexpected error occurs.
 *
 * @author Ömer Asaf BALIKÇI
 */

public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }
}

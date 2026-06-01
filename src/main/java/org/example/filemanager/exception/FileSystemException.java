package org.example.filemanager.exception;

/**
 * Исключение при недопустимой операции с элементами файловой системы.
 */
public class FileSystemException extends RuntimeException {

    public FileSystemException(String message) {
        super(message);
    }

    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

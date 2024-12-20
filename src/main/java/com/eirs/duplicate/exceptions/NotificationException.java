package com.eirs.duplicate.exceptions;

public class NotificationException extends RuntimeException {
    public NotificationException() {
        super();
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }
}

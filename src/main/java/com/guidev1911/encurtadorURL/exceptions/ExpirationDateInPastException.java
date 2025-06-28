package com.guidev1911.encurtadorURL.exceptions;

public class ExpirationDateInPastException extends RuntimeException {

    public ExpirationDateInPastException(String message) {
        super(message);
    }

    public ExpirationDateInPastException(String message, Throwable cause) {
        super(message, cause);
    }
}

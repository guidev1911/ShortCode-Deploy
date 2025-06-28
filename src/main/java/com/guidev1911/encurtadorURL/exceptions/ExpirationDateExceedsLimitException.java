package com.guidev1911.encurtadorURL.exceptions;

public class ExpirationDateExceedsLimitException extends RuntimeException {

    public ExpirationDateExceedsLimitException(String message) {
        super(message);
    }

    public ExpirationDateExceedsLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}

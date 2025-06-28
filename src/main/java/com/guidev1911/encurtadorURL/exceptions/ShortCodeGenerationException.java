package com.guidev1911.encurtadorURL.exceptions;

public class ShortCodeGenerationException extends RuntimeException {

    public ShortCodeGenerationException(String message) {
        super(message);
    }

    public ShortCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

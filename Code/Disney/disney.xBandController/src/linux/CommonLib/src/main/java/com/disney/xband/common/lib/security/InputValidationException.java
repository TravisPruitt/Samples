package com.disney.xband.common.lib.security;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/1/12
 * Time: 3:44 PM
 */
public class InputValidationException extends RuntimeException {
    public InputValidationException() {
        super();
    }

    public InputValidationException(final String message) {
        super(message);
    }

    public InputValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InputValidationException(final Throwable cause) {
        super(cause);
    }
}

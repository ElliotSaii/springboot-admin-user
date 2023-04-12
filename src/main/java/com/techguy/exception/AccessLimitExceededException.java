package com.techguy.exception;

public class AccessLimitExceededException extends Throwable {
    public AccessLimitExceededException() {
    }

    public AccessLimitExceededException(String message) {
        super(message);
    }

    public AccessLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessLimitExceededException(Throwable cause) {
        super(cause);
    }
}

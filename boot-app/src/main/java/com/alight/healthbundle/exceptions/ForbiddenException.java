package com.alight.healthbundle.exceptions;

public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String aMessage) {
        super(aMessage);
    }

    public ForbiddenException(Throwable aCause) {
        super(aCause);
    }

    public ForbiddenException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}

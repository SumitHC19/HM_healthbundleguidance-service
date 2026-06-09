package com.alight.healthbundle.exceptions;

public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String aMessage) {
        super(aMessage);
    }

    public UnauthorizedException(Throwable aCause) {
        super(aCause);
    }

    public UnauthorizedException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}

package com.alight.healthbundle.exceptions;

public class NoObjectFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoObjectFoundException() {
        super();
    }

    public NoObjectFoundException(String aMessage) {
        super(aMessage);
    }

    public NoObjectFoundException(Throwable aCause) {
        super(aCause);
    }

    public NoObjectFoundException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }
}

package com.alight.healthbundle.exceptions;

public class VersionConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VersionConflictException() {
        super();
    }

    public VersionConflictException(String aMessage) {
        super(aMessage);
    }

    public VersionConflictException(Throwable aCause) {
        super(aCause);
    }

    public VersionConflictException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}

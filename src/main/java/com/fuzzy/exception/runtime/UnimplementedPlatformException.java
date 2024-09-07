package com.fuzzy.exception.runtime;

public class UnimplementedPlatformException extends IllegalArgumentException {

    public UnimplementedPlatformException(String methodName) {
        super("Unimplemented platform dependent method: " + methodName);
    }
}

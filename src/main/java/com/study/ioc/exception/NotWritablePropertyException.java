package com.study.ioc.exception;

public class NotWritablePropertyException extends RuntimeException{

    public NotWritablePropertyException (String message, Throwable cause) {
        super(message, cause);
    }

}

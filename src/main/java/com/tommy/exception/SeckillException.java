package com.tommy.exception;

/**
 * 所有的秒杀业务相关的异常的父异常
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}

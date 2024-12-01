package com.javaoffers.brief.modelhelper.exception;

/**
 * @Description: 停止执行异常，也是可忽略的异常
 * @Auther: create by cmj on 2022/6/11 23:11
 */
public class StopExecException extends RuntimeException {
    public StopExecException(String message) {
        super(message);
    }
}

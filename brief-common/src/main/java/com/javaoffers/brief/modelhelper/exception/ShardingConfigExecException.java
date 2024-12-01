package com.javaoffers.brief.modelhelper.exception;

/**
 * @Description: 分表配置错误
 * @Auther: create by cmj on 2022/6/11 23:11
 */
public class ShardingConfigExecException extends RuntimeException {
    public ShardingConfigExecException(String message) {
        super(message);
    }
}

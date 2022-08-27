package com.javaoffers.batis.modelhelper.fun.crud;

import com.javaoffers.batis.modelhelper.fun.ExecutFun;

/**
 * @Description: limit sql .
 * @Auther: create by cmj on 2022/6/11 17:26
 */
public interface LimitFun<M, R extends LimitFun<M,?>> extends ExecutFun<M> {

    /**
     * 分页
     * @param pageNum 第几页
     * @param size 每页条数
     * @return 数据
     */
    R limitPage(int pageNum, int size);

}

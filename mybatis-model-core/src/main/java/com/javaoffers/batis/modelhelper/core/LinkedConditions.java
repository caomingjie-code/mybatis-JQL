package com.javaoffers.batis.modelhelper.core;

import com.javaoffers.batis.modelhelper.fun.Condition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * create by cmj on 2022-06-20 2:50:07
 */
public class LinkedConditions<T> extends LinkedList<T>{

    private List<BiConsumer<T,T>> beforeAddProcess  = new LinkedList<>();

    public boolean add(T condition){
        beforeAddProcess.forEach(biConsumer -> {
            biConsumer.accept(this.peekLast(), condition);
        });
        return super.add(condition);
    }

    public boolean addAll(Collection<? extends T> c){
        c.forEach(a->{
            add(a);
        });
        return true;
    }

    public void beforeAdd(BiConsumer<T,T> biConsumer){
        beforeAddProcess.add(biConsumer);
    }


}

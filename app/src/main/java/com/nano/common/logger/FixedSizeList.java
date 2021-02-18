package com.nano.common.logger;

import java.util.LinkedList;

/**
 * 固定长度链表
 *
 * @author nano
 */
public class FixedSizeList<T> extends LinkedList<T> {

    // 定义缓存的容量
    private int capacity;

    public FixedSizeList(int capacity) {
        super();
        this.capacity = capacity;
    }

    /**
     * 添加方法
     *
     * @param e 元素
     * @return 是否成功
     */
    @Override
    public boolean add(T e) {
        // 超过长度，移除最后一个
        if (size() + 1 > capacity) {
            super.removeFirst();
        }
        return super.add(e);
    }
}

package com.joysky.demo.encryt.core;


import java.io.Serializable;
import java.util.List;


public  class  PageR <T> implements Serializable {

    private  long total  ;

    private List<T> data ;

    public PageR() {
    }

    public PageR(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

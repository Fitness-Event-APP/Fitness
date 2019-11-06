package com.example.fitnessevent.db;


import com.example.fitnessevent.helper.DateUtils;

public class DbItem<T> {

    private T object;
    private long date;

    public DbItem(T object, long date) {
        this.object = object;
        this.date = date;
    }

    public T getObject() {
        return object;
    }

    public boolean isExpired(long ttl) {
        int seconds = DateUtils.diffSeconds(date); // the difference between date and currentTimeMillis
        return seconds > ttl;
    }
}

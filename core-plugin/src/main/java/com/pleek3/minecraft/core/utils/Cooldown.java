package com.pleek3.minecraft.core.utils;

public class Cooldown {

    private final long startTime;
    private final long expire;

    public Cooldown(long startTime, long duration) {
        this.startTime = startTime;
        this.expire = startTime + duration;
    }

    public Cooldown(long duration) {
        this(System.currentTimeMillis(), duration);
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - expire >= 0;
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.startTime;
    }

}

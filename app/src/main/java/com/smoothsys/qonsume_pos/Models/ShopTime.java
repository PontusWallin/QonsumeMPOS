package com.smoothsys.qonsume_pos.Models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopTime implements Comparable<ShopTime> {

    public ShopTime(String id, String weekday, String open, String close) {
        this.id = id;
        this.weekday = weekday;
        this.open = open;
        this.close = close;
    }

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("weekday")
    @Expose
    private String weekday;
    // @SerializedName("open")
    @Expose
    private String open;
    // @SerializedName("close")
    @Expose
    private String close;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    @Override
    public int compareTo(@NonNull ShopTime o) {
        int myWeekDay = Integer.parseInt(this.getWeekday());
        int hisWeekDay = Integer.parseInt(o.getWeekday());
        return myWeekDay - hisWeekDay;
    }
}


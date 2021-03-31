package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pontu on 2018-03-20.
 */

public class Table {

    public Table(int tableNo, int orderNo) {
        this.tableNo = tableNo;
        this.orderNo = orderNo;
    }

    // @SerializedName("TableNo")
    @Expose
    private int tableNo;

    // @SerializedName("OrderNo")
    @Expose
    private int orderNo;

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int n) {
        tableNo = n;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int n) {
        orderNo = n;
    }

}

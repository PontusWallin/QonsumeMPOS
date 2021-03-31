package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pontus on 2017-06-29.
 */

public class SubmissionStatusObject {

    // @SerializedName("status")
    @Expose
    private String status;

    // @SerializedName("data")
    @Expose
    private String data;

    public int getReceiptNo() {
        return receiptNo;
    }

    // @SerializedName("receipt_number")
    @Expose
    private int receiptNo;

    // @SerializedName("order_id")
    @Expose
    private int orderID;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getOrderID() { return orderID;}

    public void setOrderID(int orderID) { this.orderID = orderID; }
}
package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MerchantResponseObject {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Merchant data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Merchant getData() {
        return data;
    }

    public void setData(Merchant data) {
        this.data = data;
    }

}

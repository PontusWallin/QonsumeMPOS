
package com.smoothsys.qonsume_pos.Models.RestaurantClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleRestaurantResponseObject {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Restaurant data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Restaurant getData() {
        return data;
    }

    public void setData(Restaurant data) {
        this.data = data;
    }

}

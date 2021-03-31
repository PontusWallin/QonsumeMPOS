package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontu on 2017-06-28.
 */

public class orderSubmissionObject {

    @SerializedName("orders")
    @Expose
    private List<String> orders = new ArrayList<>();

    public List<String> getOrders() {
        return orders;
    }

    public void addOrders(String orders) {
        this.orders.add(orders);
    }

}

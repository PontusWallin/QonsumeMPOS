package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pontu on 2017-01-30.
 */

public class DailyPayment {
    // @SerializedName("PaymentSum")
    @Expose
    private Double paymentSum;
    // @SerializedName("Count")
    @Expose
    private Integer count;

    public Double getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(Double paymentSum) {
        this.paymentSum = paymentSum;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}

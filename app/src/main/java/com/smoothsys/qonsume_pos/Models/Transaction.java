package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pontu on 2017-01-28.
 */

public class Transaction {
    // @SerializedName("TransactionID")
    @Expose
    private Integer transactionID;
    // @SerializedName("Date")
    @Expose
    private String date;
    // @SerializedName("Amount")
    @Expose
    private Double amount;
    // @SerializedName("Remain")
    @Expose
    private Double remain;

    // @SerializedName("CustomerName")
    @Expose
    private String custName;

    // @SerializedName("TrxType")
    @Expose
    private int trxType;

    public void setTrxType(int t) {trxType = t;}

    public String getTrxType() {
        switch (trxType) {
            case 1:
                return "Payment";

            case 2:
                return "Cash In";

            case 3:
                return "Cash Out";

            case 4:
                return "Refund";

            case 5:
                return "Partial Refund";

            default:
                return "null transaction";
        }
    }


    public void setCustomerName(String c) {custName = c;}

    public String getCustomerName() {return custName;}

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRemain() {
        return remain;
    }

    public void setRemain(Double remain) {
        this.remain = remain;
    }


}


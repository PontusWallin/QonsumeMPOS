package com.smoothsys.qonsume_pos.Models.Message;

/**
 * Created by Pontu on 2017-08-28.
 */

public class OrderFinishedMsg {

    private int transaction_id;
    private int merchant_id;
    private String msg;

    public OrderFinishedMsg(int transaction_id, int merchant_id, String msg) {

        this.transaction_id = transaction_id;
        this.merchant_id = merchant_id;
        this.msg = msg;

    }

}

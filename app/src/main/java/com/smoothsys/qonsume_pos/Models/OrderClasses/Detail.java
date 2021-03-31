package com.smoothsys.qonsume_pos.Models.OrderClasses;

/**
 * Created by Pontu on 2017-05-11.
 */

public class Detail {

    public Detail(String item_name, String item_attribute) {
        this.item_name = item_name;
        this.item_attribute = item_attribute;
    }

    public Detail(String item_name, int qty, int price) {
        this.item_name = item_name;
        this.qty = qty;
        this.unit_price = price;
    }

    private int id;
    private int transaction_header_id;
    private int shop_id;
    private int item_id;
    private String item_name;
    private String item_attribute;
    private float unit_price;
    private int qty;
    private float discount_percent;
    private String added;
    private int item_state;

    public int getId() { return id;}
    public void setId(int i) {id= i;}

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_attribute() {
        return item_attribute;
    }

    public void setItem_attribute(String item_attribute) {
        this.item_attribute = item_attribute;
    }

    public float getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(int discount_percent) {
        this.discount_percent = discount_percent;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public int getTransaction_header_id(){ return transaction_header_id;}

    public void setTransaction_header_id(int id) {transaction_header_id = id;}

    public int getItem_state() {
        return item_state;
    }

    public void setItem_state(int item_state) {
        this.item_state = item_state;
    }
}

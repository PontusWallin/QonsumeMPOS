package com.smoothsys.qonsume_pos.Models;

/**
 * Created by Pontus on 2017-06-29.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class sendOrderObject {

    // @SerializedName("item_id")
    @Expose
    private String itemId;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("unit_price")
    @Expose
    private String unitPrice;
    // @SerializedName("discount_percent")
    @Expose
    private String discountPercent;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("qty")
    @Expose
    private Integer qty;
    // @SerializedName("user_id")
    @Expose
    private Integer userId;
    // @SerializedName("payment_trans_id")
    @Expose
    private String paymentTransId;
    // @SerializedName("delivery_address")
    @Expose
    private String deliveryAddress;
    // @SerializedName("billing_address")
    @Expose
    private String billingAddress;
    // @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    // @SerializedName("basket_item_attribute")
    @Expose
    private String basketItemAttribute;
    // @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    // @SerializedName("email")
    @Expose
    private String email;
    // @SerializedName("phone")
    @Expose
    private String phone;
    // @SerializedName("coupon_discount_amount")
    @Expose
    private String couponDiscountAmount;
    // @SerializedName("flat_rate_shipping")
    @Expose
    private String flatRateShipping;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPaymentTransId() {
        return paymentTransId;
    }

    public void setPaymentTransId(String paymentTransId) {
        this.paymentTransId = paymentTransId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBasketItemAttribute() {
        return basketItemAttribute;
    }

    public void setBasketItemAttribute(String basketItemAttribute) {
        this.basketItemAttribute = basketItemAttribute;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(String couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public String getFlatRateShipping() {
        return flatRateShipping;
    }

    public void setFlatRateShipping(String flatRateShipping) {
        this.flatRateShipping = flatRateShipping;
    }

}

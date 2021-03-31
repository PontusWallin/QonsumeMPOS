package com.smoothsys.qonsume_pos.Models.OrderClasses;

/**
 * Created by Pontu on 2017-05-11.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

  // @SerializedName("id")
  @Expose
  private String id;
  // @SerializedName("shop_id")
  @Expose
  private String shopId;
  // @SerializedName("user_id")
  @Expose
  private String userId;
  // @SerializedName("paypal_trans_id")
  @Expose
  private String paypalTransId;
  // @SerializedName("payment_date")
  @Expose
  private String paymentDate;
  // @SerializedName("payment_ref_number")
  @Expose
  private String transactionRefNo;

  // @SerializedName("receipt_number")
  @Expose
  private int receipt_number;

  public int getReceipt_number() {
    return receipt_number;
  }


  // @SerializedName("total_amount")
  @Expose
  private String totalAmount;
  // @SerializedName("delivery_address")
  @Expose
  private String deliveryAddress;
  // @SerializedName("billing_address")
  @Expose
  private String billingAddress;
  // @SerializedName("transaction_status")
  @Expose
  private String transactionStatus;
  // @SerializedName("email")
  @Expose
  private String email;
  // @SerializedName("phone")
  @Expose
  private String phone;
  // @SerializedName("payment_method")
  @Expose
  private String paymentMethod;
  // @SerializedName("coupon_discount_amount")
  @Expose
  private String couponDiscountAmount;
  // @SerializedName("flat_rate_shipping")
  @Expose
  private String flatRateShipping;
  // @SerializedName("added")
  @Expose
  private String added;

  public int getTableNo() {
    return tableNo;
  }

  public void setTableNo(int tableNo) {
    this.tableNo = tableNo;
  }

  // @SerializedName("table_number")
  @Expose
  private int tableNo;
  // @SerializedName("currency_symbol")
  @Expose
  private String currencySymbol;
  // @SerializedName("currency_short_form")
  @Expose
  private String currencyShortForm;
  // @SerializedName("details")
  @Expose
  private List<Detail> details = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getPaypalTransId() {
    return paypalTransId;
  }

  public void setPaypalTransId(String paypalTransId) {
    this.paypalTransId = paypalTransId;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
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

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
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

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
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

  public String getAdded() {
    return added;
  }

  public void setAdded(String added) {
    this.added = added;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public String getCurrencyShortForm() {
    return currencyShortForm;
  }

  public void setCurrencyShortForm(String currencyShortForm) {
    this.currencyShortForm = currencyShortForm;
  }

  public List<Detail> getDetails() {
    return details;
  }

  public void setDetails(List<Detail> details) {
    this.details = details;
  }

  public String getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(String paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getTransactionRefNo() {
    return transactionRefNo;
  }

  public void setTransactionRefNo(String transactionRefNo) {
    this.transactionRefNo = transactionRefNo;
  }
}

package com.smoothsys.qonsume_pos.Models.RestaurantClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.smoothsys.qonsume_pos.Models.ShopHoliday;
import com.smoothsys.qonsume_pos.Models.ShopTime;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("description")
    @Expose
    private String description;
    // @SerializedName("phone")
    @Expose
    private String phone;
    // @SerializedName("email")
    @Expose
    private String email;
    // @SerializedName("address")
    @Expose
    private String address;
    // @SerializedName("lat")
    @Expose
    private String lat;
    // @SerializedName("lng")
    @Expose
    private String lng;
    // @SerializedName("paypal_email")
    @Expose
    private String paypalEmail;
    // @SerializedName("paypal_payment_type")
    @Expose
    private String paypalPaymentType;
    // @SerializedName("paypal_environment")
    @Expose
    private String paypalEnvironment;
    // @SerializedName("paypal_appid_live")
    @Expose
    private String paypalAppidLive;
    // @SerializedName("paypal_merchantname")
    @Expose
    private String paypalMerchantname;
    // @SerializedName("paypal_customerid")
    @Expose
    private String paypalCustomerid;
    // @SerializedName("paypal_ipnurl")
    @Expose
    private String paypalIpnurl;
    // @SerializedName("paypal_memo")
    @Expose
    private String paypalMemo;
    // @SerializedName("bank_account")
    @Expose
    private String bankAccount;
    // @SerializedName("bank_name")
    @Expose
    private String bankName;
    // @SerializedName("bank_code")
    @Expose
    private String bankCode;
    // @SerializedName("branch_code")
    @Expose
    private String branchCode;
    // @SerializedName("swift_code")
    @Expose
    private String swiftCode;
    // @SerializedName("cod_email")
    @Expose
    private String codEmail;
    // @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;
    // @SerializedName("stripe_secret_key")
    @Expose
    private String stripeSecretKey;
    // @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    // @SerializedName("currency_short_form")
    @Expose
    private String currencyShortForm;
    // @SerializedName("sender_email")
    @Expose
    private String senderEmail;
    // @SerializedName("flat_rate_shipping")
    @Expose
    private String flatRateShipping;
    // @SerializedName("keyword")
    @Expose
    private String keyword;
    // @SerializedName("added")
    @Expose
    private String added;
    // @SerializedName("status")
    @Expose
    private String status;
    // @SerializedName("paypal_enabled")
    @Expose
    private String paypalEnabled;
    // @SerializedName("stripe_enabled")
    @Expose
    private String stripeEnabled;
    // @SerializedName("cod_enabled")
    @Expose
    private String codEnabled;
    // @SerializedName("banktransfer_enabled")
    @Expose
    private String banktransferEnabled;
    // @SerializedName("distance")
    @Expose
    private String distance;
    // @SerializedName("item_count")
    @Expose
    private Integer itemCount;
    // @SerializedName("category_count")
    @Expose
    private Integer categoryCount;
    // @SerializedName("follow_count")
    @Expose
    private Integer followCount;
    // @SerializedName("cover_image_file")
    @Expose
    private String coverImageFile;
    // @SerializedName("cover_image_width")
    @Expose
    private String coverImageWidth;
    // @SerializedName("cover_image_height")
    @Expose
    private String coverImageHeight;
    // @SerializedName("cover_image_description")
    @Expose
    private String coverImageDescription;
    // @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    // @SerializedName("feeds")
    @Expose
    private List<Feed> feeds = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public String getPaypalPaymentType() {
        return paypalPaymentType;
    }

    public void setPaypalPaymentType(String paypalPaymentType) {
        this.paypalPaymentType = paypalPaymentType;
    }

    public String getPaypalEnvironment() {
        return paypalEnvironment;
    }

    public void setPaypalEnvironment(String paypalEnvironment) {
        this.paypalEnvironment = paypalEnvironment;
    }

    public String getPaypalAppidLive() {
        return paypalAppidLive;
    }

    public void setPaypalAppidLive(String paypalAppidLive) {
        this.paypalAppidLive = paypalAppidLive;
    }

    public String getPaypalMerchantname() {
        return paypalMerchantname;
    }

    public void setPaypalMerchantname(String paypalMerchantname) {
        this.paypalMerchantname = paypalMerchantname;
    }

    public String getPaypalCustomerid() {
        return paypalCustomerid;
    }

    public void setPaypalCustomerid(String paypalCustomerid) {
        this.paypalCustomerid = paypalCustomerid;
    }

    public String getPaypalIpnurl() {
        return paypalIpnurl;
    }

    public void setPaypalIpnurl(String paypalIpnurl) {
        this.paypalIpnurl = paypalIpnurl;
    }

    public String getPaypalMemo() {
        return paypalMemo;
    }

    public void setPaypalMemo(String paypalMemo) {
        this.paypalMemo = paypalMemo;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getCodEmail() {
        return codEmail;
    }

    public void setCodEmail(String codEmail) {
        this.codEmail = codEmail;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getStripeSecretKey() {
        return stripeSecretKey;
    }

    public void setStripeSecretKey(String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
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

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getFlatRateShipping() {
        return flatRateShipping;
    }

    public void setFlatRateShipping(String flatRateShipping) {
        this.flatRateShipping = flatRateShipping;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaypalEnabled() {
        return paypalEnabled;
    }

    public void setPaypalEnabled(String paypalEnabled) {
        this.paypalEnabled = paypalEnabled;
    }

    public String getStripeEnabled() {
        return stripeEnabled;
    }

    public void setStripeEnabled(String stripeEnabled) {
        this.stripeEnabled = stripeEnabled;
    }

    public String getCodEnabled() {
        return codEnabled;
    }

    public void setCodEnabled(String codEnabled) {
        this.codEnabled = codEnabled;
    }

    public String getBanktransferEnabled() {
        return banktransferEnabled;
    }

    public void setBanktransferEnabled(String banktransferEnabled) {
        this.banktransferEnabled = banktransferEnabled;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Integer categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public String getCoverImageFile() {
        return coverImageFile;
    }

    public void setCoverImageFile(String coverImageFile) {
        this.coverImageFile = coverImageFile;
    }

    public String getCoverImageWidth() {
        return coverImageWidth;
    }

    public void setCoverImageWidth(String coverImageWidth) {
        this.coverImageWidth = coverImageWidth;
    }

    public String getCoverImageHeight() {
        return coverImageHeight;
    }

    public void setCoverImageHeight(String coverImageHeight) {
        this.coverImageHeight = coverImageHeight;
    }

    public String getCoverImageDescription() {
        return coverImageDescription;
    }

    public void setCoverImageDescription(String coverImageDescription) {
        this.coverImageDescription = coverImageDescription;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public ArrayList<ShopTime> shoptimes;

    public ArrayList<ShopHoliday> shopholidays;
}

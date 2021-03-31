
package com.smoothsys.qonsume_pos.Models.ItemClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("item_id")
    @Expose
    private String itemId;
    // @SerializedName("appuser_id")
    @Expose
    private String appuserId;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("review")
    @Expose
    private String review;
    // @SerializedName("status")
    @Expose
    private String status;
    // @SerializedName("added")
    @Expose
    private String added;
    // @SerializedName("appuser_name")
    @Expose
    private String appuserName;
    // @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAppuserId() {
        return appuserId;
    }

    public void setAppuserId(String appuserId) {
        this.appuserId = appuserId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getAppuserName() {
        return appuserName;
    }

    public void setAppuserName(String appuserName) {
        this.appuserName = appuserName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

}

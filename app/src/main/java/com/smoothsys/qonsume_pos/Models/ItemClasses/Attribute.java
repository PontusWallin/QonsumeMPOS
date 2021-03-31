
package com.smoothsys.qonsume_pos.Models.ItemClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attribute {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("item_id")
    @Expose
    private String itemId;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("added")
    @Expose
    private String added;
    // @SerializedName("detailString")
    @Expose
    private String detailString;
    // @SerializedName("details")
    @Expose
    private List<Detail> details = null;

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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getDetailString() {
        return detailString;
    }

    public void setDetailString(String detailString) {
        this.detailString = detailString;
    }

    public List<com.smoothsys.qonsume_pos.Models.ItemClasses.Detail> getDetails() {
        return details;
    }

    public void setDetails(List<com.smoothsys.qonsume_pos.Models.ItemClasses.Detail> details) {
        this.details = details;
    }

}

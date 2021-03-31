
package com.smoothsys.qonsume_pos.Models.ItemClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// == THIS DETAIL IS OF TYPE ADD-ON DETAIL - NOT ORDER DETAIL ==
public class Detail {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("header_id")
    @Expose
    private String headerId;
    // @SerializedName("item_id")
    @Expose
    private String itemId;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("additional_price")
    @Expose
    private String additionalPrice;
    // @SerializedName("added")
    @Expose
    private String added;

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

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(String additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

}

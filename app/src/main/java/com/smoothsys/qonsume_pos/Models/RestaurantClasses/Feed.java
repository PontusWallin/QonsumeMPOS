
package com.smoothsys.qonsume_pos.Models.RestaurantClasses;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feed {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("title")
    @Expose
    private String title;
    // @SerializedName("description")
    @Expose
    private String description;
    // @SerializedName("is_published")
    @Expose
    private String isPublished;
    // @SerializedName("added")
    @Expose
    private String added;
    // @SerializedName("images")
    @Expose
    private List<Image> images = null;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(String isPublished) {
        this.isPublished = isPublished;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}

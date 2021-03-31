
package com.smoothsys.qonsume_pos.Models.RestaurantClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("is_published")
    @Expose
    private String isPublished;
    // @SerializedName("ordering")
    @Expose
    private String ordering;
    // @SerializedName("added")
    @Expose
    private String added;

    @Expose
    private String coverImageFile;
    // @SerializedName("cover_image_width")
    @Expose
    private String coverImageWidth;
    // @SerializedName("cover_image_height")
    @Expose
    private String coverImageHeight;
    // @SerializedName("sub_categories")
    @Expose
    private List<SubCategory> subCategories = null;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(String isPublished) {
        this.isPublished = isPublished;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
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

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

}

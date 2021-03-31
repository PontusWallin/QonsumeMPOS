
package com.smoothsys.qonsume_pos.Models.ItemClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {

    // @SerializedName("id")
    @Expose
    private String id;
    // @SerializedName("cat_id")
    @Expose
    private String catId;
    // @SerializedName("sub_cat_id")
    @Expose
    private String subCatId;
    // @SerializedName("shop_id")
    @Expose
    private String shopId;
    // @SerializedName("discount_type_id")
    @Expose
    private String discountTypeId;
    // @SerializedName("name")
    @Expose
    private String name;
    // @SerializedName("description")
    @Expose
    private String description;
    // @SerializedName("unit_price")
    @Expose
    private String unitPrice;
    // @SerializedName("search_tag")
    @Expose
    private String searchTag;
    // @SerializedName("is_published")
    @Expose
    private String isPublished;
    // @SerializedName("added")
    @Expose
    private String added;
    // @SerializedName("updated")
    @Expose
    private Object updated;
    // @SerializedName("images")
    @Expose
    private List<Image> images = null;
    // @SerializedName("like_count")
    @Expose
    private Integer likeCount;
    // @SerializedName("review_count")
    @Expose
    private Integer reviewCount;
    // @SerializedName("inquiries_count")
    @Expose
    private Integer inquiriesCount;
    // @SerializedName("touches_count")
    @Expose
    private Integer touchesCount;
    // @SerializedName("discount_name")
    @Expose
    private String discountName;
    // @SerializedName("discount_percent")
    @Expose
    private String discountPercent;
    // @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    // @SerializedName("currency_short_form")
    @Expose
    private String currencyShortForm;
    // @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;
    //@SerializedName("attributes")
    @Expose
    private List<Attribute> attributes = null;

    // @SerializedName("tax")
    @Expose
    private int tax;

    public float parseVAT() {

        if(tax == 0) {
            return 0;
        }
        float VAT = (float) (tax * 0.01);
        return VAT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getDiscountTypeId() {
        return discountTypeId;
    }

    public void setDiscountTypeId(String discountTypeId) {
        this.discountTypeId = discountTypeId;
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

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSearchTag() {
        return searchTag;
    }

    public void setSearchTag(String searchTag) {
        this.searchTag = searchTag;
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

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getInquiriesCount() {
        return inquiriesCount;
    }

    public void setInquiriesCount(Integer inquiriesCount) {
        this.inquiriesCount = inquiriesCount;
    }

    public Integer getTouchesCount() {
        return touchesCount;
    }

    public void setTouchesCount(Integer touchesCount) {
        this.touchesCount = touchesCount;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

}

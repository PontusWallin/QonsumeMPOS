package com.smoothsys.qonsume_pos.Models;

/**
 * Created by Pontu on 2017-01-24.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Customer {

    // @SerializedName("CustomerID")
    @Expose
    private Integer customerID;
    // @SerializedName("Firstname")
    @Expose
    private String firstname;
    // @SerializedName("Lastname")
    @Expose
    private String lastname;
    // @SerializedName("Fathername")
    @Expose
    private String fathername;
    // @SerializedName("NID")
    @Expose
    private Object nID;
    // @SerializedName("Sex")
    @Expose
    private Integer sex;
    // @SerializedName("Birthdate")
    @Expose
    private String birthdate;
    // @SerializedName("City")
    @Expose
    private String city;
    // @SerializedName("Country")
    @Expose
    private Object country;
    // @SerializedName("Mobile")
    @Expose
    private String mobile;
    // @SerializedName("Address")
    @Expose
    private String address;
    // @SerializedName("Email")
    @Expose
    private String email;
    // @SerializedName("Status")
    @Expose
    private String status;

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public Object getNID() {
        return nID;
    }

    public void setNID(Object nID) {
        this.nID = nID;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
        this.country = country;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

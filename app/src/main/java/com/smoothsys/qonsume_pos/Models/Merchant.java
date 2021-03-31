package com.smoothsys.qonsume_pos.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pontu on 2017-01-28.
 */

public class Merchant implements Parcelable {

    //@SerializedName("user_id")
    @Expose
    private int userID;

    //@SerializedName("user_name")
    @Expose
    private String userName;

    //@SerializedName("user_email")
    @Expose
    private String userEmail;

    //@SerializedName("user_pass")
    @Expose
    private String userPassword;

    //@SerializedName("role_id")
    @Expose
    private int roleID;

    //@SerializedName("is_owner")
    @Expose
    private int isOwner;

    //@SerializedName("is_shop_admin")
    @Expose
    private int isShopAdmin;

    //@SerializedName("shop_id")
    @Expose
    private int shopID;

    //@SerializedName("added")
    @Expose
    private String addedDate;

    //@SerializedName("status")
    @Expose
    private int status;

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public int getRoleID() {
        return roleID;
    }

    public int getIsOwner() {
        return isOwner;
    }

    public int getIsShopAdmin() {
        return isShopAdmin;
    }

    public int getShopID() {
        return shopID;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Merchant() {
    }

    protected Merchant(Parcel in) {
        userID = in.readInt();
        userName = in.readString();
        userEmail = in.readString();
        userPassword = in.readString();
        roleID = in.readInt();
        isOwner = in.readInt();
        isShopAdmin = in.readInt();
        shopID = in.readInt();
        addedDate = in.readString();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(userID);
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userPassword);
        dest.writeInt(roleID);
        dest.writeInt(isOwner);
        dest.writeInt(isShopAdmin);
        dest.writeInt(shopID);
        dest.writeString(addedDate);
        dest.writeInt(status);

        if(userName == null ||userEmail == null ||userPassword == null
                || addedDate == null) {
            Log.d("something is null", "something is null");
        }
    }


    // == Unused ==
    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

}
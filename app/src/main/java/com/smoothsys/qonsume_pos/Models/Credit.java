package com.smoothsys.qonsume_pos.Models;

public class Credit {

    public int getCreditID() {
        return creditID;
    }

    public void setCreditID(int creditID) {
        this.creditID = creditID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public Credit(int credit_id, String username, String useremail, String added, int amount) {
        this.creditID = credit_id;
        this.username = username;
        this.userEmail = useremail;
        this.addedDate = added;
        this.amount = amount;
    }

    private int creditID;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    private float amount;
    private String username;
    private String userEmail;
    private String addedDate;


}

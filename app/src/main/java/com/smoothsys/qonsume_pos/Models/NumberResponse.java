package com.smoothsys.qonsume_pos.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberResponse {

    @SerializedName("status")
    @Expose
    private int number;

    public int getNumber() { return number;}
    public void setNumber(int number) { this.number = number;}

}

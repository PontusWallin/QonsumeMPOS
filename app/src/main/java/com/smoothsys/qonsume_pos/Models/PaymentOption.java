package com.smoothsys.qonsume_pos.Models;

public class PaymentOption {

    private String optionName;
    private String iconName;
    private String description;
    private String paymentType;

    public PaymentOption(String optionName, String iconName, String description, String type) {
        this.optionName = optionName;
        this.iconName = iconName;
        this.description = description;
        this.paymentType = type;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getIconName() {
        return iconName;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return paymentType;
    }

}

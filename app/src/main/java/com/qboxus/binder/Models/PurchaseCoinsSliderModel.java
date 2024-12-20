package com.qboxus.binder.Models;

import java.io.Serializable;

public class PurchaseCoinsSliderModel implements Serializable {

    public String name;
    public String desc;
    public Integer image;

    public PurchaseCoinsSliderModel(String name, String desc, Integer image) {
        this.name = name;
        this.desc = desc;
        this.image = image;
    }
}

package com.qboxus.binder.Models;

import java.io.Serializable;

public class LoginSliderModel implements Serializable {
    public String name;
    public String desc;
    public Integer image;

    public LoginSliderModel(String name, String desc, Integer image) {
        this.name = name;
        this.desc = desc;
        this.image = image;
    }
}

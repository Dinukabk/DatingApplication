package com.qboxus.binder.Models;

import java.io.Serializable;

/**
 * Created by qboxus on 10/15/2018.
 */

public class ProfileSliderModel implements Serializable {
    String headerTv,descriptionTv,priceTv;

    public String getHeaderTv() {
        return headerTv;
    }

    public void setHeaderTv(String headerTv) {
        this.headerTv = headerTv;
    }

    public String getDescriptionTv() {
        return descriptionTv;
    }

    public void setDescriptionTv(String descriptionTv) {
        this.descriptionTv = descriptionTv;
    }

    public String getPriceTv() {
        return priceTv;
    }

    public void setPriceTv(String priceTv) {
        this.priceTv = priceTv;
    }

}

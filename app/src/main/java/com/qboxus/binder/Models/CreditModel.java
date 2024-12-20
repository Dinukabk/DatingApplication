package com.qboxus.binder.Models;

import java.io.Serializable;

public class CreditModel implements Serializable {
    String coinsPurchaseId;
    String coinsNumber;
    String coinsAmount;
    Integer coinsImage;

    public String getCoinsNumber() {
        return coinsNumber;
    }

    public void setCoinsNumber(String coinsNumber) {
        this.coinsNumber = coinsNumber;
    }

    public String getCoinsAmount() {
        return coinsAmount;
    }

    public void setCoinsAmount(String coinsAmount) {
        this.coinsAmount = coinsAmount;
    }

    public Integer getCoinsImage() {
        return coinsImage;
    }

    public void setCoinsImage(Integer coinsImage) {
        this.coinsImage = coinsImage;
    }

    public String getCoinsPurchaseId() {
        return coinsPurchaseId;
    }

    public void setCoinsPurchaseId(String coinsPurchaseId) {
        this.coinsPurchaseId = coinsPurchaseId;
    }
}

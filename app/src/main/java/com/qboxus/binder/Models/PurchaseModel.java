package com.qboxus.binder.Models;

import java.io.Serializable;

public class PurchaseModel implements Serializable {

    String productId;
    String productIdDuration;
    String productIdamount;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductIdDuration() {
        return productIdDuration;
    }

    public void setProductIdDuration(String productIdDuration) {
        this.productIdDuration = productIdDuration;
    }

    public String getProductIdamount() {
        return productIdamount;
    }

    public void setProductIdamount(String productIdamount) {
        this.productIdamount = productIdamount;
    }

}

package com.qboxus.binder.Models;

import java.io.Serializable;

public class SexualOrientationModel implements Serializable {

    String id;
    String sexualOrientation;

    Boolean isCheck = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

}

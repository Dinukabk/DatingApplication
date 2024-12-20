package com.qboxus.binder.Models;

import java.io.Serializable;

/**
 * Created by qboxus on 10/15/2018.
 */
public class MatchModel implements Serializable{
    String u_id,username,picture,superLike;

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSuperLike() {
        return superLike;
    }

    public void setSuperLike(String superLike) {
        this.superLike = superLike;
    }
}

package com.qboxus.binder.Models;

import java.io.Serializable;

public class FreeVideoCreditModel implements Serializable {
    String packageId;
    String credits;
    String noOfVideos;
    String viewNoOfVideos;


    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getNoOfVideos() {
        return noOfVideos;
    }

    public void setNoOfVideos(String noOfVideos) {
        this.noOfVideos = noOfVideos;
    }

    public String getViewNoOfVideos() {
        return viewNoOfVideos;
    }

    public void setViewNoOfVideos(String viewNoOfVideos) {
        this.viewNoOfVideos = viewNoOfVideos;
    }

}

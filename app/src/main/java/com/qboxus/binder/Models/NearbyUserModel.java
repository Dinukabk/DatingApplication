package com.qboxus.binder.Models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qboxus on 10/24/2018.
 */

public class NearbyUserModel implements Serializable {

    public String fbId;
    public String firstName;
    public String lastName;
    public String name;
    public String jobTitle;
    public String company;
    public String school;
    public String birthday;
    public String gender;
    public String genderShow;
    public String about;
    public String location;
    public String superLike;
    public String image;
    public String like;
    public String boost;
    public String boostDateTime;
    public String totalBoost;
    public String lastSeenDate;
    public String hide_location;
    public List<UserMultiplePhotoModel> imagesUrl;
    public List<String> userPassion ;

    String swipe;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSwipe() {
        return swipe;
    }

    public void setSwipe(String swipe) {
        this.swipe = swipe;
    }

    public String getSuperLike() {
        return superLike;
    }

    public void setSuperLike(String superLike) {
        this.superLike = superLike;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderShow() {
        return genderShow;
    }

    public void setGenderShow(String genderShow) {
        this.genderShow = genderShow;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getHide_location() {
        return hide_location;
    }

    public void setHide_location(String hide_location) {
        this.hide_location = hide_location;
    }

    public String getBoost() {
        return boost;
    }

    public void setBoost(String boost) {
        this.boost = boost;
    }

    public String getBoostDateTime() {
        return boostDateTime;
    }

    public void setBoostDateTime(String boostDateTime) {
        this.boostDateTime = boostDateTime;
    }

    public String getTotalBoost() {
        return totalBoost;
    }

    public void setTotalBoost(String totalBoost) {
        this.totalBoost = totalBoost;
    }

    public List<UserMultiplePhotoModel> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<UserMultiplePhotoModel> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public List<String> getUserPassion() {
        return userPassion;
    }

    public void setUserPassion(List<String> userPassion) {
        this.userPassion = userPassion;
    }

    public String getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(String lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

}


package com.qboxus.binder.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {

  public Boolean isFromPh;
  public Boolean isSocialLogin;
  public String socail_id="",
          socail_type="",
          fname="",
          lname="",
          email="",
          date_of_birth="",
          phone_no="",
          password="",
          username="",
          auth_tokon="",
          gender="",
          show_gender="",
          show_me_gender="",
          show_orientation="",
          mySchoolId="";

  public List<SexualOrientationModel> orientationList = new ArrayList<>();
  public List<String> userPassion = new ArrayList<>();

}

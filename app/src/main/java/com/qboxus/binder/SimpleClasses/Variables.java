package com.qboxus.binder.SimpleClasses;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by qboxus on 10/23/2018.
 */
public class Variables {

    public static final String http = "http";

    public static final String device="android";

    public static SharedPreferences sharedPreferences;
    public static String prefName ="pref_name";
    public static String fName ="f_name";
    public static String lName ="l_name";
    public static String uName ="u_Name";
    public static String birthDay ="birth_day";
    public static String gender="gender";
    public static String school="school";
    public static String distanceType ="distanceType";
    public static String uid="uid";
    public static String uPic ="u_pic";
    public static String uPhone ="u_Phone";
    public static String uPassions ="uPassions";
    public static String uTotalBoost ="totalboost";
    public static String uBoost ="boost";
    public static String uCoins ="coins";
    public static String uWallet ="wallet";
    public static String authToken ="auth_Token";
    public static String islogin="islogin";


    public static String currentLat ="current_Lat";
    public static String currentLon ="current_Lon";
    public static String currentLocationString ="current_Location";

    public static String seletedLat ="seleted_Lat";
    public static String selectedLon ="selected_Lon";
    public static String isSeletedLocationSelected ="is_seleted_location_selected";

    public static String selectedLocationString ="selected_location_string";


    public static String deviceToken = "device_token";
    public static String isProductPurchase = "isProductPurchase";
    public static String isCodePurchase = "isCodePurchase";
    public static String userLikeLimit = "userLikeLimit";
    public static String userLikeLimitDate = "userLikeLimitDate";

    public static String boostOnTime ="Boost_On_Time";


    public static String videoCallingUsedTime ="video_calling_used_time";
    public static String audioCallingUsedTime ="voice_calling_used_time";
    public static String usedCoins = "usedCoins";
    public static String callType = "callType";
    public static String dialingUserId = "dialingUserId";


    public static boolean isReloadUsers =false;
    public static String showMe ="show_me";
    public static String maxDistance ="max_distance";
    public static String minAge ="min_age";
    public static String maxAge ="max_age";
    public static String showMeOnTinder ="show_me_on_tinder";
    public static String hideAge ="hide_age";
    public static String hide_distance ="Hide_Distance";
    public static String selectedLanguage ="selected_language";
    public static String defultLanguage ="en";



    public static int screenHeight = 0;
    public static int screenWidth = 0;


    public static final String defaultLat ="40.6976701";
    public static final String defaultLon ="-74.2598737";


    public static String gifFirstpart ="https://media.giphy.com/media/";
    public static String gifSecondpart ="/100w.gif";

    public static String gifFirstpartChat ="https://media.giphy.com/media/";
    public static String gifSecondpartChat ="/200w.gif";


    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("HH:mmZZ", Locale.ENGLISH);


    public static SimpleDateFormat newdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

}

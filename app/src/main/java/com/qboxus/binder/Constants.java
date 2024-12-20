package com.qboxus.binder;

import android.content.Context;

import com.qboxus.binder.Models.CreditModel;
import com.qboxus.binder.Models.FreeVideoCreditModel;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public final static String BASE_URL = "https://domain.com/mobileapp_api/";
    public static final String API_KEY = "API_KEY";


    public final static String LICENCE_URL = "";
    public final static String PRIVACY_POLICY_URL = "";
    public final static String TERMS_OF_SERVICE_URL = "";
    public final static String DATE_SAFELY_URL = "";
    public final static String CONTACT_US_EMAIL = "";



    public static final String TWILIO_FUNCTION_LINK = "";

    public static String gifApiKey1 ="gif_api_key";


    //    if you false this param you can call unlimited time
    public static final boolean CALLING_LIMIT = true;
    public static final int ALLOW_FREE_MINUTES = 1;

    public static final int DEFAULT_DISTANCE =10000;

    public static final int MIN_DEFAULT_AGE = 18;
    public static final int MAX_DEFAULT_AGE = 75;


    public static int BOOST_TIME_DURATION = 1800000;

    public static boolean REMOVE_ADS = false;


    public static String tag="binder_";


    public static boolean enableSubscribe=true;

    public static final String PAYMENT_CURRENCY = "LKR";
    public static int BOOST_COINS = 100;

    public static int SUPER_LIKE_COINS = 50;

    public static int VIDEO_CALL_COINS = 50;

    public static int AUDIO_CALL_COINS = 30;

    public static boolean GET_COINS_FROM_VIDEOS = true;




    //in app currency symbol
    public static String INAPPCURRENCYSYMBOL ="$";
    public static String licenseKey="licenseKey";


    public static String subscriptionID ="com.qboxus.binder.subscription1";
    public static String subscriptionIdDuration ="1";
    public static String subscriptionIdamount ="1640";

    public static String subscriptionID2 ="com.qboxus.binder.subscription2";
    public static String subscriptionIdDuration2 ="3";
    public static String subscriptionIdamount2 ="2460";

    public static String subscriptionID3 ="com.qboxus.binder.subscription3";
    public static String subscriptionIdDuration3 ="12";
    public static String subscriptionIdamount3 ="5740";





    public static String boostID = "com.qboxus.binder.boost1";
    public static String boostNumber = "1";
    public static String boostamount1 = "160";

    public static String boostID2 = "com.qboxus.binder.boost2";
    public static String boostNumber2 = "5";
    public static String boostamount2 = "250";

    public static String boostID3 = "com.qboxus.binder.boost3";
    public static String boostNumber3 = "10";
    public static String boostamount3 = "400";


    public static String purchaseID = "com.qboxus.binder.purchased1";
    public static String purchaseIDCoins = "100";
    public static String purchaseIDPrice = "339";

    public static String purchaseID2 = "com.qboxus.binder.purchased2";
    public static String purchaseIDCoins2 = "550";
    public static String purchaseIDPrice2 = "1149";

    public static String purchaseID3 = "com.qboxus.binder.purchased3";
    public static String purchaseIDCoins3 = "1250";
    public static String purchaseIDPrice3 = "1699";

    //if you want to add more packages then add item in list
    public static List<FreeVideoCreditModel> freeCreditsVideosList() {
        List<FreeVideoCreditModel> list = new ArrayList<>();

        FreeVideoCreditModel model1 = new FreeVideoCreditModel();

        model1.setPackageId("1");
        model1.setCredits("200");
        model1.setNoOfVideos("10");
        model1.setViewNoOfVideos("0");

        list.add(model1);

        return list;

    }

    //if you want to increase or decrease credits packages then add or remove item from below list
    public static List<CreditModel> creditsPackagesList(Context context) {
        List<CreditModel> list = new ArrayList<>();

        if(GET_COINS_FROM_VIDEOS){
            CreditModel model = new CreditModel();

            model.setCoinsPurchaseId("");
            model.setCoinsNumber(context.getString(R.string.free));
            model.setCoinsAmount(context.getString(R.string.credits_capital));
            model.setCoinsImage(R.drawable.ic_free);

            list.add(model);
        }

        CreditModel model1 = new CreditModel();

        //here you will add purchase id which you will get from your play store
        model1.setCoinsPurchaseId(purchaseID);
        model1.setCoinsNumber(purchaseIDCoins);
        model1.setCoinsAmount(INAPPCURRENCYSYMBOL+purchaseIDPrice);
        model1.setCoinsImage(R.drawable.ic_coins_two);

        list.add(model1);
        //if you want to remove item then uncomment below line
        //list.remove(model1);

        CreditModel model2 = new CreditModel();

        model2.setCoinsPurchaseId(purchaseID2);
        model2.setCoinsNumber(purchaseIDCoins2);
        model2.setCoinsAmount(INAPPCURRENCYSYMBOL+purchaseIDPrice2);
        model2.setCoinsImage(R.drawable.ic_coins3);
        list.add(model2);

        CreditModel model3 = new CreditModel();
        model3.setCoinsPurchaseId(purchaseID3);
        model3.setCoinsNumber(purchaseIDCoins3);
        model3.setCoinsAmount(INAPPCURRENCYSYMBOL+purchaseIDPrice3);
        model3.setCoinsImage(R.drawable.ic_coins_muktiple);
        list.add(model3);

        return list;

    }


}

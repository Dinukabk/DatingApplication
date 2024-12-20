package com.qboxus.binder.ActivitiesFragments.inapppurchases;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.CountDownTimer;
import android.util.Log;
import com.qboxus.binder.ApiClasses.ApiLinks;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qboxus.binder.R;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.SimpleClasses.Variables;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import games.moisoni.google_iab.BillingConnector;
import games.moisoni.google_iab.BillingEventListener;
import games.moisoni.google_iab.enums.ProductType;
import games.moisoni.google_iab.models.BillingResponse;
import games.moisoni.google_iab.models.ProductInfo;
import games.moisoni.google_iab.models.PurchaseInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoostF extends Fragment implements View.OnClickListener {

    View view;
    Context context;
    CircularProgressBar circularProgressBar;

    RelativeLayout rl1,rl2,rl3,boostButton,seperaterView;
    LinearLayout ll1,goldView, walletView;
    TextView tv1,tv2,boostDescTV;

    String totalBoost = "";
    String boostNum = "";
    String userId;

    int position = 2, wallet;


    public BoostF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        if (!checkIsBoostOn()) {
            view = inflater.inflate(R.layout.fragment_boost, container, false);
            view.findViewById(R.id.boost_btn).setOnClickListener(this);

        }

        else {
            view = inflater.inflate(R.layout.fragment_boost_on, container, false);
            circularProgressBar = view.findViewById(R.id.circularProgressBar);
            view.findViewById(R.id.okay_btn).setOnClickListener(this);
            setProgress();
        }

        userId = Functions.getSharedPreference(context).getString(Variables.uid, "");
        view.findViewById(R.id.transparent_layout).setOnClickListener(this);

        totalBoost = Functions.getSharedPreference(context).getString(Variables.uTotalBoost, "");
        wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));

        if(!checkIsBoostOn()){
            initializeViews();

        }


        initializeBillingClient();

        return view;
    }


    private BillingConnector billingConnector;
    //list for example purposes to demonstrate how to manually acknowledge or consume purchases
    private final List<PurchaseInfo> purchasedInfoList = new ArrayList<>();
    //list for example purposes to demonstrate how to synchronously check a purchase state
    private final List<ProductInfo> fetchedProductInfoList = new ArrayList<>();

    private void initializeBillingClient() {
        //create a list with subscription ids
        List<String> consumableIds = new ArrayList<>();
        consumableIds.add(Constants.boostID);
        consumableIds.add(Constants.boostID2);
        consumableIds.add(Constants.boostID3);


        billingConnector = new BillingConnector(view.getContext(), Constants.licenseKey)
                .setConsumableIds(consumableIds)
                .autoAcknowledge()
                .autoConsume()
                .enableLogging()
                .connect();

        billingConnector.setBillingEventListener(new BillingEventListener() {
            @Override
            public void onProductsFetched(@NonNull List<ProductInfo> productDetails) {
                String product;

                for (ProductInfo productInfo : productDetails) {
                    product = productInfo.getProduct();

                    Log.d(Constants.tag, "onProductsFetched: " + product);

                    //TODO - similarly check for other ids

                    fetchedProductInfoList.add(productInfo); //check "usefulPublicMethods" to see how to synchronously check a purchase state
                }
            }

            @Override
            public void onPurchasedProductsFetched(@NonNull ProductType productType, @NonNull List<PurchaseInfo> purchases) {
                /*
                 * This will be called even when no purchased products are returned by the API
                 * */
                Log.d(Constants.tag,"onPurchasedProductsFetched: "+productType);
                switch (productType) {
                    case INAPP:
                        Log.d(Constants.tag,"INAPP available");
                        //TODO - non-consumable/consumable products
                        break;
                    case SUBS:
                        //TODO - subscription products
                        Log.d(Constants.tag,"Subscription available");
//                        if(user_info != null)
//                        {
//                            isSubscriptionEnable=true;
//                            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Variables.Var_App_Config_header_bg_color)));
//                        }
                        break;
                    case COMBINED:
                        Log.d(Constants.tag,"COMBINED available");
                        //this will be triggered on activity start
                        //the other two (INAPP and SUBS) will be triggered when the user actually buys a product
                        //TODO - restore purchases
                        break;
                }

                String product;
                for (PurchaseInfo purchaseInfo : purchases) {
                    product = purchaseInfo.getProduct();

                    Log.d(Constants.tag, "Purchased product fetched: " + product);
                    //TODO - similarly check for other ids
                }
            }

            @Override
            public void onProductsPurchased(@NonNull List<PurchaseInfo> purchases) {
                String product;
                String purchaseToken;

                for (PurchaseInfo purchaseInfo : purchases) {
                    product = purchaseInfo.getProduct();
                    purchaseToken = purchaseInfo.getPurchaseToken();

                    Log.d(Constants.tag, "Product purchased: " + product);
                    //TODO - do something
                    Log.d(Constants.tag, "Purchase token: " + purchaseToken);
                    //TODO - similarly check for other ids
                    if (product.equals(Constants.boostID))
                    {
                        callApiBoostProfilePurchase(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.boostID2))
                    {
                        callApiBoostProfilePurchase(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.boostID3))
                    {
                        callApiBoostProfilePurchase(purchaseInfo);
                    }
                    purchasedInfoList.add(purchaseInfo); //check "usefulPublicMethods" to see how to acknowledge or consume a purchase manually
                }
            }

            @Override
            public void onPurchaseAcknowledged(@NonNull PurchaseInfo purchase) {
                /*
                 * Grant user entitlement for NON-CONSUMABLE products and SUBSCRIPTIONS here
                 *
                 * Even though onProductsPurchased is triggered when a purchase is successfully made
                 * there might be a problem along the way with the payment and the purchase won't be acknowledged
                 *
                 * Google will refund users purchases that aren't acknowledged in 3 days
                 *
                 * To ensure that all valid purchases are acknowledged the library will automatically
                 * check and acknowledge all unacknowledged products at the startup
                 * */

                String acknowledgedProduct = purchase.getProduct();

                Log.d(Constants.tag, "onPurchaseAcknowledged: " + acknowledgedProduct);

                //TODO - similarly check for other ids
            }

            @Override
            public void onPurchaseConsumed(@NonNull PurchaseInfo purchase) {
                /*
                 * Grant user entitlement for CONSUMABLE products here
                 *
                 * Even though onProductsPurchased is triggered when a purchase is successfully made
                 * there might be a problem along the way with the payment and the user will be able consume the product
                 * without actually paying
                 * */

                String consumedProduct = purchase.getProduct();

                Log.d(Constants.tag, "onPurchaseConsumed: " + consumedProduct);

                //TODO - similarly check for other ids
            }

            @Override
            public void onBillingError(@NonNull BillingConnector billingConnector, @NonNull BillingResponse response) {
                switch (response.getErrorType()) {
                    case CLIENT_NOT_READY:
                        //TODO - client is not ready yet
                        break;
                    case CLIENT_DISCONNECTED:
                        //TODO - client has disconnected
                        break;
                    case PRODUCT_NOT_EXIST:
                        //TODO - product does not exist
                        break;
                    case CONSUME_ERROR:
                        //TODO - error during consumption
                        break;
                    case CONSUME_WARNING:
                        /*
                         * This will be triggered when a consumable purchase has a PENDING state
                         * User entitlement must be granted when the state is PURCHASED
                         *
                         * PENDING transactions usually occur when users choose cash as their form of payment
                         *
                         * Here users can be informed that it may take a while until the purchase complete
                         * and to come back later to receive their purchase
                         * */
                        //TODO - warning during consumption
                        break;
                    case ACKNOWLEDGE_ERROR:
                        //TODO - error during acknowledgment
                        break;
                    case ACKNOWLEDGE_WARNING:
                        /*
                         * This will be triggered when a purchase can not be acknowledged because the state is PENDING
                         * A purchase can be acknowledged only when the state is PURCHASED
                         *
                         * PENDING transactions usually occur when users choose cash as their form of payment
                         *
                         * Here users can be informed that it may take a while until the purchase complete
                         * and to come back later to receive their purchase
                         * */
                        //TODO - warning during acknowledgment
                        break;
                    case FETCH_PURCHASED_PRODUCTS_ERROR:
                        //TODO - error occurred while querying purchased products
                        break;
                    case BILLING_ERROR:
                        //TODO - error occurred during initialization / querying product details
                        break;
                    case USER_CANCELED:
                        //TODO - user pressed back or canceled a dialog
                        break;
                    case SERVICE_UNAVAILABLE:
                        //TODO - network connection is down
                        break;
                    case BILLING_UNAVAILABLE:
                        //TODO - billing API version is not supported for the type requested
                        break;
                    case ITEM_UNAVAILABLE:
                        //TODO - requested product is not available for purchase
                        break;
                    case DEVELOPER_ERROR:
                        //TODO - invalid arguments provided to the API
                        break;
                    case ERROR:
                        //TODO - fatal error during the API action
                        break;
                    case ITEM_ALREADY_OWNED:
                        //TODO - failure to purchase since item is already owned
                        break;
                    case ITEM_NOT_OWNED:
                        //TODO - failure to consume since item is not owned
                        break;
                }

                Log.d(Constants.tag, "Error type: " + response.getErrorType() +
                        " Response code: " + response.getResponseCode() + " Message: " + response.getDebugMessage());

            }
        });
    }




    private void initializeViews() {
        ll1 = view.findViewById(R.id.ll1);

        rl1 = view.findViewById(R.id.rl1);
        rl2 = view.findViewById(R.id.rl2);
        rl3 = view.findViewById(R.id.rl3);

        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);

        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);

        rl1.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
        rl2.setBackground(ContextCompat.getDrawable(context, R.drawable.d_blue_border));
        rl3.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.GONE);

        boostNum = Constants.boostNumber2;

        walletView = view.findViewById(R.id.walletView);
        walletView.setOnClickListener(this);
        goldView = view.findViewById(R.id.goldView);
        goldView.setOnClickListener(this);

        boostButton = view.findViewById(R.id.boost_btn);
        seperaterView = view.findViewById(R.id.seperaterView);
        boostDescTV = view.findViewById(R.id.boostDescTV);
        boostDescTV.setText(context.getString(R.string._1_boost_with_)+" "+Constants.BOOST_COINS+" "+context.getString(R.string.coins_));

        int totalBoost = Integer.parseInt(Functions.getSharedPreference(context)
                .getString(Variables.uTotalBoost,"0"));


        if(totalBoost>0){
            ll1.setVisibility(View.GONE);
            boostButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
            seperaterView.setVisibility(View.GONE);
            goldView.setVisibility(View.GONE);
        }else {
            ll1.setVisibility(View.VISIBLE);
            boostButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_blue_btn));
            seperaterView.setVisibility(View.VISIBLE);
            goldView.setVisibility(View.VISIBLE);
        }

        setvalues();
    }


    private void setvalues(){

        TextView boost_count1_txt=view.findViewById(R.id.boost_count1_txt);
        TextView boost_count2_txt=view.findViewById(R.id.boost_count2_txt);
        TextView boost_count3_txt=view.findViewById(R.id.boost_count3_txt);

        TextView boost_price1_txt=view.findViewById(R.id.boost_price1_txt);
        TextView boost_price2_txt=view.findViewById(R.id.boost_price2_txt);
        TextView boost_price3_txt=view.findViewById(R.id.boost_price3_txt);


        if(boost_count1_txt!=null)
            boost_count1_txt.setText(Constants.boostNumber);

        if(boost_count2_txt!=null)
            boost_count2_txt.setText(Constants.boostNumber2);

        if(boost_count3_txt!=null)
            boost_count3_txt.setText(Constants.boostNumber3);


        if(boost_price1_txt!=null)
            boost_price1_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.boostamount1 +"/ea");

        if(boost_price2_txt!=null)
            boost_price2_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.boostamount2 +"/ea");

        if(boost_price3_txt!=null)
            boost_price3_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.boostamount3 +"/ea");

    }



    long timeGone;
    public boolean checkIsBoostOn() {
        long requestTime = Long.parseLong( Functions.getSharedPreference(context).getString(Variables.boostOnTime, "0"));
        long currentTime = System.currentTimeMillis();

        timeGone = (currentTime - requestTime);

        if(timeGone>Constants.BOOST_TIME_DURATION ){
            return false;
        }
        else
            return true;


    }


    public void setProgress() {
        long requestTime = Long.parseLong( Functions.getSharedPreference(context).getString(Variables.boostOnTime, "0"));
        long currentTime = System.currentTimeMillis();

        timeGone = (currentTime - requestTime);
        startTimer();
    }


    CountDownTimer timer;
    public void startTimer() {
        long timeLeft = Constants.BOOST_TIME_DURATION - timeGone;
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                long millis = l;

                String timeString = Functions.convertSeconds((int) (millis / 1000));
                TextView textView = view.findViewById(R.id.remaining_txt);
                textView.setText(timeString+" "+context.getString(R.string.Remaining));

                float progress = ((l * 100) / Constants.BOOST_TIME_DURATION);
                circularProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                stopTimer();
            }
        };

        timer.start();
    }

    public void stopTimer() {
        if (timer != null){
            timer.cancel();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.boost_btn) {
            int totalBoost = Integer.parseInt(Functions.getSharedPreference(context)
                    .getString(Variables.uTotalBoost, "0"));
            if (totalBoost > 0) {
                callApiForBoostProfile();
            } else {
                puchaseItem();
//                 callApiBoostProfilePurchase();
            }
        } else if (id == R.id.transparent_layout) {
            getActivity().onBackPressed();
        } else if (id == R.id.okay_btn) {
            getActivity().onBackPressed();
        } else if (id == R.id.rl1) {
            rl1.setBackground(ContextCompat.getDrawable(context, R.drawable.d_blue_border));
            rl2.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            rl3.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            position = 1;
            boostNum = Constants.boostNumber;
        } else if (id == R.id.rl2) {
            rl1.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            rl2.setBackground(ContextCompat.getDrawable(context, R.drawable.d_blue_border));
            rl3.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.GONE);
            position = 2;
            boostNum = Constants.boostNumber2;
        } else if (id == R.id.rl3) {
            rl1.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            rl2.setBackground(ContextCompat.getDrawable(context, R.drawable.d_white_border));
            rl3.setBackground(ContextCompat.getDrawable(context, R.drawable.d_blue_border));
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
            position = 3;
            boostNum = Constants.boostNumber3;
        } else if (id == R.id.walletView) {
            if (wallet > Constants.BOOST_COINS || wallet == Constants.BOOST_COINS) {
                callApiForUseCoins();
            } else {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                PurchaseCoinsF fragment = new PurchaseCoinsF();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, fragment).commit();
            }
        } else if (id == R.id.goldView) {
            openSubscriptionView();
        }
    }


    public void puchaseItem() {
        if(position == 1){
            billingConnector.purchase(getActivity(), Constants.boostID);
        }else if(position == 2){
            billingConnector.purchase(getActivity(), Constants.boostID2);
        } else if(position == 3){
            billingConnector.purchase(getActivity(), Constants.boostID3);
        }
    }


    // when user will click the refresh btn  then this view will be open for subscribe it in our app
    public void openSubscriptionView(){
        if(!Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,false)){
            startActivity(new Intent(getActivity(), InAppSubscriptionA.class));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }


    private void callApiForUseCoins() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("coin", ""+Constants.BOOST_COINS);
            parameters.put("feature", "boost");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApiLinks.useCoin, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if(code.equals("200")){
                        JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");

                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.uWallet, userObject.getString("wallet")).apply();

                        callApiForBoostProfile();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callApiForBoostProfile() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApiLinks.boostProfile, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if(code.equals("200")){
                        JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");

                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.uTotalBoost, userObject.getString("total_boost")).commit();

                        Functions.getSharedPreference(context).edit()
                                .putString(Variables.uBoost, userObject.getString("boost")).commit();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                long min = System.currentTimeMillis();
                Functions.getSharedPreference(context).edit().putString(Variables.boostOnTime, "" + min).commit();
                getActivity().onBackPressed();
            }
        });
    }

    private void callApiBoostProfilePurchase(PurchaseInfo purchaseInfo) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("transaction_id", purchaseInfo.getPurchaseToken());
            parameters.put("no_of_boost", boostNum);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Functions.showLoader(getActivity(), false, false);
                ApiRequest.callApi(context, ApiLinks.boostPurchase, parameters, new Callback() {
                    @Override
                    public void response(String resp) {
                        Functions.cancelLoader();

                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            String code = jsonObject.optString("code");
                            if(code.equals("200")){
                                JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");

                                Functions.getSharedPreference(context).edit()
                                        .putString(Variables.uTotalBoost, userObject.getString("total_boost")).commit();

                                Functions.getSharedPreference(context).edit()
                                        .putString(Variables.uBoost, userObject.getString("boost")).commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        long min = System.currentTimeMillis();
                        Functions.getSharedPreference(context).edit().putString(Variables.boostOnTime, "" + min).commit();
                        getActivity().onBackPressed();
                    }
                });
            }
        });

    }


}

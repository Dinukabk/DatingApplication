package com.qboxus.binder.ActivitiesFragments.inapppurchases;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qboxus.binder.ActivitiesFragments.Accounts.ForgotPassA;
import com.qboxus.binder.ActivitiesFragments.Accounts.LoginA;
import com.qboxus.binder.ActivitiesFragments.SplashA;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.qboxus.binder.Adapters.SlidingImageAdapter;
import com.qboxus.binder.R;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.qboxus.binder.interfaces.Callback;
import games.moisoni.google_iab.BillingConnector;
import games.moisoni.google_iab.BillingEventListener;
import games.moisoni.google_iab.enums.ProductType;
import games.moisoni.google_iab.models.BillingResponse;
import games.moisoni.google_iab.models.ProductInfo;
import games.moisoni.google_iab.models.PurchaseInfo;


public class InAppSubscriptionA extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    RelativeLayout purchaseBtn;
    TextView goBack;
    LinearLayout subLayout1, subLayout2, subLayout3;


    String duration,amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_subscription);

        // get the shared preference
        sharedPreferences=getSharedPreferences(Variables.prefName,MODE_PRIVATE);

        purchaseBtn =findViewById(R.id.purchase_btn);
        purchaseBtn.setOnClickListener(this);


        goBack =findViewById(R.id.Goback);
        goBack.setOnClickListener(this);

        setSlider();

        subLayout1 = findViewById(R.id.sub_layout1);
        subLayout2 =findViewById(R.id.sub_layout2);
        subLayout3 = findViewById(R.id.sub_layout3);

        selectOne(1);
        amount = Constants.subscriptionIdamount2;
        duration = Constants.subscriptionIdDuration2;

        subLayout1.setOnClickListener(this);
        subLayout2.setOnClickListener(this);
        subLayout3.setOnClickListener(this);

        setValues();

        initializeBillingClient();
    }


    boolean isSubscriptionEnable=false;
    private BillingConnector billingConnector;
    //list for example purposes to demonstrate how to manually acknowledge or consume purchases
    private final List<PurchaseInfo> purchasedInfoList = new ArrayList<>();
    //list for example purposes to demonstrate how to synchronously check a purchase state
    private final List<ProductInfo> fetchedProductInfoList = new ArrayList<>();

    private void initializeBillingClient() {
        //create a list with subscription ids
        List<String> subscriptionIds = new ArrayList<>();
        subscriptionIds.add(Constants.subscriptionID);
        subscriptionIds.add(Constants.subscriptionID2);
        subscriptionIds.add(Constants.subscriptionID3);


        billingConnector = new BillingConnector(this, Constants.licenseKey) //"license_key" - public developer key from Play Console
                .setSubscriptionIds(subscriptionIds) //to set subscription ids - call only for subscription products
                .autoAcknowledge() //legacy option - better call this. Alternatively purchases can be acknowledge via public method "acknowledgePurchase(PurchaseInfo purchaseInfo)"
                .autoConsume() //legacy option - better call this. Alternatively purchases can be consumed via public method consumePurchase(PurchaseInfo purchaseInfo)"
                .enableLogging() //to enable logging for debugging throughout the library - this can be skipped
                .connect(); //to connect billing client with Play Console

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
                    if (product.equals(Constants.subscriptionID))
                    {
                        callApiForUpdatePurchase(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.subscriptionID2))
                    {
                        callApiForUpdatePurchase(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.subscriptionID3))
                    {
                        callApiForUpdatePurchase(purchaseInfo);
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


    private void setValues(){

        TextView subscription_price1_txt=findViewById(R.id.subscription_price1_txt);
        TextView subscription_price2_txt=findViewById(R.id.subscription_price2_txt);
        TextView subscription_price3_txt=findViewById(R.id.subscription_price3_txt);


        TextView subscription_duration1_txt=findViewById(R.id.subscription_duration1_txt);
        TextView subscription_duration2_txt=findViewById(R.id.subscription_duration2_txt);
        TextView subscription_duration3_txt=findViewById(R.id.subscription_duration3_txt);


        subscription_price1_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.subscriptionIdamount);
        subscription_price2_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.subscriptionIdamount2);
        subscription_price3_txt.setText(Constants.INAPPCURRENCYSYMBOL+Constants.subscriptionIdamount3);


        subscription_duration1_txt.setText(Constants.subscriptionIdDuration +" month");
        subscription_duration2_txt.setText(Constants.subscriptionIdDuration2 +" months");
        subscription_duration3_txt.setText(Constants.subscriptionIdDuration3 +" months");
    }


    int subscriptionPosition = 1;
    public void selectOne(int position){
        subscriptionPosition = position;

        subLayout1.setBackground(getResources().getDrawable(R.drawable.d_round_gray_border));
        subLayout2.setBackground(getResources().getDrawable(R.drawable.d_round_gray_border));
        subLayout3.setBackground(getResources().getDrawable(R.drawable.d_round_gray_border));

        if(position==0){
            subLayout1.setBackground(getResources().getDrawable(R.drawable.d_round_pink_border));
        } else if(position==1){
            subLayout2.setBackground(getResources().getDrawable(R.drawable.d_round_pink_border));
        } else if(position==2){
            subLayout3.setBackground(getResources().getDrawable(R.drawable.d_round_pink_border));
        }

    }


    // when we click the continue btn this method will call
    public void puchaseItem() {
        if(subscriptionPosition == 0){
            billingConnector.subscribe(InAppSubscriptionA.this, Constants.subscriptionID);
            amount = Constants.subscriptionIdamount;
            duration = Constants.subscriptionIdDuration;
        }else if(subscriptionPosition == 1){
            billingConnector.subscribe(InAppSubscriptionA.this, Constants.subscriptionID2);
            amount = Constants.subscriptionIdamount2;
            duration = Constants.subscriptionIdDuration2;
        } else if(subscriptionPosition == 2){
            billingConnector.subscribe(InAppSubscriptionA.this, Constants.subscriptionID3);
            amount = Constants.subscriptionIdamount3;
            duration = Constants.subscriptionIdDuration3;
        }
    }


    // when user subscribe the app then this method will call that will store the status of user
    // into the database
    private void callApiForUpdatePurchase(PurchaseInfo purchaseInfo) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getApplicationContext()).getString(Variables.uid,""));
            parameters.put("package",""+purchaseInfo.getPurchaseToken());
            parameters.put("amount", amount);
            parameters.put("duration", duration);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        Functions.showLoader(InAppSubscriptionA.this,false,false);
        ApiRequest.callApi(InAppSubscriptionA.this, ApiLinks.startSubscription, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                try {
                    JSONObject response=new JSONObject(resp);
                    String code=response.optString("code");
                    if(code.equals("200")) {

                        Functions.getSharedPreference(getApplicationContext()).edit().putBoolean(Variables.isProductPurchase, true).commit();
                        Intent intent=new Intent(InAppSubscriptionA.this, SplashA.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else
                    {
                        String msg_txt =  response.getString("msg");
                        Toast.makeText(InAppSubscriptionA.this, msg_txt, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }



    private ViewPager mPager;
    private ArrayList<Integer> ImagesArray;
    public void setSlider(){

        ImagesArray=new ArrayList<>();
        ImagesArray.add(0);
        ImagesArray.add(1);
        ImagesArray.add(2);
        mPager = findViewById(R.id.image_slider_pager);

        try {
            mPager.setAdapter(new SlidingImageAdapter(this, ImagesArray));
        } catch (NullPointerException e){
            e.getCause();
        }

        mPager.setCurrentItem(0);

        TabLayout indicator = findViewById(R.id.indicator);
        indicator.setupWithViewPager(mPager, true);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.purchase_btn) {
            puchaseItem();
        }

        else if (id == R.id.Goback) {
            onBackPressed();
        }

        else if (id == R.id.sub_layout1) {
            selectOne(0);
        }

        else if (id == R.id.sub_layout2) {
            selectOne(1);
        }

        else if (id == R.id.sub_layout3) {
            selectOne(2);
        }


    }

}

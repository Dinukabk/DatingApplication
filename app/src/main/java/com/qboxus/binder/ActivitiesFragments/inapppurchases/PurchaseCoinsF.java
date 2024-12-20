package com.qboxus.binder.ActivitiesFragments.inapppurchases;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.qboxus.binder.Adapters.CreditsAdapter;
import com.qboxus.binder.Adapters.PurchaseCoinsSlidingAdapter;
import com.qboxus.binder.R;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.DebounceClickHandler;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.AdapterClickListener;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.CreditModel;
import com.qboxus.binder.Models.PurchaseCoinsSliderModel;
import com.qboxus.binder.SimpleClasses.GridSpacingItemDecoration;
import com.qboxus.binder.SimpleClasses.Variables;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.qboxus.binder.ApiClasses.ApiLinks;
import games.moisoni.google_iab.BillingConnector;
import games.moisoni.google_iab.BillingEventListener;
import games.moisoni.google_iab.enums.ProductType;
import games.moisoni.google_iab.models.BillingResponse;
import games.moisoni.google_iab.models.ProductInfo;
import games.moisoni.google_iab.models.PurchaseInfo;

public class PurchaseCoinsF extends Fragment{

    View view;
    Context context;

    RecyclerView rv;
    CreditsAdapter adapter;

    CreditModel model = new CreditModel();
    public static String selectedCoins = "";

    int position=2;

    TextView coin_count_txt,continueButton;
    RelativeLayout continueButtonView;
    ViewPager viewPager;
    WormDotsIndicator dotsIndicator;
    List<PurchaseCoinsSliderModel> data_list;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 5000; //delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;


    String coins="", price="";



    public PurchaseCoinsF() {
        //Required Empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_purchase, container, false);
        context=getContext();

        initializeViews();
        initializeBillingClient();
        callApiShowUserDetail();

        return view;
    }


    private void initializeViews() {
        view.findViewById(R.id.Goback).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        }));

        coin_count_txt = view.findViewById(R.id.coin_count_txt);
        coin_count_txt.setText(Functions.getSharedPreference(context).getString(Variables.uWallet,""));

        rv = view.findViewById(R.id.rv);

        setAdapterToRecyclerView();

        viewPager = view.findViewById(R.id.viewPager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);
        setSlider();
        handler();

        continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                puchaseItem();
            }
        }));
        continueButtonView = view.findViewById(R.id.continueButtonView);
    }


    private void callApiShowUserDetail() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context)
                    .getString(Variables.uid,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(context, ApiLinks.showUserDetail, parameters, resp -> {
            Functions.cancelLoader();
            parseUserInfo(resp);
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void parseUserInfo(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject userdata = msg.optJSONObject("User");
                Functions.getSharedPreference(context).edit()
                        .putString(Variables.uWallet, userdata.getString("wallet")).apply();
                coin_count_txt.setText(userdata.getString("wallet"));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setAdapterToRecyclerView() {
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rv.setLayoutManager(layout);
        rv.setHasFixedSize(false);

        adapter = new CreditsAdapter(getContext(), Constants.creditsPackagesList(context), new AdapterClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int pos, Object object, View view) {
                model = (CreditModel) object;
                position = pos;
                Constants.creditsPackagesList(context).remove(position);
                selectedCoins = model.getCoinsNumber();
                Constants.creditsPackagesList(context).add(position,model);
                adapter.notifyDataSetChanged();

                if(Constants.GET_COINS_FROM_VIDEOS && pos == 0){
                    openFreeCreditsVideoFragment();
                    continueButton.setTextColor(ContextCompat.getColor(context, R.color.gray));
                    continueButtonView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_google_background));
                }

                else {
                    position = pos+1;
                    coins = model.getCoinsNumber();
                    price = model.getCoinsAmount();
                    continueButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                    continueButtonView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
                }
            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {
            }
        });

        rv.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._10sdp);
        rv.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));

    }


    private void setSlider() {
        data_list = new ArrayList<>();
        data_list.add(new PurchaseCoinsSliderModel(context.getString(R.string.boost), context.getString(R.string.boostDescription)+" "+Constants.BOOST_COINS+" "+context.getString(R.string.boostDescription1), R.drawable.ic_boost_purchase));
        data_list.add(new PurchaseCoinsSliderModel(context.getString(R.string.videocall), context.getString(R.string.videocallDescription)+" "+Constants.VIDEO_CALL_COINS+" "+context.getString(R.string.videocallDescription1), R.drawable.ic_video_call_purchase));
        data_list.add(new PurchaseCoinsSliderModel(context.getString(R.string.superlike), context.getString(R.string.superlikeDescription), R.drawable.ic_superlike_purchase));

        viewPager.setAdapter(new PurchaseCoinsSlidingAdapter(context, data_list));
        dotsIndicator.setViewPager(viewPager);
    }


    private void handler() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == data_list.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }




    private BillingConnector billingConnector;
    //list for example purposes to demonstrate how to manually acknowledge or consume purchases
    private final List<PurchaseInfo> purchasedInfoList = new ArrayList<>();
    //list for example purposes to demonstrate how to synchronously check a purchase state
    private final List<ProductInfo> fetchedProductInfoList = new ArrayList<>();

    private void initializeBillingClient() {
        //create a list with subscription ids
        List<String> consumableIds = new ArrayList<>();
        consumableIds.add(Constants.purchaseID);
        consumableIds.add(Constants.purchaseID2);
        consumableIds.add(Constants.purchaseID3);


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
                    if (product.equals(Constants.purchaseID))
                    {
                        callApiPurchaseCoins(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.purchaseID2))
                    {
                        callApiPurchaseCoins(purchaseInfo);
                    }
                    else
                    if (product.equals(Constants.purchaseID3))
                    {
                        callApiPurchaseCoins(purchaseInfo);
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


    public void puchaseItem() {
        Log.d(Constants.tag,"position: "+position);
        if(position == 2){
            billingConnector.purchase(getActivity(), Constants.purchaseID);
        }else if(position == 3){
            billingConnector.purchase(getActivity(), Constants.purchaseID2);
            Log.d(Constants.tag,"purchase: "+Constants.purchaseID2);
        } else if(position == 4){
            billingConnector.purchase(getActivity(), Constants.purchaseID3);
        }
    }



    private void callApiPurchaseCoins(PurchaseInfo purchaseInfo) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.uid,""));
            parameters.put("title", coins+" coins");
            parameters.put("coin", coins);
            parameters.put("price", price);
            parameters.put("purchase_id", purchaseInfo.getPurchaseToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Functions.showLoader(context, false, false);
                ApiRequest.callApi(context, ApiLinks.purchaseCoin, parameters, new Callback() {
                    @Override
                    public void response(String resp) {
                        Functions.cancelLoader();

                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String code = jsonObject.optString("code");

                            if(code.equals("200")){
                                JSONObject userObject = jsonObject.optJSONObject("msg").optJSONObject("User");

                                coin_count_txt.setText(userObject.optString("wallet"));
                                Functions.getSharedPreference(context).edit()
                                        .putString(Variables.uWallet, userObject.optString("wallet")).commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    public void openFreeCreditsVideoFragment(){
        FreeCreditsVideoF freeCreditsVideoF = new FreeCreditsVideoF();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        getActivity().getSupportFragmentManager().setFragmentResultListener("1222",
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        if(requestKey.equals("1222")){
                            coin_count_txt.setText(Functions.getSharedPreference(context).getString(Variables.uWallet,""));
                        }
                    }
                });
        transaction.addToBackStack(null).replace(R.id.purchaseFragment, freeCreditsVideoF).commit();
    }

    @Override
    public void onDetach() {
        selectedCoins = "";
        super.onDetach();
    }


}

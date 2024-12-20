package com.qboxus.binder.ActivitiesFragments.Users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.Variables;
import org.json.JSONException;
import org.json.JSONObject;
import com.qboxus.binder.ApiClasses.ApiLinks;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuperLikePopupF extends Fragment implements View.OnClickListener {

    View view;
    Context context;

    RelativeLayout seperaterView;
    LinearLayout  goldView, walletView;
    TextView boostDescTV;

    String userId;

    int wallet;
    FragmentCallback callback;

    public SuperLikePopupF() {
        // Required empty public constructor
    }

    public SuperLikePopupF(FragmentCallback callback) {
        this.callback = callback;
    }

    public static SuperLikePopupF newInstance(FragmentCallback callback) {
        SuperLikePopupF fragment = new SuperLikePopupF(callback);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_superlike_popup, container, false);

        context = getActivity();

        userId = Functions.getSharedPreference(context).getString(Variables.uid, "");
        wallet = Integer.parseInt(Functions.getSharedPreference(context).getString(Variables.uWallet, "0"));

        initializeViews();

        return view;
    }


    private void initializeViews() {
        view.findViewById(R.id.transparent_layout).setOnClickListener(this);




        walletView = view.findViewById(R.id.walletView);
        walletView.setOnClickListener(this);
        goldView = view.findViewById(R.id.goldView);
        goldView.setOnClickListener(this);

        seperaterView = view.findViewById(R.id.seperaterView1);
        if(Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,Constants.enableSubscribe)){
            goldView.setVisibility(View.GONE);
            seperaterView.setVisibility(View.GONE);
        }else {
            goldView.setVisibility(View.VISIBLE);
            seperaterView.setVisibility(View.VISIBLE);
        }

        boostDescTV = view.findViewById(R.id.boostDescTV);
        boostDescTV.setText(context.getString(R.string._1_superlike_with)+" "+Constants.SUPER_LIKE_COINS+" "+context.getString(R.string.coins_));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transparent_layout:
                callback.responce(new Bundle());
                getActivity().onBackPressed();
                break;

            case R.id.walletView:
                if(Functions.getSharedPreference(context).getBoolean(Variables.isCodePurchase,false)){
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }
                    try {
                        Fragment fragment = (Fragment) Class.forName("com.qboxus.binder.ActivitiesFragments.inapppurchases.PurchaseCoinsF").newInstance();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null).replace(R.id.MainMenuFragment, fragment).commit();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.goldView:
                openSubscriptionView();
                break;

        }
    }

    // when user will click the refresh btn  then this view will be open for subscribe it in our app
    public void openSubscriptionView(){
        if(!Functions.getSharedPreference(context).getBoolean(Variables.isProductPurchase,false)){
            try {
                startActivity(new Intent(getActivity(), Class.forName("com.qboxus.binder.ActivitiesFragments.inapppurchases.InAppSubscriptionA")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void callApiForUseCoins() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("coin", ""+Constants.SUPER_LIKE_COINS);
            parameters.put("feature", "superlike");
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

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("superlike", true);
                        callback.responce(bundle);
                        getActivity().onBackPressed();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

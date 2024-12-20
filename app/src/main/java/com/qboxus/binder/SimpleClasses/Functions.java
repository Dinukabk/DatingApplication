package com.qboxus.binder.SimpleClasses;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.qboxus.binder.BuildConfig;
import com.qboxus.binder.R;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.Constants;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Functions {


    public static void swipeLeft(CardStackView cardStackView) {
        View target = cardStackView.getTopView();
        View targetOverlay = cardStackView.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, -2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(400);
        translateY.setStartDelay(400);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);

        cardStackView.swipe(SwipeDirection.Left, cardAnimationSet, overlayAnimationSet);
    }

    public static void swipeRight(CardStackView cardStackView) {
        View target = cardStackView.getTopView();
        View targetOverlay = cardStackView.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(400);
        translateY.setStartDelay(400);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);

        cardStackView.swipe(SwipeDirection.Right, cardAnimationSet, overlayAnimationSet);
    }


    public static void swipeTop(CardStackView cardStackView) {
        View target = cardStackView.getTopView();
        View targetOverlay = cardStackView.getTopView().getOverlayContainer();

        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 0f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, -2000f));
        translateX.setStartDelay(400);
        translateY.setStartDelay(400);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether( translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);

        cardStackView.swipe(SwipeDirection.Top, cardAnimationSet, overlayAnimationSet);
    }


    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(String url,int resource, SimpleDraweeView simpleDrawee, boolean isGif)
    {
        if (url==null)
        {
            url="null";
        }
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);

        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }



        return controller;
    }


    public static DraweeController frescoImageLoad(Uri uri, int resource, SimpleDraweeView simpleDrawee, boolean isGif)
    {

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }



        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Drawable drawable, SimpleDraweeView simpleDrawee, boolean isGif)
    {


        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(drawable);
        simpleDrawee.getHierarchy().setFailureImage(drawable);
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .build();
        }

        return controller;
    }








    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void printLog(String msg){
        if(BuildConfig.DEBUG)
            Log.d(Constants.tag,msg);
    }

    //access private storage
    public static String getAppFolder(Context activity)
    {
        try {
            return activity.getExternalFilesDir(null).getPath()+"/";
        }
        catch (Exception e)
        {
            return Environment.getDataDirectory().getPath()+"/";
        }
    }


    public static SharedPreferences getSharedPreference(Context context){
        if(Variables.sharedPreferences!=null)
            return Variables.sharedPreferences;
        else {
            Variables.sharedPreferences = context.getSharedPreferences(Variables.prefName, Context.MODE_PRIVATE);
            return Variables.sharedPreferences;
        }

    }

    public static int convertDpToPx(Context context, int dp) {
        return (int) ((int) dp * context.getResources().getDisplayMetrics().density);
    }

    // initialize the loader dialog and show
    public static Dialog dialog;

    public static void showLoader(Context context, boolean outside_touch, boolean cancleable) {
        try {

            if (dialog != null)
            {
                cancelLoader();
                dialog=null;
            }
            {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dialog_loading_view);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

                if (!outside_touch)
                    dialog.setCanceledOnTouchOutside(false);

                if (!cancleable)
                    dialog.setCancelable(false);

                dialog.show();
            }

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception : "+e);
        }
    }

    public static void cancelLoader() {
        try {
            if (dialog != null || dialog.isShowing()) {
                dialog.cancel();
            }
        }catch (Exception e){
            Log.d(Constants.tag,"Exception : "+e);
        }
    }

    public static String convertSeconds(int seconds){
        int h = seconds/ 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "")+ss;
    }

    public static String getRandomString(int n) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static void showAlert(Context context, String title, String description, Callback callBack){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(callBack!=null){
                    callBack.response("OK");
                }
            }
        });
        builder.create();
        builder.show();
    }




    static BroadcastReceiver broadcastReceiver;
    static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    public static void unRegisterConnectivity(Context mContext) {
        try {
            mContext.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void registerConnectivity(Context context, final Callback callback) {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.response("connected");
                } else {
                    callback.response("disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static Boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }

        Log.i("update_statut","Network is available : FALSE ");
        return false;

    }

    // this is the delete message dialog which will show after long press in chat message
    public static void showOptions(Context context, CharSequence[] options, final  Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                callback.response("" + options[item]);
            }
        });

        builder.show();
    }

    public static String bitmapToBase64(Activity activity, Bitmap imageBitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos .toByteArray();
        String base64= Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(context.getExternalFilesDir(null).getAbsolutePath() , "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName+".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static double calculateSegmentProgress(double currentSegment, double totalSegments){
        return Math.round((currentSegment / totalSegments) * 100);
    }

    public static String changemiletoKm(String mile){
        try {
            int mile_value = Integer.parseInt("mile");
            int km = (int) (mile_value * 1.6);
            return "" + km;
        }
        catch (Exception e){
            return mile;
        }

    }

    public static String convertMillisecondsToHoursMinutes(long milliseconds){
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    //check rational permission status
    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }


    public static void showDoubleButtonAlert(Context context, String title, String message, String negTitle, String posTitle,boolean isCancelable, FragmentCallback callBack)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvtitle,tvMessage,tvPositive,tvNegative;
        tvtitle=dialog.findViewById(R.id.tvtitle);
        tvMessage=dialog.findViewById(R.id.tvMessage);
        tvNegative=dialog.findViewById(R.id.tvNegative);
        tvPositive=dialog.findViewById(R.id.tvPositive);


        tvtitle.setText(title);
        tvMessage.setText(message);
        tvNegative.setText(negTitle);
        tvPositive.setText(posTitle);

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",false);
                callBack.responce(bundle);
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                callBack.responce(bundle);
            }
        });
        dialog.show();
    }


    //show permission setting screen
    public static void showPermissionSetting(Context context,String message) {
        showDoubleButtonAlert(context, context.getString(R.string.permission_alert),message,
                context.getString(R.string.cancel), context.getString(R.string.settings), false, new FragmentCallback() {
                    @Override
                    public void responce(Bundle bundle) {
                        if (bundle.getBoolean("isShow",false))
                        {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    }
                });
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/ Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/ Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    //    app language change
    @SuppressLint("NewApi")
    public static void setLocale(String lang, Activity context, Class<?> className, boolean isRefresh) {

        String[] languageArray=context.getResources().getStringArray(R.array.language_code);
        List<String> languageCode = Arrays.asList(languageArray);
        if (languageCode.contains(lang)) {
            Locale myLocale = new Locale(lang);
            Resources res = context.getBaseContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
            context.onConfigurationChanged(conf);

            if (isRefresh)
            {
                updateActivity(context,className);
            }
        }
    }

    public static void updateActivity(Activity context, Class<?> className) {
        Intent intent = new Intent(context,className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }


    public static String ChangeDateFormatWithAdditionalMonth(String fromFormat, String toFormat, String date, int additionalMonth){

        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat, Locale.ENGLISH);
        Date sourceDate = null;

        try {
            sourceDate = dateFormat.parse(date);

            Calendar calanderDate = Calendar.getInstance();
            calanderDate.setTime(sourceDate);
            calanderDate.add(Calendar.MONTH,additionalMonth);
            sourceDate=calanderDate.getTime();

            SimpleDateFormat targetFormat = new SimpleDateFormat(toFormat,Locale.ENGLISH);

            return  targetFormat.format(sourceDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    // getCurrent Date
    public static String getCurrentDate(String dateFormat) {
        SimpleDateFormat format=new SimpleDateFormat(dateFormat,Locale.ENGLISH);
        Calendar date = Calendar.getInstance();
        return format.format(date.getTime());
    }

    public static boolean isDateExpiredInDays(String format, String mStartDate, String mEndDate) {
        try {

            Calendar startDateCal = Calendar.getInstance();
            Calendar endDateCal = Calendar.getInstance();


            SimpleDateFormat f = new SimpleDateFormat(format,Locale.ENGLISH);

            Date startDate = null;
            try {
                startDate = f.parse(mStartDate);
                startDateCal.setTime(startDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception startDate: "+e);
            }

            Date endDate = null;
            try {
                endDate = f.parse(mEndDate);
                endDateCal.setTime(endDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception endDate: "+e);
            }

            long difference = (endDateCal.getTimeInMillis() - startDateCal.getTimeInMillis()) / 1000;

            Log.d(Constants.tag,"difference in Second: "+difference);
            if (difference>0)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception days: "+e);
            return true;
        }

    }


    //    deduction on minutes base
    public static double allowMinutesForCalling(int totalCoins, String callType) {
        double allowMinutes =0;
        if (callType.equals("voice_call"))
        {
            allowMinutes=(totalCoins/Constants.AUDIO_CALL_COINS);
        }
        else
        {
            allowMinutes=(totalCoins/Constants.VIDEO_CALL_COINS);
        }
        if (allowMinutes>0)
        {
            return formatDouble(allowMinutes);
        }
        else
        {
            return formatDouble(allowMinutes+Constants.ALLOW_FREE_MINUTES);
        }
    }

    //    deduction on minutes base
    public static long getCoinsAgainstCallTime(double timeSpendInCall, String callType) {
        double totalCoins =0;
        if (callType.equals("voice_call"))
        {
            totalCoins= (timeSpendInCall*Constants.AUDIO_CALL_COINS);
        }
        else
        {
            totalCoins= (timeSpendInCall*Constants.VIDEO_CALL_COINS);
        }
        return (long) totalCoins;
    }

    public static double formatDouble(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
        decimalFormat.setRoundingMode(java.math.RoundingMode.HALF_UP);
        String formattedValue = decimalFormat.format(value);
        return Double.parseDouble(formattedValue);
    }
}

package com.example.shubhamgoel.marsplay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by shubhamgoel on 18/05/18.
 */

public class Util {

    public static void showToast(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

    }

    public static void dismissKeyboard(Context activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean available =
                activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!available)
            showToast("Not connected to internet", context);
        return available;
    }
}

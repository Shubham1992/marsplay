package com.example.shubhamgoel.marsplay.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shubhamgoel on 08/05/18.
 */

public class SharedPrefUtil {

    static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

}

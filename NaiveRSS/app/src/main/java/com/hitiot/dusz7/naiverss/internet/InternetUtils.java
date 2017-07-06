package com.hitiot.dusz7.naiverss.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hitiot.dusz7.naiverss.application.MyApplication;

/**
 * Created by dusz7 on 2017/7/4.
 */

public class InternetUtils {

    private static Context context = MyApplication.getContext();

    public static  boolean isNetworkConnected(){
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                return networkInfo.isAvailable();
            }
        }
        return false;

    }

}



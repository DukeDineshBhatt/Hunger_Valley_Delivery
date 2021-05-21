package com.dinesh.hungervalleydelivery;

import android.app.Application;
import android.content.Context;

public class Bean extends Application {

    private static Context context;

    public String baseurl = "https://www.fast2sms.com/";
    public String notificationBaseurl = "https://fcm.googleapis.com/";

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
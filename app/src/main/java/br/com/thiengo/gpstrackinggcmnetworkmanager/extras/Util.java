package br.com.thiengo.gpstrackinggcmnetworkmanager.extras;

import android.content.Context;
import android.content.SharedPreferences;


public class Util {
    private static final String SP = "SP";
    public static final String TRACKING_STATUS = "tracking_status";


    public static void saveSP(Context context, String key, boolean value){
        SharedPreferences sp = context.getSharedPreferences( SP, Context.MODE_PRIVATE );
        sp.edit().putBoolean( key, value ).apply();
    }

    public static boolean retrieveSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences( SP, Context.MODE_PRIVATE );
        return sp.getBoolean( key, false );
    }
}

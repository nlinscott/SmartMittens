package com.linscott.smartmitten.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Nic on 8/14/2015.
 */
public class Debug {


    public static void ShowText(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    public static void Log(String message){
        Log.d("=====DEBUG=====", message);
    }
}

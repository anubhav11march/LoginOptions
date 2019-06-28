package com.example.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class smsBroadcast extends BroadcastReceiver {
    private static OtpListener mlistener = null;
    private String otp = "";

    public static void bindListener(OtpListener listener) {
        Log.v("AAA", "Binded Listener");
        mlistener= listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("AAA", "onReceive called");
        if(SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())){
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()){
                case CommonStatusCodes
                        .SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.v("AAAB", message);
                    if(mlistener!=null){
                        for(int i=17; i<23; i++){
                            otp+=message.charAt(i);
                        }
                        Log.v("AAA", otp);
                        mlistener.messageReceived(otp);
                    }

                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.v("AAA","Timed out");
                    break;
            }
        }
    }
}

package com.example.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.common.internal.service.Common;

public class OtpReceiver extends BroadcastReceiver {
    private static OtpListener mListener;
    private SmsMessage smsMessage;
    String message, code="";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("AAA", "onreceive");

        Bundle data = intent.getExtras();

        Object[] pdus = new Object[0];
        if (data != null) {
            pdus = (Object[]) data.get("pdus");
        }

        if (pdus != null) {
            Log.v("AAA", "pdus!=null");
            for (Object pdu : pdus) { // loop through and pick up the SMS of interest
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                Log.v("AAA", smsMessage.getDisplayOriginatingAddress());
                Log.v("AAA", smsMessage.getOriginatingAddress());
                message = smsMessage.getMessageBody();
                for(int i=0; i<6; i++){
                    code+=message.charAt(i);
                }
            }

                if (mListener!=null)
                    Log.v("AAA", "" + smsMessage.getDisplayOriginatingAddress().equals("59029410"));
                        if (smsMessage.getDisplayOriginatingAddress().equals("59029410"))
                            mListener.messageReceived(code);

        }
    }

    public static void bindListener(OtpListener listener) {
        Log.v("AAA", "Binded Listener");
        mListener = listener;
    }

    public static void unbindListener() {
        mListener = null;
    }

}

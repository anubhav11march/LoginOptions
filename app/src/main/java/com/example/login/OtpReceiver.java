package com.example.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.android.gms.common.internal.service.Common;

public class OtpReceiver extends BroadcastReceiver {
    private static OtpListener mListener;
    String abcd;
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data = intent.getExtras();

        Object[] pdus = new Object[0];
        if (data != null) {
            pdus = (Object[]) data.get("pdus");
        }

        if (pdus != null) {
            for (Object pdu : pdus) { // loop through and pick up the SMS of interest
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

                if (mListener!=null)
                    mListener.messageReceived("Extracted OTP");
                break;
            }
        }
    }

    public static void bindListener(OtpListener listener) {
        mListener = listener;
    }

    public static void unbindListener() {
        mListener = null;
    }

}

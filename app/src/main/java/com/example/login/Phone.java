package com.example.login;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Phone extends AppCompatActivity {
    private EditText phoneNumber, OTPCode;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean inProgress = false;
    private String verificationid;
    private OtpReceiver otpReceiver = new OtpReceiver();
    private int RESOLVE_HINT = 2;
    SmsRetrieverClient smsRetrieverClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_phone);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = (EditText) findViewById(R.id.pnumber);
        phoneNumber.setText("+91");
        OTPCode = (EditText) findViewById(R.id.otp);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Phone.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            ActivityCompat.requestPermissions(Phone.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            ActivityCompat.requestPermissions(Phone.this, new String[]{Manifest.permission.READ_SMS}, 1);

        }
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                inProgress = false;

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                inProgress = false;
            }

            @Override
            public void onCodeSent(String vid, PhoneAuthProvider .ForceResendingToken token){
                verificationid = vid;
                Log.v("AAA", "Code Sent");
                Toast.makeText(Phone.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();



            }
        };
        OtpReceiver.bindListener(new OtpListener() {
            @Override
            public void messageReceived(String messageText) {
                OTPCode.setText(messageText);
                Log.v("AAA", "Messagereceived");
            }
        });

//        currentUser = mAuth.getCurrentUser();
//        smsRetrieverClient = SmsRetriever.getClient(this);
//        Task<Void> task = smsRetrieverClient.startSmsRetriever();
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
////                OTPCode.setText(ssss);
//                Log.v("AAA", "Onsuccess");
//            }
//        });
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
    }





    public void buttonRequestOTP(View view){
        Log.v("AAA", "requestOTP");
        String number = phoneNumber.getText().toString();
        verifyNumber(number);
    }

    public void Signin(View view){
        String otp = OTPCode.getText().toString();
        verifyNumberWithCode(otp, verificationid);
    }


    public void verifyNumber(String phonenumber){
        Log.v("AAA", "verifystart" + phonenumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                10,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
        inProgress = true;
    }
    public void verifyNumberWithCode(String otp, String verificationid){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, otp);
        signInWithNumber(credential);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        otpReceiver.unbindListener();
    }

    public static int f=0;
    public void signInWithNumber(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("AAA", "Sign in Success");
                        }
                        else
                            Log.v("AAA", "Sign in Failed");
                    }
                });
    }
}

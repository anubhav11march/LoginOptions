package com.example.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Others extends AppCompatActivity {
    private GoogleSignInClient mgooglesigninclient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private PhoneAuthProvider .OnVerificationStateChangedCallbacks mCallbacks;
    private boolean inProgress = false;
    private Button requestOTP, signInWithCode;
    private EditText phoneNumber, OTPCode;
    private String verificationid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        //GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("873606793313-qifc7g3te34ji7atnjl28vpmab0gr3d9.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mgooglesigninclient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        Toast.makeText(this, "Signed in as: " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();


        //FACEBOOK LOGIN


        //OTP Login
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        OTPCode = (EditText) findViewById(R.id.otp);
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
            }
        };

        currentUser = mAuth.getCurrentUser();
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

    @Override
    protected void onStart() {
        super.onStart();



    }

    public void googleSignIn(View view){

        Intent signin = mgooglesigninclient.getSignInIntent();
        startActivityForResult(signin, 9001);

    }

    public void Signout(View view){
        if(currentUser == null)
            return;
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(currentUser != null)
            return;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 9001){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthwithGoogle(account);
                Toast.makeText(this, "Signed in from " + account.getEmail(), Toast.LENGTH_SHORT).show();
            }
            catch (ApiException e){
                Log.v("AAA", task.getException().toString());
                Toast.makeText(this, "Failed Login", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  void firebaseAuthwithGoogle(GoogleSignInAccount account){
        Log.v("AAA", "firebaseauthwithgoogle " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("AAA", "Success");
                        }
                        else
                            Log.v("AAA", "Fail");
                    }
                });
    }
}

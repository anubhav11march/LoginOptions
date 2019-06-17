package com.example.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);


    }

    public void clickedDiff(View view){
        startActivity(new Intent(MainActivity.this, Others.class));
    }
    public void clickedRegister(View view){

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("AAA", "User Created");
                                Toast.makeText(MainActivity.this, "User Signed Up Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("AAA", "User Creation Failed");
                                Toast.makeText(MainActivity.this, "User Sign up Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            inputPassword.setText("");
            inputEmail.setText("");
        }
        else {
            Log.d("AAA", "Fields empty");
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }

}

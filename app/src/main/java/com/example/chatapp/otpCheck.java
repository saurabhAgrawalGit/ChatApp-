package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class otpCheck extends AppCompatActivity {
    Button verifyOTP;
    EditText userOTP;
    String check_otp;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_check);
        verifyOTP=findViewById(R.id.verity_btn);
        userOTP=findViewById(R.id.otpNo);
        firebaseAuth=FirebaseAuth.getInstance();
        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_otp=userOTP.getText().toString();
                if(check_otp.isEmpty())
                {
                    Toast.makeText(otpCheck.this, "enter otp", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String otp=getIntent().getStringExtra("otp");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp,check_otp);
                    signWithPhone(credential);
                }
            }
        });
    }

    void signWithPhone(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(otpCheck.this, "successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(otpCheck.this,setProfile.class);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }
}
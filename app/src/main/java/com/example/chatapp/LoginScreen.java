package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginScreen extends AppCompatActivity {

    Button sendOTP;
    EditText user_Number;
    CountryCodePicker countryCodePicker_in;
    String phoneNo;
    String countryCode;
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks ;
    String codesend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        user_Number=findViewById(R.id.userNo);
        countryCodePicker_in=findViewById(R.id.countryCodePicker);
        sendOTP=findViewById(R.id.send_otp);

        firebaseAuth=FirebaseAuth.getInstance();
        countryCode =countryCodePicker_in.getSelectedCountryCodeWithPlus();



        countryCodePicker_in.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode =countryCodePicker_in.getSelectedCountryCodeWithPlus();
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num;
                num=user_Number.getText().toString();
                if(num.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Your Number",Toast.LENGTH_SHORT).show();
                }
                else if(num.length()<10)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter correct Number",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"check funtion",Toast.LENGTH_SHORT).show();
                    phoneNo=countryCode+num;
                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNo)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginScreen.this)
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });



       /* mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codesend=s;
                Intent intent=new Intent(LoginScreen.this,otpCheck.class);
                intent.putExtra("otp",codesend);
                startActivity(intent);

            }
        };

    }*/
        Toast.makeText(getApplicationContext(),"for check",Toast.LENGTH_SHORT).show();
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //how to automatically fetch code here
            Toast.makeText(getApplicationContext(),phoneAuthCredential.toString(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();

        }


        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(),"OTP is Sent",Toast.LENGTH_SHORT).show();
            codesend=s;
            Intent intent=new Intent(LoginScreen.this,otpCheck.class);
            intent.putExtra("otp",codesend);
            Toast.makeText(getApplicationContext(),"complete22",Toast.LENGTH_SHORT).show();
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"complete",Toast.LENGTH_SHORT).show();
        }
    };



}


    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent = new Intent(LoginScreen.this,MainActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
        }
    }
}
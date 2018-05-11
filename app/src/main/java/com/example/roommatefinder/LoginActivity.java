package com.example.roommatefinder;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private AdView mAdView;
    private EditText Name;
    private EditText Password;
    private Button Login, userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog; //apparently this is deprecated, but still works

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize an add instance with your app id - this is testing app id, using your app id
        // for testing can lead to termination of your account
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");

        // Send out ad request and then load ad once received
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Name = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btnLogin);

        userRegistration = findViewById(R.id.btnRegister); //registration activity

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //check to see if user is logged in or not
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null)
        {
            finish(); //destroys this activity , then start Home activity
            //pass current user to next intent

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

    }

    private void validate(String userName, String userPassword)
    {
        progressDialog.setMessage("Loading. Please wait");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

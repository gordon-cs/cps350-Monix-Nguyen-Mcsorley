package com.example.roommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private AdView mAdView;

    private FirebaseAuth firebaseAuth;
    private Button logout;
    private ImageButton profilePage;
    private TextView txtName;

    private FirebaseDatabase firebaseDatabase;
    static UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize an add instance with your app id - this is testing app id, using your app id
        // for testing can lead to termination of your account
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");

        // Send out ad request and then load ad once received
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        firebaseAuth = FirebaseAuth.getInstance(); //get instance of main class
        firebaseDatabase = FirebaseDatabase.getInstance();


        logout = findViewById(R.id.btnLogout);
        profilePage = findViewById(R.id.btnProfile);
        txtName = findViewById(R.id.txtName);


        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userProfile = dataSnapshot.getValue(UserProfile.class);
                        txtName.setText(userProfile.getUserName());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });




    }

    private void downloadProfilePic()
    {
        txtName = findViewById(R.id.txtName);

    }

}

package com.example.roommatefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ProfileActivity extends AppCompatActivity {

    private AdView mAdView;

    private ImageView profilePic;
    private TextView profileName, profileClass, profileEmail, profileGender;
    private Button profileUpdate;

    //1st step: import firebase auth and database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize an add instance with your app id - this is testing app id, using your app id
        // for testing can lead to termination of your account
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");

        // Send out ad request and then load ad once received
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        profileName = findViewById(R.id.tvProfileName);
        profileEmail = findViewById(R.id.tvProfileEmail);
        profileClass = findViewById(R.id.tvProfileClass);
        profileGender = findViewById(R.id.tvProfileGender);

        //2nd step: get instance of firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //3rd step: create reference to database, give it object name
        //parameter should be userID
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        //4th step: retrieve data with ValueEventListener
        //https://firebase.google.com/docs/database/admin/retrieve-data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //when theres data change or when app starts
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                profileName.setText(userProfile.getUserName());
                profileEmail.setText(userProfile.getUserEmail());
                profileClass.setText(userProfile.getUserClass());
                profileGender.setText(userProfile.getUserGender());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}

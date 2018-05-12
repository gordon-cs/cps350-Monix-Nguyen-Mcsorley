package com.example.roommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private AdView mAdView;

    private FirebaseAuth firebaseAuth;
    private Button logout;
    private ImageButton profilePage;
    private TextView txtName;
    private ListView myListView;
    private FirebaseListAdapter adapter;

    //ArrayList<String> myArrayList = new ArrayList<>();


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
        profilePage = findViewById(R.id.btnProfile);

        myListView = findViewById(R.id.displayList);
        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<UserProfile> options = new FirebaseListOptions.Builder<UserProfile>()
                .setLayout(R.layout.gordon)
                //.setLifecycleOwner(HomeActivity.this)
                .setQuery(query, UserProfile.class)
                .build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView usrName = v.findViewById(R.id.userName);
                TextView usrEmail = v.findViewById(R.id.userEmail);
                TextView usrClass = v.findViewById(R.id.userClass);
                TextView usrGender = v.findViewById(R.id.userGender);

                UserProfile usrProfile = (UserProfile) model;
                usrName.setText(" Name: " + usrProfile.getUserName().toString());
                usrEmail.setText(" \n Email: " + usrProfile.getUserEmail().toString());
                usrClass.setText(" Class: " + usrProfile.getUserClass().toString());
                usrGender.setText(" Gender: " + usrProfile.getUserGender().toString());
            }
        };

        myListView.setAdapter(adapter);

//        //myListView.setClickable(true);





        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfile model = (UserProfile) parent.getItemAtPosition(position);
                String user = model.getUserName();
                String email = model.getUserEmail();
                String classYear = model.getUserClass();
                String gender = model.getUserGender();


               // Toast.makeText(getApplicationContext(), "name here PLEASE: "+ classYear, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(HomeActivity.this, SpecificUserActivity.class);

                intent.putExtra("1", user);
                intent.putExtra("2", email);
                intent.putExtra("3", classYear);
                intent.putExtra("4", gender);
                startActivity(intent);
            }
        });


        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userProfile = dataSnapshot.getValue(UserProfile.class);
                        txtName.setText(userProfile.getUserName());

//                        //loop through the users based on male or female
//                        for (DataSnapshot d: dataSnapshot.getChildren()) {
//
//                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {}
                });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference usersdRef = rootRef.child("roomatefinder-71ee7");
//
//
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                DataSnapshot userSnapshot = dataSnapshot;
//                Iterable<DataSnapshot> nameSnapshot = userSnapshot.getChildren();
//                ArrayList<String> allUsers = new ArrayList<>();
//
//
//                for (DataSnapshot d: nameSnapshot) {
//                    String name = d.child("userName").getValue(String.class);
//                    Log.d("name is: ", name);
//                    allUsers.add(name);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        };
//        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void downloadProfilePic()
    {
        txtName = findViewById(R.id.txtName);
    }

}

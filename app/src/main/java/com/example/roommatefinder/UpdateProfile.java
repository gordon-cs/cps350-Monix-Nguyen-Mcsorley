package com.example.roommatefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUserName, newUserEmail, newUserClass, newUserGender;
    private Button save;

    //1st step: import firebase auth and database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        newUserName = findViewById(R.id.etNameUpdate);
        newUserEmail = findViewById(R.id.etEmailUpdate);
        newUserClass = findViewById(R.id.etClassUpdate);
        newUserGender = findViewById(R.id.etGenderUpdate);
        save = findViewById(R.id.btnSave);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //2nd step: get instance of firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //3rd step: create reference to database, give it object name
        //parameter should be userID
        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        //4th step: retrieve data with ValueEventListener
        //https://firebase.google.com/docs/database/admin/retrieve-data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //when theres data change or when app starts
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                newUserName.setText(userProfile.getUserName());
                newUserEmail.setText(userProfile.getUserEmail());
                newUserClass.setText(userProfile.getUserClass());
                newUserGender.setText(userProfile.getUserGender());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newUserName.getText().toString();
                String email = newUserEmail.getText().toString();
                String classYear = newUserClass.getText().toString();
                String gender = newUserGender.getText().toString();
                //need to add a way to put current picture uri into updated profile


                //name -> email -> class -> gender in that order
                UserProfile userProfile = new UserProfile(name, email, classYear, gender, null);

                databaseReference.setValue(userProfile);

                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: //when click on top direct to previous act
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

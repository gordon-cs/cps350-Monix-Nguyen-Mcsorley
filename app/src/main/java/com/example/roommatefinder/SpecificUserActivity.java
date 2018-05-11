package com.example.roommatefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificUserActivity extends AppCompatActivity {

    private TextView specificName;
    private TextView specificEmail;
    private TextView specificClass;
    private TextView specificGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user);

        String spName = getIntent().getStringExtra("1");
        String spEmail = getIntent().getStringExtra("2");
        String spClass = getIntent().getStringExtra("3");
        String spGender = getIntent().getStringExtra("4");


        specificName = findViewById(R.id.tvSpecificName);
        specificEmail = findViewById(R.id.tvSpecificEmail);
        specificClass = findViewById(R.id.tvSpecificClass);
        specificGender = findViewById(R.id.tvSpecificGender);

        specificName.setText(spName);
        specificEmail.setText(spEmail);
        specificClass.setText(spClass);
        specificGender.setText(spGender);
    }
}

package com.example.roommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button logout, profilePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance(); //get instance of main class

        logout = (Button) findViewById(R.id.btnLogout);

        profilePage = (Button) findViewById(R.id.btnProfile);

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



        Intent intent = getIntent();

        String userName = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");


        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Username: " + userName + "\n" + "Password: " + password + " \n" + "\n");


        /*
            Using Volley to make a GET Request
            How it Works:
                Volley stores requests in RequestQueue
                StringRequest makes and holds the request:
                    The first two parameters are used for making the request
                    The last two parameters are listeners waiting on the response of the server
                    if there is a valid response then onResponse is called, else an error was thrown

            Dependencies:
            Add this line to your gradle dependencies for your Android project's app module:
                    compile 'com.android.volley:volley:1.0.0'

            Sources:
                https://stackoverflow.com/questions/20059576/import-android-volley-to-android-studio
                https://developer.android.com/training/volley/simple.html
                https://afzaln.com/volley/com/android/volley/toolbox/StringRequest.html
         */
        RequestQueue queue = Volley.newRequestQueue(this);
        String weatherURL = "https://api.darksky.net/forecast/c7a0bbc2b027787365af6e16179330a4/42.589611,-70.819806";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherURL, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                textView.append(" Response is: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("Did not work.");
            }

        });

        queue.add(stringRequest);
    }

}

package com.example.roommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();

        String userName = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");



        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Username: " + userName + "\n" + "Password: " + password + " \n" + "\n");


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

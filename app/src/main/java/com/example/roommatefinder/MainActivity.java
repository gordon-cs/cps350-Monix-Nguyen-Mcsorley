package com.example.roommatefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private Button Login;
    private int counter = 5;
    private HashMap users = new HashMap();

    private TextView userRegistration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);

        userRegistration = (TextView) findViewById(R.id.tvRegister);



        users.put("admin","1234");
        users.put("max","moniz");
        users.put("sam", "nguyen");



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

    }

    private void validate(String userName, String userPassword)
    {
//        if ((userName.equals("admin")) && (userPassword.equals("1234")))
        for(int i = 0; i< this.users.size(); i++)
        {
            if ( users.containsKey(userName) && users.containsValue(userPassword) ) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("username", userName);
                intent.putExtra("password", userPassword);
                startActivity(intent);
            }
            else
            {
                Toast toast = Toast.makeText(getBaseContext(),"username and password combination doesn't exist",Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}

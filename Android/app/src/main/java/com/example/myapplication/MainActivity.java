package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




    public void goToHostActivity(View view) {
        startActivity(new Intent(MainActivity.this, HostActivity.class));
    }

    public void goToFollowerActivity(View view) {
        startActivity(new Intent(MainActivity.this, HostActivity.class));
    }




}

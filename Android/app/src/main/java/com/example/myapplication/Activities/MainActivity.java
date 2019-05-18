package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.myapplication.R;

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
        startActivity(new Intent(MainActivity.this, FollowerActivity.class));
    }

    




}

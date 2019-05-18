package com.example.myapplication.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;

public class FollowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
    }

    // TODO reset back to main
    public void goToMainActivity(View view) {
        startActivity(new Intent(FollowerActivity.this, FollowerRecordActivity.class));
    }

}

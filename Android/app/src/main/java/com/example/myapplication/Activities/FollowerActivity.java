package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowerActivity extends AppCompatActivity {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = db.getReference("/");
    private List<String> hosts = new ArrayList<>();

    private LinearLayout layout;

    private void getHosts() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hosts = (ArrayList<String>) dataSnapshot.getValue();
                Log.i("FollowerActivity", ", prebrali smo snapshot.");
                Log.i("FollowerActivity", hosts.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FollowerActivity", "The read failed: " + databaseError.getMessage());
            }
        };
    }

    private void addHosts() {
        getHosts();
        for (String host : hosts) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        layout = findViewById(R.id.linearLayoutHosts);
    }

    public void goToMainActivity(View view) {
        startActivity(new Intent(FollowerActivity.this, MainActivity.class));
    }

    public void goToFollowerRecordActivity(View view) {
        startActivity(new Intent(FollowerActivity.this, FollowerRecordActivity.class));
    }

}

package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowerActivity extends AppCompatActivity {

    private void addHostToLayout(String name, LinearLayout layout) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(this);
        textView.setText(name);
        itemLayout.addView(textView);

        ImageButton btn = new ImageButton(this);
        btn.setImageResource(R.drawable.join);
        itemLayout.addView(btn);

        layout.addView(itemLayout);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("/");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LinearLayout layoutHosts = findViewById(R.id.linearLayoutHosts);

                layoutHosts.removeAllViews();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    addHostToLayout(ds.getKey(), layoutHosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FollowerActivity", "The read failed: " + databaseError.getMessage());
            }
        };

        myRef.addValueEventListener(listener);

    }

    public void goToMainActivity(View view) {
        startActivity(new Intent(FollowerActivity.this, MainActivity.class));
    }

    public void goToFollowerRecordActivity(View view) {
        startActivity(new Intent(FollowerActivity.this, FollowerRecordActivity.class));
    }

}

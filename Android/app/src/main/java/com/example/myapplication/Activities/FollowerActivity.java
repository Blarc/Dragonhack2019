package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String imeNaprave = getDeviceName();
    String pot;
    String imeHosta;
    private void addHostToLayout(String name, LinearLayout layout) {
        LinearLayout itemLayout = new LinearLayout(this);
        final String hostName = name;
        Button btn = new Button(this);
        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imeHosta = hostName;
                Log.i("MyApp", "Ime izbranega hosta je: " + imeHosta);
                String pot = imeHosta + "/" + imeNaprave;
                sendMessage(pot, "Follower");
                goToWaitActivity(view, hostName);
            }
        });
        itemLayout.addView(btn);
        layout.addView(itemLayout);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        myRef = db.getReference("/");

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
    //poslje sporocilo str na direktorij pot
    public void sendMessage(String pot, String str) {
        //reference je pot na bazi
        myRef = db.getReference(pot);
        myRef.setValue(str);
    }

    //vrne ime naprave/telefona
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
    //spremeni ime v caps
    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public void pridruziSession(View view, String name){

    }

    public void goToWaitActivity(View view, String hostName) {
        Intent intent = new Intent(FollowerActivity.this, WaitForFile.class);
        intent.putExtra("hostName", hostName);
        startActivity(intent);
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(FollowerActivity.this, MainActivity.class);
        startActivity(intent);
    }


}

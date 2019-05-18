package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HostActivity extends AppCompatActivity {

    //naredi povezavo s FireBase
    private String imeNaprave;
    private String pot;
    private FirebaseDatabase db;
    private List<String> podatki;
    //ko se nalozi screen hosta, se izvede funkcija, ki poslje firebase-u ime trenutne naprave
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        imeNaprave = getDeviceName();
        pot = "message/";
        //izpisemo ime naprave
        TextView ime = (TextView)findViewById(R.id.imeNaprave);
        ime.setText(imeNaprave);

        podatki = new ArrayList<String>();
        //sendMessage(this.imeNaprave, "Host");
        Log.i(null, "Testis1234");

        //zacnemo proces branja podatkov iz baze
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(pot);
        // dodamo listener, ki poslusa na mapi ime/
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                podatki = (ArrayList<String>) dataSnapshot.getValue();
                Log.i("HostActivity", "prebrali snapshot: ");
                Log.i("HostActivity", podatki.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HostActivity", "The read failed: " + databaseError.getMessage());
            }
        };
        ref.addValueEventListener(listener);

        //izpisemo vse elemente v podatkih


    }

    //spremeni activity nazaj na izbiro med Host in Follower
    public void goToMainActivity(View view) {
        startActivity(new Intent(HostActivity.this, MainActivity.class));
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
    //nastavi value na poti pot na str
    public void sendMessage(String pot, String str) {
        //reference je pot na bazi
        DatabaseReference myRef = db.getReference(pot);
        myRef.setValue(str);
    }

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
    //razred, v katerega shranimo podatke
    public class Podatki{
        String[] imena;
        int stevilo;

        public Podatki(String[] imena, int stevilo){
            this.imena = imena;
            this.stevilo = stevilo;
        }
    }
}

package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myapplication.Classes.AudioRecorder;
import com.example.myapplication.Classes.FileUploader;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.List;

public class HostActivity extends AppCompatActivity {

    //naredi povezavo s FireBase
    private String imeNaprave;
    private String pot;
    private FirebaseDatabase db;
    private Map<String, String> podatki;
    DatabaseReference ref;
    private boolean isRecording = false;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private FileUploader fileUploader;

    private AudioRecorder audioRecorder;

    private boolean startRecording = true;


    //ko se nalozi screen hosta, se izvede funkcija, ki poslje firebase-u ime trenutne naprave
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        imeNaprave = getDeviceName();
        pot = imeNaprave;
        //izpisemo ime naprave
        TextView ime = (TextView)findViewById(R.id.imeNaprave);
        ime.setText(imeNaprave);


        audioRecorder = new AudioRecorder(this, imeNaprave);
        fileUploader = new FileUploader();
        podatki = new HashMap<>();
        //najprej posljemo svoje ime na bazo
        db = FirebaseDatabase.getInstance();
        sendMessage(this.imeNaprave, "Host");
        Log.i(null, "Testis1234");

        //zacnemo proces branja podatkov iz baze
        ref = db.getReference(pot);
        // dodamo listener, ki poslusa na mapi ime/
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() instanceof HashMap){
                    podatki = (HashMap<String, String>) dataSnapshot.getValue();
                    Log.i("MyApp", "prebrali snapshot: ");
                    Log.i("MyApp", podatki.toString());
                    izpisiFollowerje();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HostActivity", "The read failed: " + databaseError.getMessage());
            }
        };
        ref.addValueEventListener(listener);

        ref = db.getReference(imeNaprave + "/Start");

        ValueEventListener listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i("MyApp", "Zacnemo snemanje ");
                    toggleRecording();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HostActivity", "The read failed: " + databaseError.getMessage());
            }
        };
        ref.addValueEventListener(listener2);

        ref = db.getReference(imeNaprave + "/Stop");

        ValueEventListener listener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i("MyApp", "Zakljucimo s snemanjem ");
                    toggleRecording();
                    prenesi();
                    goToMainActivity();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HostActivity", "The read failed: " + databaseError.getMessage());
            }
        };
        ref.addValueEventListener(listener3);

    }
    //odstrani null vrednosti iz podatkov
    public void odstraniNull(){
        Map<String, String> novo = new HashMap<>();
        for(Map.Entry<String, String> podatek : podatki.entrySet()){
            if(podatek.getValue() != null){
                novo.put(podatek.getKey(),podatek.getValue());
            }
        }
        podatki = novo;
    }

    //izpisemo vse elemente v podatkih
    public void izpisiFollowerje(){
        izbrisiFollowerje();
        odstraniNull();
        LinearLayout prostor = (LinearLayout) findViewById(R.id.mainLinearLayout);
        for(Map.Entry<String, String> podatek : podatki.entrySet()){
            if(podatek.getValue() != null && !podatek.getKey().equals("Start") && !podatek.getKey().equals("Stop")){
                TextView tv = new TextView(this);
                tv.setText(podatek.getKey());
                prostor.addView(tv);
                Log.i("MyApp", "Dodali textview s tekstom " + tv.getText());
            }
        }
    }
    //izbrisemo vse elemente v podatkih
    public void izbrisiFollowerje(){
        LinearLayout prostor = (LinearLayout) findViewById(R.id.mainLinearLayout);
        prostor.removeAllViews();
        Log.i("MyApp", "Odstranili vse viewe");
    }

    //spremeni activity nazaj na izbiro med Host in Follower
    public void goToMainActivity(View view) {
        startActivity(new Intent(HostActivity.this, MainActivity.class));
    }
    public void goToMainActivity() {
        startActivity(new Intent(HostActivity.this, MainActivity.class));
    }

    public void goToWaitActivity(View view) {
        startActivity(new Intent(HostActivity.this, WaitForFile.class));
    }

    //glede na trenutno stanje gumba
    public void toggleRecord(View view){
        String izpis = "Start";
        if(isRecording){
            izpis = "Stop";
        }
        sendMessage(this.imeNaprave + "/" + izpis, "Host");
        Button gumb = (Button) findViewById(R.id.toggleStart);
        if(isRecording == false){
            isRecording = true;
            gumb.setText("Stop recording");
        } else {
            gumb.setVisibility(View.GONE);
            goToWaitActivity(view);
        }


    }

    private void onRecord(boolean start) {
        if (start) {
            audioRecorder.startRecording();
        } else {
            audioRecorder.stopRecording();
        }
    }

    public void toggleRecording() {
        onRecord(startRecording);
        if (!startRecording) {
            fileUploader.upload(audioRecorder.getFilePath());
        }
        startRecording = !startRecording;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    public void prenesi(){
        StorageReference sref = storage.getReferenceFromUrl("gs://micitup-ff7ce.appspot.com/audio/improved.mp3");
        try {
            final File localFile = File.createTempFile("improved", "mp3");
            sref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.i("WaitForFile","File downloaded at: " + localFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.i("WaitForFile","File NOT downloaded");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        audioRecorder.close();
    }

    public void deleteData(String pot){
        ref = db.getReference(pot);
        ref.removeValue();
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
        ref = db.getReference(pot);
        ref.setValue(str);
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

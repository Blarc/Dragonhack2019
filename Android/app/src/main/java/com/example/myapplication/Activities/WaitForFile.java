package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;

public class WaitForFile extends AppCompatActivity {
    private String hostName;
    private boolean permissionToRecordAccepted = false;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private FileUploader fileUploader;

    private AudioRecorder audioRecorder;

    private boolean startRecording = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_file);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        audioRecorder = new AudioRecorder(this, hostName);
        fileUploader = new FileUploader();
        hostName = getIntent().getStringExtra("hostName");
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(hostName + "/Start");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i("MyApp", "Zacnemo snemanje ");
                    TextView tv = (TextView) findViewById(R.id.loading);
                    tv.setText("Recording...");
                    toggleRecording();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HostActivity", "The read failed: " + databaseError.getMessage());
            }
        };
        ref.addValueEventListener(listener);

        ref = db.getReference(hostName + "/Stop");

        ValueEventListener listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.i("MyApp", "Zakljucimo s snemanjem ");
                    TextView tv = (TextView) findViewById(R.id.loading);
                    tv.setText("Waiting for file...");
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
        ref.addValueEventListener(listener2);
    }

    public void goToMainActivity() {
        startActivity(new Intent(WaitForFile.this, MainActivity.class));
    }

    // to be deleted
    //private Button recordButton;

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

}

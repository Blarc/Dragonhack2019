package com.example.myapplication.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.Classes.AudioRecorder;
import com.example.myapplication.Classes.FileUploader;
import com.example.myapplication.R;

public class FollowerRecordActivity extends AppCompatActivity {

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private FileUploader fileUploader;

    private AudioRecorder audioRecorder;
    private boolean startRecording = true;

    // to be deleted
    private Button recordButton;

    private void onRecord(boolean start) {
        if (start) {
            audioRecorder.startRecording();
        } else {
            audioRecorder.stopRecording();
        }
    }

    public void recordButtonOnClick(View view) {
        onRecord(startRecording);
        if (startRecording) {
            recordButton.setText("Stop recording");

        } else {
            recordButton.setText("Start recording");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_follower_record);

        audioRecorder = new AudioRecorder(this);
        fileUploader = new FileUploader();
        recordButton = findViewById(R.id.recordButton);
    }

    @Override
    public void onStop() {
        super.onStop();
        audioRecorder.close();
    }
}

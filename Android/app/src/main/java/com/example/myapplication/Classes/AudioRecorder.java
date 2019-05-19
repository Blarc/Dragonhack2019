package com.example.myapplication.Classes;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AudioRecorder {
    private static final String LOG_TAG = "AudioRecorder";
    private static String filePath = null;
    private MediaRecorder recorder = null;
    private int stevec = 0;

    public AudioRecorder(@NotNull AppCompatActivity activity, String hostName) {
        stevec++;
        filePath = activity.getExternalCacheDir().getAbsolutePath();
        //filePath += "/" + hostName + "/recording" + stevec + ".mp3";
        filePath += "/" + hostName + stevec + ".mp4";
    }

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void close() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

}

//    public void recordButtonOnClick(View view) {
//
//        onRecord(mStartRecording);
//        if (mStartRecording) {
//            recordButton.setText("Stop recording");
//        } else {
//            recordButton.setText("Start recording");
//        }
//        mStartRecording = !mStartRecording;
//    }

//    // Requesting permission to RECORD_AUDIO
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private boolean permissionToRecordAccepted = false;
//    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
//            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//        }
//        if (!permissionToRecordAccepted ) finish();
//
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Record to the external cache directory for visibility
//        fileName = getExternalCacheDir().getAbsolutePath();
//        fileName += "/audiorecordtest.3gp";
//
//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
//
//        setContentView(R.layout.activity_audio_record);
//
//        recordButton = findViewById(R.id.recordButton);
//        playButton = findViewById(R.id.playButton);
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (recorder != null) {
//            recorder.release();
//            recorder = null;
//        }
//
//        if (player != null) {
//            player.release();
//            player = null;
//        }
//    }

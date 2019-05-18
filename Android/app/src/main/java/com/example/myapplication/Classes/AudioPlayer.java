package com.example.myapplication.Classes;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AudioPlayer {

    private static final String LOG_TAG = "AudioPlayer";
    private static String filePath = null;

    private MediaPlayer player = null;

    public AudioPlayer(@NotNull String path) {
        filePath = path;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void close() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

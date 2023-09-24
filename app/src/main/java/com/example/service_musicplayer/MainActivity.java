package com.example.service_musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private PlayMusicService mService;
    private boolean isBound = false;
    private Button playPause;
    private boolean isPlaying = false;
    private SeekBar progressBar;
    private MyReceiver myReceiver;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PlayMusicService.LocalBinder binder = (PlayMusicService.LocalBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPause = findViewById(R.id.play_or_pause);
        isPlaying = false;
        playPause.setText("播放");

        progressBar = findViewById(R.id.prgress);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mService.setPosition(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayMusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("fromService");
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(mConnection);
            isBound = false;
        }

        unregisterReceiver(myReceiver);
    }

    public void playOrPause(View view) {
        isPlaying = !isPlaying;
        if (!isPlaying){
            playPause.setText("播放");
            mService.pauseMusic();
        }else{
            playPause.setText("暫停");
            mService.playMusic();
        }
    }

    public void stopPlay(View view) {
        isPlaying = false;
        playPause.setText("播放");

        mService.stopMusic();
        mService.setPosition(0);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wherenow = intent.getIntExtra("wherenow", -1);
            if (wherenow >= 0){
                progressBar.setProgress(wherenow);
            }

            int max = intent.getIntExtra("max", -1);
            if (max >= 0){
                progressBar.setMax(max);
            }
        }
    }
}
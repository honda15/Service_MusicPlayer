package com.example.service_musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PlayMusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public class LocalBinder extends Binder {
        PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.japen_song);

        Intent intent = new Intent("fromService");
        intent.putExtra("max", mediaPlayer.getDuration());
        sendBroadcast(intent);

        timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 500);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
                Intent intent = new Intent("fromService");
                intent.putExtra("wherenow", mediaPlayer.getCurrentPosition());
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void playMusic(){
        mediaPlayer.start();
    }

    public void pauseMusic(){
        mediaPlayer.pause();
    }

    public void stopMusic(){
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        Intent intent = new Intent("fromService");
        intent.putExtra("wherenow", mediaPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

    public void setPosition(int pos){
        mediaPlayer.seekTo(pos);
    }

}
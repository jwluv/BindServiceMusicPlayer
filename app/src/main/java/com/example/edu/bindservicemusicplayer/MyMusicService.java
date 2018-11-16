package com.example.edu.bindservicemusicplayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MyMusicService extends Service {

    MediaPlayer mPlayer;

    int[] music_list = {R.raw.media_1, R.raw.media_2, R.raw.media_3, R.raw.media_4, R.raw.media_5};
    String[] str_music_list = {"media_1.mp3", "media_2.mp3", "media_3.mp3", "media_4.mp3", "media_5.mp3"};
    int music_list_position = 0;

    private MyMusicService mServiceBinder;
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceBinder=((MyMusicService.MyBinder) binder).getService();
        }
        public void onServiceDisconnected(ComponentName className) { mServiceBinder = null; }
    };

    public MyMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }

    public class MyBinder extends Binder {
        MyMusicService getService() {
            return MyMusicService.this;
        }
    }

    public void play() {
        mPlayer = MediaPlayer.create(this, R.raw.media_1);
        mPlayer.setLooping(true);
        mPlayer.setVolume(80, 80);
        mPlayer.start();
    }

    public void stop() {
        mPlayer.pause();
        mPlayer.seekTo(0);
    }

    public String[] getPlayList() {
        return str_music_list;
    }

}

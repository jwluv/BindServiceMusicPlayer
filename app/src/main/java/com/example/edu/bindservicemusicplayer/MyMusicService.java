package com.example.edu.bindservicemusicplayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MyMusicService extends Service {

    MediaPlayer mPlayer;

    int[] music_list = {R.raw.media_1, R.raw.media_2, R.raw.media_3, R.raw.media_4, R.raw.media_5};
    String[] str_music_list = {"media_1.mp3", "media_2.mp3", "media_3.mp3", "media_4.mp3", "media_5.mp3"};
    int music_list_position = 0;
    String title = "", artist = "";

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    public class MyBinder extends Binder {
        MyMusicService getService() {
            return MyMusicService.this;
        }
    }

    public void play(int playlist_position) {

        setTitleArtist(playlist_position);
        this.music_list_position = playlist_position;

        if(mPlayer != null && mPlayer.isPlaying()) mPlayer.stop();

        mPlayer = MediaPlayer.create(this, music_list[playlist_position]);
        mPlayer.setLooping(true);
        mPlayer.setVolume(80, 80);
        mPlayer.start();
    }

    public void play_previous() {
        if(this.music_list_position == 0)
            this.music_list_position = music_list.length - 1;
        else
            this.music_list_position -= 1;

        setTitleArtist(this.music_list_position);

        if(mPlayer != null && mPlayer.isPlaying()) mPlayer.stop();

        mPlayer = MediaPlayer.create(this, music_list[this.music_list_position]);
        mPlayer.setLooping(true);
        mPlayer.setVolume(80, 80);
        mPlayer.start();
    }

    public void play_next() {
        if(this.music_list_position == music_list.length - 1)
            this.music_list_position = 0;
        else
            this.music_list_position += 1;

        setTitleArtist(this.music_list_position);

        if(mPlayer != null && mPlayer.isPlaying()) mPlayer.stop();

        mPlayer = MediaPlayer.create(this, music_list[this.music_list_position]);
        mPlayer.setLooping(true);
        mPlayer.setVolume(80, 80);
        mPlayer.start();
    }
    public void resume() {
        mPlayer.start();
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.pause();
        mPlayer.seekTo(0);
    }

    public String[] getPlayList() {
        return str_music_list;
    }

    public int getPlayingIndex() {
        return music_list_position;
    }

    public void setTitleArtist(int index) {
        Resources res = getResources();
        String pkgName = getPackageName();

        String str_id = "media_" + String.valueOf(index+1);
        int identifier = res.getIdentifier(str_id, "raw", pkgName);
        AssetFileDescriptor afd = res.openRawResourceFd(identifier);

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }

}

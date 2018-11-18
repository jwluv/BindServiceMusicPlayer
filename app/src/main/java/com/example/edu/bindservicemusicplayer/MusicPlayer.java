package com.example.edu.bindservicemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener{

    Button buttonPlayPause, buttonStop;
    Button buttonPrevious, buttonNext;
    RecyclerView recyclerViewPlaylist;
    RecyclerView.LayoutManager layoutManager;
    PlayListAdapter playlistAdapter;
    String[] playList = null;

    int play_state = 0; //0: No play, 1: Play, 2: Pause

    private MyMusicService mServiceBinder;
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceBinder=((MyMusicService.MyBinder) binder).getService();
        }
        public void onServiceDisconnected(ComponentName className) { mServiceBinder = null; }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        startService(new Intent(this, MyMusicService.class));

        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);

        recyclerViewPlaylist = findViewById(R.id.recyclerViewPlaylist);

        buttonPlayPause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        arrayList = getPlaylist();
        layoutManager = new LinearLayoutManager(this);
        recyclerViewPlaylist.setLayoutManager(layoutManager);

        playlistAdapter = new PlayListAdapter(this, arrayList);

        recyclerViewPlaylist.setAdapter(playlistAdapter);

        recyclerViewPlaylist.setNestedScrollingEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonPlayPause:
               if(play_state==1)
                    pause();
                else
                    play(0, play_state);
                break;
            case R.id.buttonStop:
                stop();
                break;
            case R.id.buttonPrevious:
                play_previous();
                break;
            case R.id.buttonNext:
                play_next();
                break;

        }
    }

    public void play(int index, int play_state) {

        playlistAdapter.loadPlayingImage(index);
        if(play_state == 0) {
            this.play_state = 1;
            mServiceBinder.play(index);
        }
        else if(play_state == 2) {
            this.play_state = 1;
            mServiceBinder.resume();
        }
        buttonPlayPause.setBackgroundResource(R.drawable.button_pause);

    }

    public void pause() {
        play_state = 2;
        mServiceBinder.pause();
        playlistAdapter.loadPauseImage(mServiceBinder.getPlayingIndex());
        buttonPlayPause.setBackgroundResource(R.drawable.button_play);
    }

    public void stop() {
        playlistAdapter.loadPlayingImage(-1);
        play_state = 0;
        mServiceBinder.stop();
        buttonPlayPause.setBackgroundResource(R.drawable.button_play);
    }

    public void play_previous(){
        play_state = 1;
        mServiceBinder.play_previous();
        playlistAdapter.loadPlayingImage(mServiceBinder.getPlayingIndex());
        buttonPlayPause.setBackgroundResource(R.drawable.button_pause);
    }
    public void play_next(){
        play_state = 1;
        mServiceBinder.play_next();
        playlistAdapter.loadPlayingImage(mServiceBinder.getPlayingIndex());
        buttonPlayPause.setBackgroundResource(R.drawable.button_pause);
    }
    public ArrayList<HashMap<String,Object>> getPlaylist() {
        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        //String[] play_list = mServiceBinder.getPlayList();
        String[] str_music_list = {"media_1.mp3", "media_2.mp3", "media_3.mp3", "media_4.mp3", "media_5.mp3"};
        String[] play_list = str_music_list;

        //https://www.codota.com/code/java/methods/android.media.MediaPlayer/setDataSource
        Resources res = getResources();
        String pkgName = getPackageName();

        for(int i=0; i<play_list.length; i++) {
            String str_id = "media_" + String.valueOf(i+1);
            int identifier = res.getIdentifier(str_id, "raw", pkgName);
            AssetFileDescriptor afd = res.openRawResourceFd(identifier);

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            String artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            HashMap<String,Object> hashMap = new HashMap<String,Object>();
            hashMap.put("artist", artist);
            hashMap.put("image", R.drawable.not_playing);
            hashMap.put("title", title);
            arrayList.add(hashMap);
        }
        return arrayList;
    }


}

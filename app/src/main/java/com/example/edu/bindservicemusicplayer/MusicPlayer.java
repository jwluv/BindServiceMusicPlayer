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

    Button buttonPlay, buttonStop;
    RecyclerView recyclerViewPlaylist;
    RecyclerView.LayoutManager layoutManager;
    PlayListAdapter playlistAdapter;
    String[] playList = null;

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

        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonStop = findViewById(R.id.buttonStop);

        recyclerViewPlaylist = findViewById(R.id.recyclerViewPlaylist);

        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        arrayList = getPlaylist();
        layoutManager = new LinearLayoutManager(this);
        recyclerViewPlaylist.setLayoutManager(layoutManager);

        playlistAdapter = new PlayListAdapter(this, arrayList);

        recyclerViewPlaylist.setAdapter(playlistAdapter);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonPlay:
                play(0);
                break;
            case R.id.buttonStop:
                mServiceBinder.stop();
                break;

        }
    }

    public void play(int index) {
        mServiceBinder.play(index);
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
            hashMap.put("image", R.drawable.images);
            hashMap.put("title", title);
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    public void getPlayingTitle() {

    }
}

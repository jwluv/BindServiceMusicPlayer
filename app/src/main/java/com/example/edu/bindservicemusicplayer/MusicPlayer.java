package com.example.edu.bindservicemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

    Button buttonPlay, buttonStop, buttonGetPlaylist;
    RecyclerView recyclerViewPlaylist;
    RecyclerView.LayoutManager layoutManager;
    PlayListAdapter playlistAdapter;
    String[] playList=null;

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
        buttonGetPlaylist = findViewById(R.id.buttonGetPlaylist);
        recyclerViewPlaylist = findViewById(R.id.recyclerViewPlaylist);

        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonGetPlaylist.setOnClickListener(this);

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();

        layoutManager = new LinearLayoutManager(this);
        recyclerViewPlaylist.setLayoutManager(layoutManager);

        playlistAdapter = new PlayListAdapter(this, arrayList);

        recyclerViewPlaylist.setAdapter(playlistAdapter);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonPlay:
                mServiceBinder.play();
                break;
            case R.id.buttonStop:
                mServiceBinder.stop();
                break;
            case R.id.buttonGetPlaylist:
                playList = mServiceBinder.getPlayList();

                for(int i=0; i<playList.length; i++)
                    addPlaylist(playList[i]);
                break;
        }
    }

    public void addPlaylist(String title) {
        HashMap<String,Object> hashMap = new HashMap<String,Object>();
        hashMap.put("title", title);
        hashMap.put("detail", "");
        hashMap.put("image", R.drawable.images);

        playlistAdapter.addItem(20,hashMap);
    }
}

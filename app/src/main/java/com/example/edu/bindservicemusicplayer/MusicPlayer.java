package com.example.edu.bindservicemusicplayer;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener{

    final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 0;
    int writeExternalStoragePermission;

    Button buttonPlayPause, buttonStop;
    Button buttonPrevious, buttonNext;
    RecyclerView recyclerViewPlaylist;
    SeekBar seekBarVolume;
    TextView textViewVolume;
    RecyclerView.LayoutManager layoutManager;
    PlayListAdapter playlistAdapter;
    String[] playList = null;
    int volume = 7;

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

        // Check whether this app has write external storage permission or not.
        writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        startService(new Intent(this, MyMusicService.class));

        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);

        recyclerViewPlaylist = findViewById(R.id.recyclerViewPlaylist);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarVolume.setProgress(volume);

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


        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = seekBar.getProgress();
                mServiceBinder.setVolume(volume);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
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

//    private void doBindService() {
//        Intent intent = new Intent(this, MyMusicService.class);
//        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    private void doUnBindService() {
//        if (mServiceBinder != null) {
////            mIsPlaying = mServiceBinder.isPlaying();
//            unbindService(myConnection);
////            myServiceBinder = null;
//        }
//    }
//
//    private void doReBindService() {
//        if (mServiceBinder != null) {
////            mIsPlaying = mServiceBinder.isPlaying();
//            unbindService(myConnection);
////            myServiceBinder = null;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        Log.d("activity", "onResume");
//        super.onResume();
//        if (mServiceBinder == null) {
//            // 서비스에 바인드
//            doBindService();
////            mIsPlaying = myServiceBinder.isPlaying();
//        }
////        startService(new Intent(getApplicationContext(), BackgroundMusicWithBindServiceService.class));
//    }

//    @Override
//    protected void onPause() {
//        Log.d("activity", "onPause");
//        super.onPause();
//
////        doUnBindService();
//
//        if(mServiceBinder != null) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
////            builder.setSmallIcon(R.drawable.musicplay);
//            builder.setContentTitle("My Music Play, Click Me!");
//            builder.setContentText("Hi, This is My Music Play");
//            Intent notificationIntent = new Intent(this, MusicPlayer.class);
//            PendingIntent contentIntent =
//                    PendingIntent.getActivity(this, 0,
//                            notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(contentIntent);
//// Add as notification
//            NotificationManager manager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            manager.notify(0, builder.build());
//
//        }
//    }

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

    /*
    public ArrayList<HashMap<String,Object>> getPlaylist() {
        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        //String[] play_list = mServiceBinder.getPlayList();
//        String[] str_music_list = {"media_1.mp3", "media_2.mp3", "media_3.mp3", "media_4.mp3", "media_5.mp3"};
//        String[] play_list = str_music_list;

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
*/
    public ArrayList<HashMap<String,Object>> getPlaylist() {

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        File musicPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        File files[] = musicPublicDir.listFiles();

        for(int i=0; i<files.length; i++) {

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(files[i].toString());
//            String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
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

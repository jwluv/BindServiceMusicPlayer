package com.example.edu.bindservicemusicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonMusicPlayer;
    TextView textViewMusicPlaying;

    private MyMusicService mServiceBinder;
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceBinder = ((MyMusicService.MyBinder)binder).getService();
        }
        public void onServiceDisconnected(ComponentName className) { mServiceBinder = null; }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        buttonMusicPlayer = findViewById(R.id.buttonMusicPlayer);
        buttonMusicPlayer.setOnClickListener(this);

        textViewMusicPlaying = findViewById(R.id.textViewMusicPlaying);

        displayMusicTitle();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonMusicPlayer:
                Intent intent = new Intent(this, MusicPlayer.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayMusicTitle();
    }

    public void displayMusicTitle() {

        if(mServiceBinder != null)
        if(mServiceBinder.getTitle() != "" || mServiceBinder.getArtist() != "")
            textViewMusicPlaying.setText(mServiceBinder.getTitle() + " by " + mServiceBinder.getArtist() + "\nis playing...");
    }
}

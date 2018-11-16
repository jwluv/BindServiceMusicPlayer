package com.example.edu.bindservicemusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonMusicPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMusicPlayer = findViewById(R.id.buttonMusicPlayer);
        buttonMusicPlayer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonMusicPlayer:
                Intent intent = new Intent(this, MusicPlayer.class);
                startActivity(intent);
                break;

        }


    }
}

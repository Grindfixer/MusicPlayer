package com.jwn.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b/*if the change comes from b (T or F) - b represents user*/){
                    mediaPlayer.seekTo(i);//i represents progress of seekBar
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration - currentPos)));

            }// end onProgressChanged

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.locomotion);

        artistImage = findViewById(R.id.imageView);
        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);
        seekBar = findViewById(R.id.seekBar);
        prevButton = findViewById(R.id.prevButton);
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prevButton:
                //do stuff
                backMusic();
                break;

            case R.id.playButton:
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    startMusic();
                }
                break;

            case R.id.nextButton:
                    nextMusic();
                break;
        }
    }

    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            // temporary cause we only have one song
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
        }
    }

    private void updateThread(){

        thread = new Thread(){
            @Override
            public void run() {
                try {
                    while(mediaPlayer !=null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                //update text
                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getCurrentPosition()))));

                                rightTime.setText(String.valueOf((new java.text.SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())))));
                            }
                        });
                    }

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;// completely clear from memory
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}// end MainActivity

package com.example.madahin.pepe_media;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer.OnPreparedListener;

import java.io.IOException;

public class MediaActivity extends AppCompatActivity {

    private enum MEDIA_TYPE {
        MUSIC,
        VIDEO
    }

    private String m_mediaPath;

    private boolean videoTouched = false;
    private MediaPlayer m_mediaPlayer;
    private VideoView m_video;
    private Handler m_Handler = new Handler();

    private MEDIA_TYPE m_mediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_media);

        m_video = (VideoView)findViewById(R.id.videoView);

        m_mediaPath = getIntent().getExtras().getString("media_path");

        /*if(m_mediaPath == null){
            // throw error
        }*/

        play();

        m_video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!videoTouched) {
                    videoTouched = true;
                    if (isPlaying()) {
                        pause();
                    } else {
                        resume();
                    }
                    m_Handler.postDelayed(new Runnable() {
                        public void run() {
                            videoTouched = false;
                        }
                    }, 100);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        stop();
    }

    private boolean isPlaying(){
        return ((m_video != null && m_video.isPlaying()) || (m_mediaPlayer != null && m_mediaPlayer.isPlaying()));
    }

    private void play() {
        Log.d("", "-- Playing");

        if(m_mediaPath.endsWith(".mp3")){
            m_mediaType = MEDIA_TYPE.MUSIC;
        }else if(m_mediaPath.endsWith(".mp4")){
            m_mediaType = MEDIA_TYPE.VIDEO;
        }

        Uri fileUri = Uri.parse(m_mediaPath);

        switch (m_mediaType){
            case MUSIC:
            {
                m_mediaPlayer = new MediaPlayer();
                m_video.setBackgroundResource(R.drawable.default_wallpaper);
                m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    m_mediaPlayer.setDataSource(getApplicationContext(), fileUri);
                    m_mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                m_mediaPlayer.start();
                break;
            }
            case VIDEO:
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                MediaController mediacontroller = new MediaController(MediaActivity.this);
                mediacontroller.setAnchorView(m_video);
                m_video.setMediaController(mediacontroller);
                m_video.setVideoURI(fileUri);

                m_video.requestFocus();
                m_video.setOnPreparedListener(new OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        m_video.start();
                    }
                });
                break;
            }
        }
    }

    private void stop() {
        Log.d("", "-- Stopping");
        switch (m_mediaType){
            case MUSIC:
            {
                if (m_mediaPlayer.isPlaying()) {
                    Log.d("", "-- is playing");
                    m_mediaPlayer.stop();
                    Log.d("", "-- is stopped");
                    m_mediaPlayer.release();
                    Log.d("", "-- is released");
                }
                break;
            }
            case VIDEO:
            {
                m_video.stopPlayback();
                break;
            }
        }
    }

    private void pause() {
        Log.d("", "-- Pausing");
        switch (m_mediaType){
            case MUSIC:
            {
                if (m_mediaPlayer.isPlaying()) {
                    Log.d("", "-- is playing");
                    m_mediaPlayer.pause();
                    Log.d("", "-- is stopped");
                }
                break;
            }
            case VIDEO:
            {
                if(m_video != null && m_video.isPlaying()) {
                    m_video.pause();
                }
                break;
            }
        }
    }

    private void resume() {
        Log.d("", "-- Resuming");
        switch (m_mediaType){
            case MUSIC:
            {
                m_mediaPlayer.start();
                break;
            }
            case VIDEO:
            {
                m_video.resume();
                break;
            }
        }
    }
}

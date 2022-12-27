package com.example.multivideos;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class Player extends AppCompatActivity {

    VideoView videoPlayer;
    Handler customerHandler=new Handler();
    LinearLayout container;
    TextView timer1;
    int set=0;
    long starttime=0L,timemilli=0L,timeswap=0L,updatetime=0L;
    Runnable updateTimeThread=new Runnable() {
        @Override
        public void run() {

            timemilli= SystemClock.uptimeMillis()-starttime;
            updatetime=timeswap+timemilli;
            int secs=(int)(updatetime/1000);
            int min=secs/60;
            secs%=60;

            int milliseconds=(int)(updatetime%1000);
            if(videoPlayer.isPlaying())
            {
                timer1.setText("Started Playng");
                set=1;

                customerHandler.removeCallbacks(updateTimeThread);
                System.out.println("buffered Time = "+min+":"+secs+":"+milliseconds);

            }else
            {
                if(set==1){timer1.setText("Buffering");

                }else {
                    timer1.setText("" + min + ":" + String.format("%2d", secs) + ":" + String.format("%3d", milliseconds));
                }

            }

            customerHandler.postDelayed(this,0);
        }
    };
    public static final String TAG = "TAG";
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.progressBar);

        Intent i = getIntent();
        Bundle data = i.getExtras();
        Video v = (Video) data.getSerializable("videoData");

        getSupportActionBar().setTitle(v.getName());
        Log.d(TAG, "onCreate:");

        TextView title = findViewById(R.id.videoTitle);
         videoPlayer = findViewById(R.id.videoView);
        title.setText(v.getName());
        Uri videoUrl = Uri.parse(v.getVideoUrl());

        videoPlayer.setVideoURI(videoUrl);
        MediaController mc = new MediaController(this);
        videoPlayer.setMediaController(mc);

        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer.start();

                spinner.setVisibility(View.GONE);


            }
        });
         timer1=(TextView) findViewById(R.id.timer);
        starttime= SystemClock.uptimeMillis();

       customerHandler.postDelayed(updateTimeThread,0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
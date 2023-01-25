package com.example.multivideos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player extends AppCompatActivity {


    public static  String FILE_NAME;
    String vname;
    String Loading_time;
    long st1,pt1;
    VideoView videoPlayer;
    Handler customerHandler=new Handler();
    LinearLayout container;
    TextView timer1;
    int set=0;
    TextView mEditText;
    long starttime=0L,timemilli=0L,timeswap=0L,updatetime=0L,diff,secs,min,milliseconds;
    Runnable updateTimeThread=new Runnable() {
        @Override
        public void run() {

            timemilli= SystemClock.uptimeMillis()-starttime;
            updatetime=timeswap+timemilli;
             secs=(int)(updatetime/1000);
             min=secs/60;
            secs%=60;

             milliseconds=(int)(updatetime%1000);
            if(videoPlayer.isPlaying() )
            {


                if(set==0)
                {   pt1=Calendar.getInstance().getTimeInMillis();
                    diff=st1-pt1;
                    System.out.println("DDiff== "+diff);
                    Date currentTime = Calendar.getInstance().getTime();
                        Loading_time= String.valueOf(currentTime);
                    timer1.setText(min+":"+secs+":"+milliseconds);
                    Loading_time=Loading_time+" "+min+":"+secs+":"+milliseconds+"\n";
                    View view = null;
                    save(view);

                    return;
                }
                set=1;


                customerHandler.removeCallbacks(updateTimeThread);
                System.out.println("buffered Time = "+min+":"+secs+":"+milliseconds);

            }else
            {
                if(set==1){//timer1.setText("Buffering");

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
        vname=v.getName();

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
                try {
                    postJsonData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                 st1=Calendar.getInstance().getTimeInMillis();
                videoPlayer.start();

                spinner.setVisibility(View.GONE);


            }
        });
        if(videoPlayer.isPlaying())
        {

            long diff=pt1-st1;
            Date currentTime = Calendar.getInstance().getTime();
            timer1.setText(vname+" "+currentTime+" "+diff);
        }
         timer1=(TextView) findViewById(R.id.timer);
        starttime= SystemClock.uptimeMillis();

       customerHandler.postDelayed(updateTimeThread,0);


        mEditText=findViewById(R.id.editText);
        Button save1=findViewById(R.id.save);
        Button load1=findViewById(R.id.load);
        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                save(view);
            }
        });
        load1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                load(view);
            }
        });
    }
    public void save(View v)
    {
        String text=Loading_time;
        FILE_NAME=vname+".txt";
        FileOutputStream fos=null;
        try {
           // fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append(text);
            fos.write(text.getBytes());

            Toast.makeText(this,"Saved to"+getFilesDir()+"/"+FILE_NAME,Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }finally {
            if(fos!=null)
            {
                try{
                    fos.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public  void load(View v)
    {
        FileInputStream fis=null;

        try{
            fis=openFileInput(FILE_NAME);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);
            StringBuilder sb=new StringBuilder();
            String text;
            while((text=br.readLine())!=null)
            {
                    sb.append(text).append("\n");

            }
            mEditText.setText(sb.toString());

        }catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }finally {
            if(fis!=null)
            {
                try{
                    fis.close();
                }catch(IOException e)
                {
                        e.printStackTrace();
                }
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    private void postJsonData() throws JSONException {
        System.out.println("{json calledddd..........}");
        Date currentTime = Calendar.getInstance().getTime();
        String date1=String.valueOf(currentTime);
       String temp=vname+" "+date1+" "+min+":"+secs+":"+milliseconds+"";
        String URL="http://172.16.14.227:4000/video/";
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JSONObject json1 =new JSONObject();
        json1.put("tejas",temp);
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, URL, json1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                   Log.d(TAG, "response=: "+response);
//                try {


//                    JSONArray videos =response.getJSONArray("videos");
//
//                    com.google.android.exoplayer2.util.Log.d(TAG,"changeResponse"+videos);
//
//                    for(int i = 0; i < videos.length() ; i++)
//                    {
//                        JSONObject video=videos.getJSONObject(i);
//                        Video v = new Video();
//                        v.setId(video.getString("id"));
//                        v.setName(video.getString("name"));
//                        String url="http://172.16.23.20:4000/video/";
//                        url=url.concat(video.getString("id"));
//
//                        com.google.android.exoplayer2.util.Log.d(TAG, "url=: "+url);
//
//                        v.setVideoUrl(url);
//
//                        all_videos.add(v);
//                        adapter.notifyDataSetChanged();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                com.google.android.exoplayer2.util.Log.e(TAG,"OnErrorResponse"+error.getMessage());
            }
        });
        requestQueue.add(objectRequest);
    }
}
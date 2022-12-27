package com.example.multivideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG="TAG";
    RecyclerView videoList;
    VideoAdapter adapter;
    List<Video> all_videos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        all_videos = new ArrayList<>();
        videoList =findViewById(R.id.videoList);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new VideoAdapter(this, all_videos);
        videoList.setAdapter(adapter);
        getJsonData();

    }

    private void getJsonData() {
        String URL="http://172.16.9.153:4000/videos";
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray videos =response.getJSONArray("videos");

                    Log.d(TAG,"changeResponse"+videos);

                    for(int i = 0; i < videos.length() ; i++)
                    {
                        JSONObject video=videos.getJSONObject(i);
                        Video v = new Video();
                        v.setId(video.getString("id"));
                        v.setName(video.getString("name"));
                        String url="http://172.16.9.153:4000/video/";
                        url=url.concat(video.getString("id"));

                        Log.d(TAG, "url=: "+url);

                        v.setVideoUrl(url);

                        all_videos.add(v);
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"OnErrorResponse"+error.getMessage());
            }
        });
        requestQueue.add(objectRequest);
    }
}
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

public class MainActivity extends AppCompatActivity {
    public static final String TAG="TAG";
    RecyclerView videoList;
    VideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoList =findViewById(R.id.videoList);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new VideoAdapter();
        videoList.setAdapter(adapter);
        getJsonData();

    }

    private void getJsonData() {
        String URL="http://192.168.200.195:4000/videos";
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories=response.getJSONArray("videos");

                    Log.d(TAG,"changeResponse"+categories);
                    for(int i=0;i<categories.length();i++)
                    {
                        JSONObject video=categories.getJSONObject(i);

                        video v=new video();
                        v.setId(video.getString("id"));
                        v.setName(video.getString("name"));
                        String url="http://192.168.200.195:4000/video/";
                        url=url.concat(video.getString("id"));
                        Log.d(TAG, "url=: "+url);
                        v.setVideoUrl(url);


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
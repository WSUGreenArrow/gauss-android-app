package com.downs.courtney.gaussapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toast.makeText(getBaseContext(), "WORKING! You are not connected to the VTM's wireless network." +
                " \nConnect or view saved content.", Toast.LENGTH_LONG).show();


        Button wifi = (Button)findViewById(R.id.find_network_btn);
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });



        Button connectedView = (Button)findViewById(R.id.connected_view_btn);
        connectedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(MainActivity.this, VTMConnectedActivity.class);
                startActivity(startIntent);
            }
        });
        Button viewRecording = (Button)findViewById(R.id.view_recordings_btn);
        viewRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("/sdcard/download/video.mp4"));
//                intent.setDataAndType(Uri.parse("/sdcard/download/video.mp4"), "video/mp4");
//                startActivity(intent);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setType("video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }
}
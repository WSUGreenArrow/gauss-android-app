package com.downs.courtney.gaussapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String ssid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
        }


        if(ssid.equals("\"PurpleBow\"")){
            // Start other screen
            Intent startIntent0 = new Intent(MainActivity.this, VTMConnectedActivity.class);
            startActivity(startIntent0);

            //Intent startIntent = new Intent(MainActivity.this, PlayerActivity.class);
            //startActivity(startIntent);

        }else{

            Toast.makeText(getBaseContext(), "WARNING! You are not connected to the VTM's wireless network." +
                    " \nConnect or view saved content.", Toast.LENGTH_LONG).show();
        }






        Button wifi = (Button)findViewById(R.id.find_network_btn);
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
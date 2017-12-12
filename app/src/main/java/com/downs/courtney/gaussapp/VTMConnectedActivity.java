package com.downs.courtney.gaussapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;


/**
 * Created by courtney on 10/9/17.
 */

public class VTMConnectedActivity extends AppCompatActivity {


    private final String SHARED_PREF_KEY = "SHARED_PREF_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vtm_connect_activity);


        Button liveView = (Button)findViewById(R.id.live_view_btn);
        Button viewRecording = (Button)findViewById(R.id.view_recordings_btn);
        liveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Send SharedPreference with DONT RECORD FLAG
                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(SHARED_PREF_KEY,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("RECORD_KEY", "DONT_RECORD");
                editor.commit();

                Intent startIntent = new Intent(VTMConnectedActivity.this, PlayerActivity.class);
                startActivity(startIntent);
            }
        });


        Button recordButton = (Button)findViewById(R.id.record_new_video);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(VTMConnectedActivity.this, PlayerActivity.class);
                startActivity(startIntent);

                // Send SharedPreference with RECORD_FLAG

                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(SHARED_PREF_KEY,
                                                                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("RECORD_KEY", "RECORD");
                editor.commit();



            }
        });

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
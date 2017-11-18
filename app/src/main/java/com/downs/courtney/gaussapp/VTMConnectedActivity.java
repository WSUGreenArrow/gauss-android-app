package com.downs.courtney.gaussapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by courtney on 10/9/17.
 */

public class VTMConnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vtm_connect_activity);


        Button liveView = (Button)findViewById(R.id.live_view_btn);
        liveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(VTMConnectedActivity.this, LiveViewActivity.class);
                startActivity(startIntent);
            }
        });


    }
}
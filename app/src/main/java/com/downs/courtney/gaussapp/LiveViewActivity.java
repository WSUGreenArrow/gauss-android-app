package com.downs.courtney.gaussapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.downs.courtney.gaussapp.util.SystemUtil;


public class LiveViewActivity extends AppCompatActivity {
    private TextInputLayout tilRemoteVideo;
    private EditText etRemoteVideoLeft;
    private EditText etRemoteVideoRight;

    private final static int PERMISSIONS_REQUEST = 0;
    private final static int VIDEO_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_view_activity);


        // TODO - this commented out stuff might be useful with displaying local video
//        Button btnLocalVideo = (Button) findViewById(R.id.btnLocalVideo);
//        btnLocalVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(VTMConnectedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    Toast.makeText(VTMConnectedActivity.this, "播放本地视频需要读取SD卡，请允许操作SD卡的权限。", Toast.LENGTH_SHORT).show();
//                }
//
//                if (ContextCompat.checkSelfPermission(VTMConnectedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("*/*");
//                    startActivityForResult(intent, VIDEO_REQUEST);
//                } else {
//                    //请求权限
//                    ActivityCompat.requestPermissions(VTMConnectedActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
//                }
//            }
//        });
//

        tilRemoteVideo = (TextInputLayout) findViewById(R.id.tilRemoteVideo);

        // Left and right .sdp files
        etRemoteVideoLeft = (EditText) findViewById(R.id.etRemoteVideoLeft);
        etRemoteVideoRight = (EditText) findViewById(R.id.etRemoteVideoRight);

        Button btnRemoteVideo = (Button) findViewById(R.id.btnRemoteVideo);
        btnRemoteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etRemoteVideoLeft.getText().toString().equals("") ||
                        etRemoteVideoRight.getText().toString().equals("")) {
                    tilRemoteVideo.setError("Remote video address can not be empty!");
                    return;
                }

                // Sharing the ViedoURL with an intent
                Intent intent = new Intent();
                intent.setClass(LiveViewActivity.this, PlayerActivity.class);
                intent.putExtra("VideoType", "Remote");
                intent.putExtra("VideoUrlLeft", etRemoteVideoLeft.getText().toString());
                intent.putExtra("VideoUrlRight", etRemoteVideoRight.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, VIDEO_REQUEST);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == VIDEO_REQUEST && data != null) {
                Intent intent = new Intent();
                intent.setClass(LiveViewActivity.this, PlayerActivity.class);
                intent.putExtra("VideoType", "Local");
                String url = SystemUtil.getPath(LiveViewActivity.this, data.getData());
                //url="/storage/emulated/0/DCIM/Video/V70617-212725.mp4"
                intent.putExtra("VideoUrl", url);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.d("Local", e.toString());
        }
    }
}
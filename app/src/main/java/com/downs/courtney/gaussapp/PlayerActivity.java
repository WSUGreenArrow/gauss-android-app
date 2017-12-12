/*****************************************************************************
 * PlayerActivity.java
 *****************************************************************************
 * Copyright (C) 2016 VideoLAN
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license. See the LICENSE file for details.
 *****************************************************************************/

package com.downs.courtney.gaussapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements IVLCVout.OnNewVideoLayoutListener {
    private static final boolean USE_SURFACE_VIEW = true;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "PlayerActivity";
    private static final String SAMPLE_URL = "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_640x360.m4v";
    private final String LEFT_URL = "http://10.107.101.1:8080/left.sdp";
    private final String RIGHT_URL = "http://10.107.101.1:8080/right.sdp";

    // Left
    private FrameLayout mVideoSurfaceFrameLeft = null;
    private SurfaceView mVideoSurfaceLeft = null;
    private SurfaceView mSubtitlesSurfaceLeft = null;
    private View mVideoViewLeft = null;
    private TextureView mVideoTextureLeft = null;

    // Right
    private FrameLayout mVideoSurfaceFrameRight = null;
    private SurfaceView mVideoSurfaceRight = null;
    private SurfaceView mSubtitlesSurfaceRight = null;
    private TextureView mVideoTextureRight = null;
    private final Handler mHandler = new Handler();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;
    private LibVLC mLibVLCLeft = null;
    private LibVLC mLibVLCRight = null;
    private MediaPlayer mMediaPlayerLeft = null;
    private MediaPlayer mMediaPlayerRight = null;
    private final String SHARED_PREF_KEY = "SHARED_PREF_KEY";




    // Stuff for recording video below
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private ToggleButton mToggleButton;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private static File video = new File("/sdcard/download/GaussApp/video.mp4");
    private static int i = 0;
    public static final int RESULT_GALLERY = 0;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palyer);


        new File("/sdcard/download/GaussApp").mkdir();

        // Check if record flag is on
        SharedPreferences sharedPref = this.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String actualValue = sharedPref.getString("RECORD_KEY", "NO_RECORD");


        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");

        mLibVLCLeft = new LibVLC(this, args);
        mLibVLCRight = new LibVLC(this, args);
        mMediaPlayerLeft = new MediaPlayer(mLibVLCLeft);
        mMediaPlayerRight = new MediaPlayer(mLibVLCRight);

        initVLCView();



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

        mToggleButton = (ToggleButton) findViewById(R.id.toggle);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "In the loop", Toast.LENGTH_LONG).show();



                if (ContextCompat.checkSelfPermission(PlayerActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(PlayerActivity.this,
                                Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale
                                    (PlayerActivity.this, Manifest.permission.RECORD_AUDIO)) {
                        mToggleButton.setChecked(false);
                        Snackbar.make(findViewById(android.R.id.content), R.string.label_permissions,
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(PlayerActivity.this,
                                                new String[]{Manifest.permission
                                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                                REQUEST_PERMISSIONS);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(PlayerActivity.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    onToggleScreenShare(v);
                }
            }
        });



        if(!actualValue.equals("RECORD")){
            // Make invisible when not recording
            mToggleButton.setVisibility(View.GONE);

        }


    }


    // Actually creates the video views
    private void initVLCView() {

        // Left
        mVideoSurfaceFrameLeft = (FrameLayout) findViewById(R.id.video_surface_frame_left);
        if (USE_SURFACE_VIEW) {
            ViewStub stub = (ViewStub) findViewById(R.id.surface_stub_left);
            mVideoSurfaceLeft = (SurfaceView) stub.inflate();
            if (ENABLE_SUBTITLES) {
                stub = (ViewStub) findViewById(R.id.subtitles_surface_stub_left);
                mSubtitlesSurfaceLeft = (SurfaceView) stub.inflate();
                mSubtitlesSurfaceLeft.setZOrderMediaOverlay(true);
                mSubtitlesSurfaceLeft.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            }
            mVideoViewLeft = mVideoSurfaceLeft;
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.texture_stub_left);
            mVideoTextureLeft = (TextureView) stub.inflate();
            mVideoViewLeft = mVideoTextureLeft;
        }


        // Right
        mVideoSurfaceFrameRight = (FrameLayout) findViewById(R.id.video_surface_frame_right);
        if (USE_SURFACE_VIEW) {
            ViewStub stub = (ViewStub) findViewById(R.id.surface_stub_right);
            mVideoSurfaceRight = (SurfaceView) stub.inflate();
            if (ENABLE_SUBTITLES) {
                stub = (ViewStub) findViewById(R.id.subtitles_surface_stub_right);
                mSubtitlesSurfaceRight = (SurfaceView) stub.inflate();
                mSubtitlesSurfaceRight.setZOrderMediaOverlay(true);
                mSubtitlesSurfaceRight.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            }

        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.texture_stub_right);
            mVideoTextureRight = (TextureView) stub.inflate();

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Left
        mMediaPlayerLeft.release();
        mLibVLCLeft.release();

        // Right
        mMediaPlayerRight.release();
        mLibVLCRight.release();

        destroyMediaProjection();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Left
        final IVLCVout vlcVout = mMediaPlayerLeft.getVLCVout();
        if (mVideoSurfaceLeft != null) {
            vlcVout.setVideoView(mVideoSurfaceLeft);
            if (mSubtitlesSurfaceLeft != null)
                vlcVout.setSubtitlesView(mSubtitlesSurfaceLeft);
        } else
            vlcVout.setVideoView(mVideoTextureLeft);
        vlcVout.attachViews(this);


        // Right
        final IVLCVout vlcVoutRight = mMediaPlayerRight.getVLCVout();
        if (mVideoSurfaceRight != null) {
            vlcVoutRight.setVideoView(mVideoSurfaceRight);
            if (mSubtitlesSurfaceRight != null)
                vlcVoutRight.setSubtitlesView(mSubtitlesSurfaceRight);
        } else
            vlcVoutRight.setVideoView(mVideoTextureRight);
        vlcVoutRight.attachViews(this);


        // Media media = new Media(mLibVLC, Uri.parse(SAMPLE_URL));


        Media mediaLeft;
        Media mediaRight;


        mediaLeft = new Media(mLibVLCLeft, Uri.parse(LEFT_URL));// SAMPLE_URL
        mediaRight = new Media(mLibVLCRight, Uri.parse(RIGHT_URL));// SAMPLE_URL



        // Left
        mMediaPlayerLeft.setMedia(mediaLeft);
        mediaLeft.release();
        mMediaPlayerLeft.play();
        mMediaPlayerLeft.getVLCVout();
        if (mOnLayoutChangeListener == null) {
            mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                private final Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                    }
                };

                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
        mVideoSurfaceFrameLeft.addOnLayoutChangeListener(mOnLayoutChangeListener);


        // Right
        mMediaPlayerRight.setMedia(mediaRight);
        mediaRight.release();
        mMediaPlayerRight.play();
        mMediaPlayerRight.getVLCVout();
        if (mOnLayoutChangeListener == null) {
            mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                private final Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                    }
                };

                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
        mVideoSurfaceFrameRight.addOnLayoutChangeListener(mOnLayoutChangeListener);
        initInfoListener();



    }

    private void initInfoListener() {

        MediaPlayer.EventListener eventListener = new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                try {
                    if (mMediaPlayerLeft.getLength() <= 0 || event.getTimeChanged() == 0) {
                        return;
                    }
                    Log.i(TAG, "TimeChanged: "+event.getTimeChanged());
                    Log.i(TAG, "Length: "+ mMediaPlayerLeft.getLength());
                    Log.i(TAG, "播放到 千分之: " + event.getTimeChanged() * 1000 / mMediaPlayerLeft.getLength() );
                    //seekBarTime.setProgress((int) (event.getTimeChanged() * 1000 / mMediaPlayerLeft.getLength()));
//                    tvCurrentTime.setText(SystemUtil.getMediaTime((int) event.getTimeChanged()));

                    //播放结束
                    if (mMediaPlayerLeft.getPlayerState() == Media.State.Ended) {
                        //seekBarTime.setProgress(0);
                        mMediaPlayerLeft.setTime(0);
                        mMediaPlayerRight.setTime(0);
//                        tvTotalTime.setText(SystemUtil.getMediaTime((int) mTotalTime));
                        mMediaPlayerLeft.stop();
                        mMediaPlayerRight.stop();
                        //imgPlay.setBackgroundResource(R.drawable.videoviewx_play);
                    }
                } catch (Exception e) {
                    Log.d("vlc-event", e.toString());
                }
            }
        };
        mMediaPlayerLeft.setEventListener(eventListener);
        mMediaPlayerRight.setEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // This should always be null because I locked the orientation of this activity but
        //  I'll keep this here.
        if (mOnLayoutChangeListener != null) {
            mVideoSurfaceFrameLeft.removeOnLayoutChangeListener(mOnLayoutChangeListener);
            mOnLayoutChangeListener = null;

            mVideoSurfaceFrameRight.removeOnLayoutChangeListener(mOnLayoutChangeListener);
            mOnLayoutChangeListener = null;
        }

        mMediaPlayerLeft.stop();
        mMediaPlayerLeft.getVLCVout().detachViews();

        mMediaPlayerRight.stop();
        mMediaPlayerRight.getVLCVout().detachViews();
    }




    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
//        mVideoWidth = width;
//        mVideoHeight = height;
//        mVideoVisibleWidth = visibleWidth;
//        mVideoVisibleHeight = visibleHeight;
//        mVideoSarNum = sarNum;
//        mVideoSarDen = sarDen;
//        updateVideoSurfaces();
    }


    @Override
    @TargetApi(21)  public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            mToggleButton.setChecked(false);
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    public void onToggleScreenShare(View view) {
        if (((ToggleButton) view).isChecked()) {
            initRecorder();
            shareScreen();
        } else {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            Log.v(TAG, "Stopping Recording");
            stopScreenSharing();
        }
    }

    @TargetApi(21)  private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    @TargetApi(21) private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder() {
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            i = 0 + (int)(Math.random() * 10000000);
            mMediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/GaussApp/video" + i + ".mp4");
            i = 0 + (int)(Math.random() * 10000000);

            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(21) private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (mToggleButton.isChecked()) {
                mToggleButton.setChecked(false);
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                Log.v(TAG, "Recording Stopped");
            }
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    @TargetApi(21) private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    @TargetApi(21) private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    onToggleScreenShare(mToggleButton);
                } else {
                    mToggleButton.setChecked(false);
                    Snackbar.make(findViewById(android.R.id.content), R.string.label_permissions,
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }


}
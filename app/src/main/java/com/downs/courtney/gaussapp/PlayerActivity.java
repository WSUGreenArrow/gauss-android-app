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

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements IVLCVout.OnNewVideoLayoutListener {
    private static final boolean USE_SURFACE_VIEW = true;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "PlayerActivity";
    private static final String SAMPLE_URL = "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_640x360.m4v";
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_BEST_FIT;



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
    private View mVideoViewRight = null;
    private TextureView mVideoTextureRight = null;


    private final Handler mHandler = new Handler();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLCLeft = null;
    private LibVLC mLibVLCRight = null;

    private MediaPlayer mMediaPlayerLeft = null;
    private MediaPlayer mMediaPlayerRight = null;

    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;


    //private ImageView imgPlay;
    //private TextView tvFullScreen;
    //private SeekBar seekBarTime;
    //private SeekBar.OnSeekBarChangeListener onTimeSeekBarChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_palyer);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");

        mLibVLCLeft = new LibVLC(this, args);
        mLibVLCRight = new LibVLC(this, args);
        mMediaPlayerLeft = new MediaPlayer(mLibVLCLeft);
        mMediaPlayerRight = new MediaPlayer(mLibVLCRight);

        initVLCView();
        initInfoView();
    }

    private void initInfoView() {
        //imgPlay = (ImageView) findViewById(R.id.imgPlay);
        //tvFullScreen = (TextView) findViewById(R.id.tvFullScreen);
        //seekBarTime = (SeekBar) findViewById(R.id.seekBarTime);

        /*
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayerLeft.isPlaying()) {
                    mMediaPlayerLeft.pause();
                    imgPlay.setBackgroundResource(R.drawable.videoviewx_play);
                } else {
                    mMediaPlayerLeft.play();
                    imgPlay.setBackgroundResource(R.drawable.videoviewx_pause);
                }
            }
        });

        tvFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Full Screen".equals(tvFullScreen.getText().toString())) {
                    tvFullScreen.setText("退出");
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    getWindow().setAttributes(params);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else {
                    tvFullScreen.setText("Full Screen");
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().setAttributes(params);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            }
        });

        seekBarTime.setMax(1000);
        onTimeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (fromUser) {
                        if (mMediaPlayerLeft.getLength() <= 0) {
                            return;
                        }
                        mMediaPlayerLeft.setTime(progress * mMediaPlayerLeft.getLength() / 1000);
                        //tvCurrentTime.setText(SystemUtil.getMediaTime(progress));
                    }
                } catch (Exception e) {
                    Log.d("vlc-time", e.toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekBarTime.setOnSeekBarChangeListener(onTimeSeekBarChangeListener);

        */
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
            mVideoViewRight = mVideoSurfaceLeft;
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.texture_stub_right);
            mVideoTextureRight = (TextureView) stub.inflate();
            mVideoViewRight = mVideoTextureLeft;
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

        // Using an intent to get the VideoUrl entered in previous screen. (LiveViewActivity)
        Media mediaLeft;
        Media mediaRight;
        Intent intent = getIntent();
        if (intent.getStringExtra("VideoType").equals("Local")) {
 
            /**
             * VTM Network: PurpleBow and ip address 10.107.101.1
             *
             * VTM Username: greenarrow
             * VTM Password: irrelevantart
             *
             *
             */
            // http://192.168.43.111:8080/left.sdp    http://192.168.43.111:8080/right.sdp

            mediaLeft = new Media(mLibVLCLeft, intent.getStringExtra("VideoUrlLeft"));
            mediaRight = new Media(mLibVLCRight, intent.getStringExtra("VideoUrlRight"));
        } else {
            mediaLeft = new Media(mLibVLCLeft, Uri.parse(intent.getStringExtra("VideoUrlLeft")));
            mediaRight = new Media(mLibVLCRight, Uri.parse(intent.getStringExtra("VideoUrlRight")));
        }



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
                       // updateVideoSurfaces();                                      // TODO - Downs
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
                        // updateVideoSurfaces();                                      // TODO - Downs
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



//    private void changeMediaPlayerLayout(int displayW, int displayH) {
//        /* Change the video placement using the MediaPlayer API */
//        switch (CURRENT_SIZE) {
//            case SURFACE_BEST_FIT:
//                mMediaPlayerLeft.setAspectRatio(null);
//                mMediaPlayerLeft.setScale(0);
//
//                mMediaPlayerRight.setAspectRatio(null);
//                mMediaPlayerRight.setScale(0);
//                break;
//            case SURFACE_FIT_SCREEN:
//            case SURFACE_FILL: {
//                Media.VideoTrack vtrack = mMediaPlayerLeft.getCurrentVideoTrack();
//                if (vtrack == null)
//                    return;
//                final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
//                        || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;
//                if (CURRENT_SIZE == SURFACE_FIT_SCREEN) {
//                    int videoW = vtrack.width;
//                    int videoH = vtrack.height;
//
//                    if (videoSwapped) {
//                        int swap = videoW;
//                        videoW = videoH;
//                        videoH = swap;
//                    }
//                    if (vtrack.sarNum != vtrack.sarDen)
//                        videoW = videoW * vtrack.sarNum / vtrack.sarDen;
//
//                    float ar = videoW / (float) videoH;
//                    float dar = displayW / (float) displayH;
//
//                    float scale;
//                    if (dar >= ar)
//                        scale = displayW / (float) videoW; /* horizontal */
//                    else
//                        scale = displayH / (float) videoH; /* vertical */
//                    mMediaPlayerLeft.setScale(scale);
//                    mMediaPlayerLeft.setAspectRatio(null);
//
//                    mMediaPlayerRight.setScale(scale);
//                    mMediaPlayerRight.setAspectRatio(null);
//
//                } else {
//                    mMediaPlayerLeft.setScale(0);
//                    mMediaPlayerLeft.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
//                            : "" + displayH + ":" + displayW);
//
//                    mMediaPlayerRight.setScale(0);
//                    mMediaPlayerRight.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
//                            : "" + displayH + ":" + displayW);
//                }
//                break;
//            }
//            case SURFACE_16_9:
//                mMediaPlayerLeft.setAspectRatio("16:9");
//                mMediaPlayerLeft.setScale(0);
//
//                mMediaPlayerRight.setAspectRatio("16:9");
//                mMediaPlayerRight.setScale(0);
//
//                break;
//            case SURFACE_4_3:
//                mMediaPlayerLeft.setAspectRatio("4:3");
//                mMediaPlayerLeft.setScale(0);
//
//                mMediaPlayerRight.setAspectRatio("4:3");
//                mMediaPlayerRight.setScale(0);
//                break;
//            case SURFACE_ORIGINAL:
//                mMediaPlayerLeft.setAspectRatio(null);
//                mMediaPlayerLeft.setScale(1);
//
//                mMediaPlayerRight.setAspectRatio(null);
//                mMediaPlayerRight.setScale(1);
//                break;
//        }
//    }

//    private void updateVideoSurfaces() {
//        int sw = getWindow().getDecorView().getWidth();
//        int sh = getWindow().getDecorView().getHeight();
//
//        // sanity check
//        if (sw * sh == 0) {
//            Log.e(TAG, "Invalid surface size");
//            return;
//        }
//
//        mMediaPlayerLeft.getVLCVout().setWindowSize(sw, sh);
//        mMediaPlayerRight.getVLCVout().setWindowSize(sw, sh);
//
//        ViewGroup.LayoutParams lp = mVideoViewLeft.getLayoutParams();
//        if (mVideoWidth * mVideoHeight == 0) {
//            /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            mVideoViewLeft.setLayoutParams(lp);
//            mVideoViewRight.setLayoutParams(lp);
//
//            lp = mVideoSurfaceFrameLeft.getLayoutParams();
//            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            mVideoSurfaceFrameLeft.setLayoutParams(lp);
//            mVideoSurfaceFrameRight.setLayoutParams(lp);
//          //  changeMediaPlayerLayout(sw, sh);                             // TODO - Downs
//            return;
//        }
//
//        if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
//            /* We handle the placement of the video using Android View LayoutParams */
//            mMediaPlayerLeft.setAspectRatio(null);
//            mMediaPlayerLeft.setScale(0);
//
//            mMediaPlayerRight.setAspectRatio(null);
//            mMediaPlayerRight.setScale(0);
//
//        }
//
//        double dw = sw, dh = sh;
//        final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
//
//        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
//            dw = sh;
//            dh = sw;
//        }
//
//        // compute the aspect ratio
//        double ar, vw;
//        if (mVideoSarDen == mVideoSarNum) {
//            /* No indication about the density, assuming 1:1 */
//            vw = mVideoVisibleWidth;
//            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
//        } else {
//            /* Use the specified aspect ratio */
//            vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
//            ar = vw / mVideoVisibleHeight;
//        }
//
//        // compute the display aspect ratio
//        double dar = dw / dh;
//
//        switch (CURRENT_SIZE) {
//            case SURFACE_BEST_FIT:
//                if (dar < ar)
//                    dh = dw / ar;
//                else
//                    dw = dh * ar;
//                break;
//            case SURFACE_FIT_SCREEN:
//                if (dar >= ar)
//                    dh = dw / ar; /* horizontal */
//                else
//                    dw = dh * ar; /* vertical */
//                break;
//            case SURFACE_FILL:
//                break;
//            case SURFACE_16_9:
//                ar = 16.0 / 9.0;
//                if (dar < ar)
//                    dh = dw / ar;
//                else
//                    dw = dh * ar;
//                break;
//            case SURFACE_4_3:
//                ar = 4.0 / 3.0;
//                if (dar < ar)
//                    dh = dw / ar;
//                else
//                    dw = dh * ar;
//                break;
//            case SURFACE_ORIGINAL:
//                dh = mVideoVisibleHeight;
//                dw = vw;
//                break;
//        }
//
//        // set display size
//        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
//        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
//        mVideoViewLeft.setLayoutParams(lp);
//        mVideoViewRight.setLayoutParams(lp);
//        if (mSubtitlesSurfaceLeft != null)
//            mSubtitlesSurfaceLeft.setLayoutParams(lp);
//            mSubtitlesSurfaceRight.setLayoutParams(lp);
//
//        // set frame size (crop if necessary)
//        lp = mVideoSurfaceFrameLeft.getLayoutParams();
//        lp.width = (int) Math.floor(dw);
//        lp.height = (int) Math.floor(dh);
//        mVideoSurfaceFrameLeft.setLayoutParams(lp);
//        mVideoSurfaceFrameRight.setLayoutParams(lp);
//
//        mVideoViewLeft.invalidate();
//        mVideoViewRight.invalidate();
//        if (mSubtitlesSurfaceLeft != null)
//            mSubtitlesSurfaceLeft.invalidate();
//            mSubtitlesSurfaceRight.invalidate();
//    }

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
}

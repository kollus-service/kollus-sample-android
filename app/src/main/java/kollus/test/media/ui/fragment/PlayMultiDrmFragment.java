package kollus.test.media.ui.fragment;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kollus.sdk.media2.MediaPlayer;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kollus.test.media.R;
import kollus.test.media.player.MultiDrmPlayer;
import kollus.test.media.player.KollusConstant;
import kollus.test.media.utils.CommonUtils;
import kollus.test.media.utils.JwtUtil;
import kollus.test.media.utils.LogUtil;

import static com.kollus.sdk.media.KollusStorage.TYPE_CACHE;
import static kollus.test.media.Config.MODE_MAKE_JWT;


public class PlayMultiDrmFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = PlayMultiDrmFragment.class.getSimpleName();

    //    private KollusStorage mStorage = null;
    private AudioManager mAudioManager = null;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView mSurfaceView = null;
    private MultiDrmPlayer mPlayer = null;
    private TextView mLogTextView = null;

    public int playType = TYPE_CACHE;
    public String jwtUrl = null;
    public String inkaPayLoad = null;
//    public String jwtUrl = "http://v.kr.kollus.com/si?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdWlkIjoidGVzdFVlc3IiLCJleHB0IjoxNTc0NDk3NDg4LCJtYyI6W3sibWNrZXkiOiJrMWpGcElXTiJ9XX0.mQHwCdX9amqJSL40K4P72XwlYd9SYlto0vp00lQAti4&custom_key=e85dfe20589e9a8d767cf8feb070fb9dcd991176ec0a70a89eae05351492e2df";

    public static PlayMultiDrmFragment newInstance() {
        return new PlayMultiDrmFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LogUtil.d(TAG, "onCreateView");

        if (getArguments() != null) {
            playType = getArguments().getInt("playType");
            jwtUrl = getArguments().getString("urlOrMcKey");

        } else {
            if (MODE_MAKE_JWT) {
//                String mckey = "BaB5zx8p";
                //String mckey = "Odm0fcPR";
                String mckey =  "Adfpeu8M";
                String cuid = "kollus_test";
                try {
                    String drmData[] = CommonUtils.createMultiDrmUrl(mckey, cuid);
                    if (drmData != null) {
                        jwtUrl = drmData[0];
                        inkaPayLoad = drmData[1];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        View root = inflater.inflate(R.layout.fragment_playvideo, container, false);

        mSurfaceView = (SurfaceView) root.findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(surfaceCallback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mSurfaceView.setSecure(true);
        }
        mLogTextView = (TextView) root.findViewById(R.id.control_log);

        root.findViewById(R.id.play).setOnClickListener(this);
        root.findViewById(R.id.pause).setOnClickListener(this);
        root.findViewById(R.id.rate_up).setOnClickListener(this);
        root.findViewById(R.id.rate_down).setOnClickListener(this);
        root.findViewById(R.id.volume_up).setOnClickListener(this);
        root.findViewById(R.id.volume_down).setOnClickListener(this);
        root.findViewById(R.id.mute).setOnClickListener(this);
        root.findViewById(R.id.un_mute).setOnClickListener(this);
        root.findViewById(R.id.ff).setOnClickListener(this);
        root.findViewById(R.id.rw).setOnClickListener(this);
        root.findViewById(R.id.restart).setOnClickListener(this);
        root.findViewById(R.id.callApp).setOnClickListener(this);

        mMediaPlayer = new MediaPlayer(getContext(), KollusConstant.ZAPPING_INTERVAL, KollusConstant.KEY, KollusConstant.EXPIRE_DATE);
        mPlayer = new MultiDrmPlayer(getContext(), mMediaPlayer, mSurfaceView);

        return root;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.d(TAG, "surfaceCreated()");
            jwtUrl = "https://v.jp.kollus.com/si?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHB0IjoxNTg2ODIzMzAxLCJjdWlkIjo1MDcsIm1jIjpbeyJtY2tleSI6IkFkZnBldThNIiwiZHJtX3BvbGljeSI6eyJraW5kIjoiaW5rYSIsInN0cmVhbWluZ190eXBlIjoiZGFzaCIsImRhdGEiOnsibGljZW5zZV91cmwiOiJodHRwczpcL1wvbGljZW5zZS5wYWxseWNvbi5jb21cL3JpXC9saWNlbnNlTWFuYWdlci5kbyIsImNlcnRpZmljYXRlX3VybCI6Imh0dHBzOlwvXC9saWNlbnNlLnBhbGx5Y29uLmNvbVwvcmlcL2Zwc0tleU1hbmFnZXIuZG8_c2l0ZUlkPUpLUEIiLCJjdXN0b21faGVhZGVyIjp7ImtleSI6InBhbGx5Y29uLWN1c3RvbWRhdGEtdjIiLCJ2YWx1ZSI6ImV5SmtjbTFmZEhsd1pTSTZJbGRwWkdWMmFXNWxJaXdpYzJsMFpWOXBaQ0k2SWtwTFVFSWlMQ0oxYzJWeVgybGtJam8xTURjc0ltTnBaQ0k2SWpJd01qQXdNekU0TFdJeGFEWjZhbnAwSWl3aWRHOXJaVzRpT2lKbWQyWk5Vamd3TVZORWJEUklaREF3UTJSclN6SkNhMWxFTkhFd1pVcGlRMU5zT0hwbVFVRjBXVXh5VlRObWMybDFiVzVqYlhKdE1FdE9kVGxsY1VsNlVVNVlaWFpJTW5KTlRHOW1NMDl6TWtwc2JFWlhWR1JRTmpWcGVYZHBUR2RGVm1wRU1GUjVaVlJzZHpOamRUZ3piRzFZWVUxVlRqUkVZbWhMUmxsRVJrbDRZU3RvSzF3dmIwb3piRTVGWTI5SVRuRXhkbEJsU1dkYWEySnRYQzlDVm1RellVTkpSRVEyY1hRMk4zRklSVTFvUTFGWWJEWlFZMGR0VkZseFlWWm9PU0lzSW5ScGJXVnpkR0Z0Y0NJNklqSXdNakF0TURRdE1UTlVNREE2TVRVNk1ERmFJaXdpYUdGemFDSTZJalZ1WEM5QlRGTmNMMGhqVldkaE1FMU9WVkJDY25jMmNERmtRbmRVYUZ3dlkxcG1ZelJ1YldWbGNVRnhNMEU5SW4wPSJ9fX19XX0.EEj-trk6nhBnoA5Brvp8BGGqpS_wS4TFRx7xmBDlsTk&custom_key=8caaa0e1a8abffb29ff31efe2e389d7a7a270933646fbc0a115f9db1a2c59696&player_version=html5&debug_mode=true";
            inkaPayLoad = "eyJkcm1fdHlwZSI6IldpZGV2aW5lIiwic2l0ZV9pZCI6IkpLUEIiLCJ1c2VyX2lkIjo1MDcsImNpZCI6IjIwMjAwMzE4LWIxaDZ6anp0IiwidG9rZW4iOiJmd2ZNUjgwMVNEbDRIZDAwQ2RrSzJCa1lENHEwZUpiQ1NsOHpmQUF0WUxyVTNmc2l1bW5jbXJtMEtOdTllcUl6UU5YZXZIMnJNTG9mM09zMkpsbEZXVGRQNjVpeXdpTGdFVmpEMFR5ZVRsdzNjdTgzbG1YYU1VTjREYmhLRllERkl4YStoK1wvb0ozbE5FY29ITnExdlBlSWdaa2JtXC9CVmQzYUNJREQ2cXQ2N3FIRU1oQ1FYbDZQY0dtVFlxYVZoOSIsInRpbWVzdGFtcCI6IjIwMjAtMDQtMTNUMDA6MTU6MDFaIiwiaGFzaCI6IjVuXC9BTFNcL0hjVWdhME1OVVBCcnc2cDFkQndUaFwvY1pmYzRubWVlcUFxM0E9In0=";
            if (mPlayer != null && holder != null) {
                mPlayer.setDataSource(jwtUrl, inkaPayLoad);
                mPlayer.prepareAsync();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.d(TAG, "surfaceChanged() width : " + width + "  height : " + height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.d(TAG, "surfaceDestroyed()");
            if (mMediaPlayer != null) {
                mMediaPlayer.changedDisplayState(false);
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "onPause");
        if (mPlayer != null) {
            mPlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        if (mPlayer != null) {
            mPlayer.finish();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                setLogText("play");
                mPlayer.start();
                break;
            case R.id.pause:
                setLogText("pause");
                mPlayer.pause();
                break;
            case R.id.rate_up:
                mPlayer.setPlayingRate(1);
                setLogText("Rate Up : " + String.format("%.1f", mPlayer.getPlayingRate()));
                break;
            case R.id.rate_down:
                mPlayer.setPlayingRate(-1);
                setLogText("Rate Down : " + String.format("%.1f", mPlayer.getPlayingRate()));
                break;
            case R.id.volume_up:
                CommonUtils.setStreamVolume(getContext(), true);
                mPlayer.setVolumeLevel(CommonUtils.getStreamVolume(getContext()));
                setLogText("Volume up : " + CommonUtils.getStreamVolume(getContext()));
                break;
            case R.id.volume_down:
                CommonUtils.setStreamVolume(getContext(), false);
                mPlayer.setVolumeLevel(CommonUtils.getStreamVolume(getContext()));
                setLogText("Volume down : " + CommonUtils.getStreamVolume(getContext()));
                break;
            case R.id.ff:
                mPlayer.setFF();
                setLogText("setFF(10) : " + mPlayer.getMediaPlayer().getCurrentPosition() + "ms");
                break;
            case R.id.rw:
                mPlayer.setRW();
                setLogText("setRW(10) : " + mPlayer.getMediaPlayer().getCurrentPosition() + "ms");
                break;
            case R.id.mute:
                mPlayer.setMute(true);
                setLogText("set Mute");
                break;
            case R.id.un_mute:
                mPlayer.setMute(false);
                setLogText("set unMute");
                break;
            case R.id.restart:
                mPlayer.prepareAsync();
                setLogText("re start");
                break;
            case R.id.callApp:
                setLogText("call Kollus App");
                //CommonUtils.startKollusApp(getContext(), jwtUrl);
                testFun();
                break;
            default:
                break;
        }
    }

    public void setLogText(String log) {
        if (log != null && mLogTextView != null) {
            mLogTextView.setText(log);
        }
    }

    public void testFun() {
        if (mPlayer != null) {
            jwtUrl = "https://v.jp.kollus.com/si?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHB0IjoxNTgxNTc2NzkwLCJjdWlkIjoia29sbHVzX3Rlc3QiLCJtYyI6W3sibWNrZXkiOiJJNDJvWm82YyIsImRybV9wb2xpY3kiOnsia2luZCI6Imlua2EiLCJzdHJlYW1pbmdfdHlwZSI6ImRhc2giLCJkYXRhIjp7ImxpY2Vuc2VfdXJsIjoiaHR0cHM6XC9cL2xpY2Vuc2UucGFsbHljb24uY29tXC9yaVwvbGljZW5zZU1hbmFnZXIuZG8iLCJjZXJ0aWZpY2F0ZV91cmwiOiJodHRwczpcL1wvbGljZW5zZS5wYWxseWNvbi5jb21cL3JpXC9mcHNLZXlNYW5hZ2VyLmRvP3NpdGVJZD1KS1BCIiwiY3VzdG9tX2hlYWRlciI6eyJrZXkiOiJwYWxseWNvbi1jdXN0b21kYXRhLXYyIiwidmFsdWUiOiJleUprY20xZmRIbHdaU0k2SWxkcFpHVjJhVzVsSWl3aWMybDBaVjlwWkNJNklrcExVRUlpTENKMWMyVnlYMmxrSWpvaWEyOXNiSFZ6WDNSbGMzUWlMQ0pqYVdRaU9pSXlNREl3TURJd05TMW1lalJwWXprMU5pSXNJblJ2YTJWdUlqb2labmRtVFZJNE1ERlRSR3cwU0dRd01FTmthMHN5UW10WlJEUnhNR1ZLWWtOVGJEaDZaa0ZCZEZsTWNsVXpabk5wZFcxdVkyMXliVEJMVG5VNVpYRkplbEZPV0dWMlNESnlUVXh2WmpOUGN6SktiR3hHVjFSa1VEWTFhWGwzYVV4blJWWnFSREJVZVdWVWJIY3pZM1U0TTJ4dFdHRk5WVTQwUkdKb1MwWlpSRVpKZUdFcmFDdGNMMjlLTTJ4T1JXTnZTRTV4TVhaUVpVbG5XbXRpYlZ3dlFsWmtNMkZEU1VSRU5uRjBOamRVVVRsY0wyeEZUaXR0WTNOdk1WSk1VMWRNYW1WVElpd2lkR2x0WlhOMFlXMXdJam9pTWpBeU1DMHdNaTB4TWxRd05qbzFNem94TUZvaUxDSm9ZWE5vSWpvaWNUQjFXbGRrV1UwMmJ6SmxiSEZWYWxSSFZUY3JjbTkyZUZkcmFqRjVUSHBrT0hWNE0yOURSMWhNT0QwaWZRPT0ifX19fV19.q-bT7HQWBPdQu-xhInFm9oRRC1yrcJA8zfWuiGIVzPw&custom_key=8caaa0e1a8abffb29ff31efe2e389d7a7a270933646fbc0a115f9db1a2c59696&debug_mode=true";
            //mPlayer.getMediaPlayer().setDisplay(mSurfaceView.getHolder());
            //mPlayer.setDataSource(playType, jwtUrl);
            mPlayer.prepareAsync();
        }
    }
}

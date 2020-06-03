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

import com.kollus.sdk.media.MediaPlayer;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import kollus.test.media.R;
import kollus.test.media.player.CustomPlayer;
import kollus.test.media.utils.CommonUtils;
import kollus.test.media.utils.LogUtil;

import static com.kollus.sdk.media.KollusStorage.TYPE_CACHE;
import static kollus.test.media.Config.MODE_MAKE_JWT;


public class PlayVideoFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = PlayVideoFragment.class.getSimpleName();

    //    private KollusStorage mStorage = null;
    private AudioManager mAudioManager = null;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceView mSurfaceView = null;
    private CustomPlayer mPlayer = null;
    private TextView mLogTextView = null;

    public int playType = TYPE_CACHE;
    public String jwtUrl = null;
//    public String jwtUrl = "http://v.kr.kollus.com/si?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdWlkIjoidGVzdFVlc3IiLCJleHB0IjoxNTc0NDk3NDg4LCJtYyI6W3sibWNrZXkiOiJrMWpGcElXTiJ9XX0.mQHwCdX9amqJSL40K4P72XwlYd9SYlto0vp00lQAti4&custom_key=e85dfe20589e9a8d767cf8feb070fb9dcd991176ec0a70a89eae05351492e2df";

    public static PlayVideoFragment newInstance() {
        return new PlayVideoFragment();
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
//                String mckey = "7vseZe0V";
                //String mckey = "vYQcpIWO";
                //String mckey = "mWb7m3ZP";
                String mckey = "ykT1mnKt";
                String cuid = "testUser";
                try {
                    jwtUrl = CommonUtils.createUrl(cuid, mckey, true);
                    //jwtUrl += "&uservalue0=testikwon@@@@@@@@@@@@@@";
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
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

        mMediaPlayer = new MediaPlayer(getContext(), mStorage, 1234);
        mPlayer = new CustomPlayer(getContext(), mMediaPlayer, mSurfaceView);

        return root;
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.d(TAG, "surfaceCreated()");

            if (mPlayer != null && holder != null) {
                mPlayer.getMediaPlayer().setDisplay(holder);
                mPlayer.setDataSource(playType, jwtUrl);
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
                mMediaPlayer.destroyDisplay();
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            // mPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        if (mPlayer != null) {
            // mPlayer.finish();
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
            mPlayer.setDataSource(playType, jwtUrl);
            mPlayer.prepareAsync();
        }
    }
}

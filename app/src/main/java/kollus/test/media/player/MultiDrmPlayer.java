package kollus.test.media.player;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.kollus.sdk.media.content.KollusContent;
import com.kollus.sdk.media2.DataSourceInfo;
import com.kollus.sdk.media2.KollusPlayerLMSListener;
import com.kollus.sdk.media2.MediaPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import kollus.test.media.utils.LogUtil;

public class MultiDrmPlayer {
    private static final String TAG = MultiDrmPlayer.class.getSimpleName();

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceVew;
    private int mPlayType;
    private String mSourceUrl;
    private String mSourceInkaPayload;
    private float mPlayingRate = 1.0f;

    // for subtitle
    private KollusContent mKollusContent;
    private Vector<KollusContent.SubtitleInfo> mSubtitles;

    public MultiDrmPlayer(Context context, MediaPlayer mediaPlayer, SurfaceView surfaceView) {
        this.mContext = context;
        this.mMediaPlayer = mediaPlayer;
        this.mSurfaceVew = surfaceView;

        initListener();
    }

    private void initListener() {
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setKollusPlayerLMSListener(kollusPlayerLMSListener);
    }

    public void start() {
        LogUtil.d(TAG, "start()");
        if (mMediaPlayer == null) {
            return;
        }

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void prepareAsync() {
        if (mMediaPlayer == null)
            return;

        /*
        if (TextUtils.isEmpty(mSourceUrl)) {
            LogUtil.d(TAG, "mSourceUrl is empty");
            return;
        }

        LogUtil.d(TAG, "mSourceUrl : " + mSourceUrl);

        if (mPlayType == 0) {
            //mMediaPlayer.setDataSource(mSourceUrl, "");
        } else {
            //mMediaPlayer.setData(mSourceUrl, "");
        }
        */
        LogUtil.d(TAG, "prepareAsync()");
        //mMediaPlayer.release();
        //mMediaPlayer.start();
    }

    public void pause() {
        LogUtil.d(TAG, "pause()");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public boolean setPlayingRate(int mode) {
        boolean result = false;

        switch (mode) {
            case -1:
                mPlayingRate -= 0.1f;
                break;
            case 1:
                mPlayingRate += 0.1f;
                break;
            default:
                mPlayingRate = 1;
                break;
        }

        if (mPlayingRate <= 0.5f) {
            mPlayingRate = 0.5f;
        }

        if (mPlayingRate >= 2.0f) {
            mPlayingRate = 2.0f;
        }

        if (mMediaPlayer != null) {
            LogUtil.d(TAG, "mPlayingRate : " + mPlayingRate);
            result = mMediaPlayer.setPlayingRate(mPlayingRate);
        }

        return result;
    }

    public void setVolumeLevel(int level) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolumeLevel(level);
    }

    public void setMute(boolean mute) {
        if (mMediaPlayer != null)
            mMediaPlayer.setMute(mute);
    }

    public void setFF() {
        if (mMediaPlayer != null) {
            LogUtil.d(TAG, "setFF() : getCurrentPosition : " + mMediaPlayer.getCurrentPosition());
            int sec = mMediaPlayer.getCurrentPosition() + 10000;
            mMediaPlayer.seekTo(sec);
        }
    }

    public void setRW() {
        if (mMediaPlayer != null) {
            LogUtil.d(TAG, "setRW() : getCurrentPosition : " + mMediaPlayer.getCurrentPosition());
            int sec = mMediaPlayer.getCurrentPosition() - 10000;
            mMediaPlayer.seekTo(sec);
        }
    }

    public void release() {
        LogUtil.d(TAG, "release()");
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
    }

    public void finish() {
        LogUtil.d(TAG, "finish()");
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public SurfaceView getSurfaceVew() {
        return mSurfaceVew;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public float getPlayingRate() {
        return mPlayingRate;
    }

    public void setDataSource(String url, String inkaDrm) {
        mSourceUrl = url;
        mSourceInkaPayload = inkaDrm;
        if (mSourceUrl != null && mSourceInkaPayload != null) {
            String proxy = "https://license.pallycon.com/ri/licenseManager.do";
            String headerKey = "pallycon-customdata-v2";

            DataSourceInfo info = new DataSourceInfo(mSourceUrl);
            HashMap<String, String> httpHeader = new HashMap<String, String>();
            httpHeader.put(headerKey, mSourceInkaPayload);
            info.setProxyInfo(proxy, httpHeader);

            try {
                mMediaPlayer.setDataSource(info, mSurfaceVew);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSizeSurfaceView(MediaPlayer mediaPlayer) {
        mSurfaceVew.getHolder().setFixedSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());

        LogUtil.d(TAG, "onPrepared() - getWidth : " + mSurfaceVew.getWidth());
        LogUtil.d(TAG, "onPrepared() - getHeight : " + mSurfaceVew.getHeight());
        LogUtil.d(TAG, "onPrepared() - left : " + mSurfaceVew.getLeft());
        LogUtil.d(TAG, "onPrepared() - right : " + mSurfaceVew.getRight());
        LogUtil.d(TAG, "onPrepared() - top : " + mSurfaceVew.getTop());
        LogUtil.d(TAG, "onPrepared() - bottom : " + mSurfaceVew.getBottom());

        int mVideoWidth = mediaPlayer.getVideoWidth();
        int mVideoHeight = mediaPlayer.getVideoHeight();
        int displayWidth = mSurfaceVew.getWidth();
        int displayHeight = mSurfaceVew.getHeight();

        int l = mSurfaceVew.getLeft();
        int r = mSurfaceVew.getRight();
        int t = mSurfaceVew.getTop();
        int b = mSurfaceVew.getBottom();

        // KollusPlayerContentMode.ScaleAspectFit

        if (mVideoWidth * displayHeight > displayWidth * mVideoHeight) {
            displayHeight = displayWidth * mVideoHeight / mVideoWidth;
        } else if (mVideoWidth * displayHeight < displayWidth * mVideoHeight) {
            displayWidth = displayHeight * mVideoWidth / mVideoHeight;
        }


        /* // KollusPlayerContentMode.ScaleAspectFill

        if (mVideoWidth * displayHeight > displayWidth * mVideoHeight) {
            displayWidth = displayHeight * mVideoWidth / mVideoHeight;
        } else if (mVideoWidth * displayHeight < displayWidth * mVideoHeight) {
            displayHeight = displayWidth * mVideoHeight / mVideoWidth;
        } */

        l = (r - l - displayWidth) / 2;
        r = l + displayWidth;
        t = (b - t - displayHeight) / 2;
        b = t + displayHeight;

        LogUtil.d(TAG, "onPrepared() - left : " + l);
        LogUtil.d(TAG, "onPrepared() - right : " + r);
        LogUtil.d(TAG, "onPrepared() - top : " + t);
        LogUtil.d(TAG, "onPrepared() - bottom : " + b);

        mSurfaceVew.layout(l, t, r, b);
    }


    public MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onPrepared()");

            mKollusContent = new KollusContent();

            mSubtitles = mKollusContent.getSubtitleInfo();
            if (mSubtitles.size() > 0) {
                KollusContent.SubtitleInfo subtitle = mSubtitles.get(0);
                Uri mCaptionUri = Uri.parse(subtitle.url);
                if (subtitle.url != null && subtitle.url.startsWith("http://")) {
                    //mediaPlayer.addTimedTextSource(mContext, mCaptionUri);\
                }
            }

            if (mediaPlayer != null) {
                setSizeSurfaceView(mediaPlayer);
                //LogUtil.d(TAG, "onPrepared() - getPlayAt() : " + mediaPlayer.get());
            }


        }
    };

    public MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onCompletion()");

            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.release();
            }
        }
    };

    public MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            LogUtil.d(TAG, "onError() what : " + what + " extra : " + extra);

            if (mediaPlayer != null) {
                LogUtil.d(TAG, "errorMsg : " + mediaPlayer.getErrorString(extra));
            }

            return false;
        }
    };

    public MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            LogUtil.d(TAG, "onVideoSizeChanged() width : " + width + "/ height : " + height);

            LogUtil.d(TAG, String.format("onVideoSizeChanged (%d %d) dimension(%d %d)",
                    width, height, mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight()));
            if (mediaPlayer.getVideoWidth() != 0 && mediaPlayer.getVideoHeight() != 0) {
                mSurfaceVew.getHolder().setFixedSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
                mSurfaceVew.requestLayout();
            }
        }
    };

    // LMS data 가 서버에 올라 갈 때 주고 받은 데이터를 확인하는 리스너
    private KollusPlayerLMSListener kollusPlayerLMSListener = new KollusPlayerLMSListener() {

        @Override
        public void onLMS(String request, String response) {
            LogUtil.d(TAG, "request : " + request + " / response : " + response);
        }
    };


}

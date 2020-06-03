
//not used
//package kollus.test.media.player;
//
//import android.app.Activity;
//import android.content.Context;
//import android.media.AudioManager;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import java.lang.ref.WeakReference;
//import java.util.Formatter;
//import java.util.Locale;
//
//import kollus.test.media.R;
//
//
//public class MediaController extends FrameLayout {
//
//    private static final String TAG = MediaController.class.getSimpleName();
//
//    private static final int HANDLER_ANIMATE_OUT = 1;// out animate
//    private static final int HANDLER_UPDATE_PROGRESS = 2;//cycle update progress
//    private static final long PROGRESS_SEEK = 500;
//    private static final long ANIMATE_TIME = 300;
//
//    private View mRootView; // root view of this
//    private SeekBar mSeekBar; //seek bar for video
//    private TextView mEndTime, mCurrentTime;
//    private boolean mIsShowing;//controller view showing
//    private boolean mIsDragging; //is dragging seekBar
//    private StringBuilder mFormatBuilder;
//    private Formatter mFormatter;
//
//    private Activity mContext;
//    private boolean mCanSeekVideo;
//    private boolean mCanControlVolume;
//    private boolean mCanControlBrightness;
//    private String mVideoTitle;
//    private ControlListener mMediaPlayerControlListener;
//    private ViewGroup mAnchorView;
//    private SurfaceView mSurfaceView;
//
//    private int mExitIcon;
//    private int mPauseIcon;
//    private int mPlayIcon;
//    private int mShrinkIcon;
//    private int mStretchIcon;
//
//    //top layout
//    private View mTopLayout;
//    private ImageButton mBackButton;
//    private TextView mTitleText;
//
//    //center layout
//    private View mCenterLayout;
//    private ImageView mCenterImage;
//    private ProgressBar mCenterProgress;
//    private float mCurBrightness = -1;
//    private int mCurVolume = -1;
//    private AudioManager mAudioManager;
//    private int mMaxVolume;
//
//    //bottom layout
//    private View mBottomLayout;
//    private ImageButton mPauseButton;
//    private ImageButton mFullscreenButton;
//
//    private Handler mHandler = new ControllerViewHandler(this);
//
//    public MediaController(Context context) {
//        super(context);
//    }
//
////    public MediaController() {
////        setAnchorView
////
////    }
////    public MediaController() {
////        setAnchorView(builder.anchorView);
////        this.mSurfaceView.setOnTouchListener(new OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                toggleControllerView();
////                return false;
////            }
////        });
////    }
//
//    /**
//     * Handler prevent leak memory.
//     */
//    private static class ControllerViewHandler extends Handler {
//        private final WeakReference<MediaController> mView;
//
//        ControllerViewHandler(MediaController view) {
//            mView = new WeakReference<>(view);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            MediaController view = mView.get();
//            if (view == null || view.mMediaPlayerControlListener == null) {
//                return;
//            }
//
//            int pos;
//            switch (msg.what) {
//                case HANDLER_ANIMATE_OUT:
//                    view.hide();
//                    break;
//                case HANDLER_UPDATE_PROGRESS://cycle update seek bar progress
//                    pos = view.setSeekProgress();
//                    if (!view.mIsDragging && view.mIsShowing && view.mMediaPlayerControlListener.isPlaying()) {//just in case
//                        //cycle update
//                        msg = obtainMessage(HANDLER_UPDATE_PROGRESS);
//                        sendMessageDelayed(msg, 1000 - (pos % 1000));
//                    }
//                    break;
//            }
//        }
//    }
//
//    private View makeControllerView() {
//        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mRootView = inflate.inflate(R.layout.media_controller, null);
//        initControllerView();
//
//        return mRootView;
//    }
//
//    private void initControllerView() {
//        //top layout
//        mTopLayout = mRootView.findViewById(R.id.layout_top);
//        mBackButton = mRootView.findViewById(R.id.top_back);
//        mBackButton.setImageResource(mExitIcon);
//        if (mBackButton != null) {
//            mBackButton.requestFocus();
//            mBackButton.setOnClickListener(mBackListener);
//        }
//
//        mTitleText = mRootView.findViewById(R.id.top_title);
//
//        //center layout
//        mCenterLayout = mRootView.findViewById(R.id.layout_center);
//        mCenterLayout.setVisibility(GONE);
//        mCenterImage = mRootView.findViewById(R.id.image_center_bg);
//        mCenterProgress = mRootView.findViewById(R.id.progress_center);
//
//        //bottom layout
//        mBottomLayout = mRootView.findViewById(R.id.layout_bottom);
//        mPauseButton = mRootView.findViewById(R.id.bottom_pause);
//        if (mPauseButton != null) {
//            mPauseButton.requestFocus();
//            mPauseButton.setOnClickListener(mPauseListener);
//        }
//
//        mFullscreenButton = mRootView.findViewById(R.id.bottom_fullscreen);
//        if (mFullscreenButton != null) {
//            mFullscreenButton.requestFocus();
//            mFullscreenButton.setOnClickListener(mFullscreenListener);
//        }
//
//        mSeekBar = mRootView.findViewById(R.id.bottom_seekbar);
//        if (mSeekBar != null) {
//            mSeekBar.setOnSeekBarChangeListener(mSeekListener);
//            mSeekBar.setMax(1000);
//        }
//
//        mEndTime = mRootView.findViewById(R.id.bottom_time);
//        mCurrentTime = mRootView.findViewById(R.id.bottom_time_current);
//
//        //init formatter
//        mFormatBuilder = new StringBuilder();
//        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
//    }
//
//    /**
//     * show controller view
//     */
//    private void show() {
//
//        if (!mIsShowing && mAnchorView != null) {
//
//            //add controller view to bottom of the AnchorView
//            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            mAnchorView.addView(MediaController.this, tlp);
//
////            ViewAnimator.putOn(mTopLayout)
////                    .waitForSize(new ViewAnimator.Listeners.Size() {
////                        @Override
////                        public void onSize(ViewAnimator viewAnimator) {
////                            viewAnimator.animate()
////                                    .translationY(-mTopLayout.getHeight(), 0)
////                                    .duration(ANIMATE_TIME)
////                                    .andAnimate(mBottomLayout)
////                                    .translationY(mBottomLayout.getHeight(), 0)
////                                    .duration(ANIMATE_TIME)
////                                    .start(new ViewAnimator.Listeners.Start() {
////                                        @Override
////                                        public void onStart() {
////                                            mIsShowing = true;
////                                            mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
////                                        }
////                                    });
////                        }
////                    });
//        }
//
//        setSeekProgress();
//        if (mPauseButton != null) {
//            mPauseButton.requestFocus();
//        }
//        togglePausePlay();
//        toggleFullScreen();
//        //update progress
//        mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
//
//    }
//
//    public void toggleControllerView() {
//        if (!isShowing()) {
//            show();
//        } else {
//            //animate out controller view
//            Message msg = mHandler.obtainMessage(HANDLER_ANIMATE_OUT);
//            //remove exist one first
//            mHandler.removeMessages(HANDLER_ANIMATE_OUT);
//            mHandler.sendMessageDelayed(msg, 100);
//        }
//    }
//
//    public boolean isShowing() {
//        return mIsShowing;
//    }
//
//    /**
//     * hide controller view with animation
//     * With custom animation
//     */
//    private void hide() {
//        if (mAnchorView == null) {
//            return;
//        }
//
////        ViewAnimator.putOn(mTopLayout)
////                .animate()
////                .translationY(-mTopLayout.getHeight())
////                .duration(ANIMATE_TIME)
////
////                .andAnimate(mBottomLayout)
////                .translationY(mBottomLayout.getHeight())
////                .duration(ANIMATE_TIME)
////                .end(new ViewAnimator.Listeners.End() {
////                    @Override
////                    public void onEnd() {
////                        mAnchorView.removeView(MediaController.this);
////                        mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
////                        mIsShowing = false;
////                    }
////                });
//    }
//
//
//    private String stringToTime(int timeMs) {
//        int totalSeconds = timeMs / 1000;
//
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours = totalSeconds / 3600;
//
//        mFormatBuilder.setLength(0);
//        if (hours > 0) {
//            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
//        }
//    }
//
//    private int setSeekProgress() {
//        if (mMediaPlayerControlListener == null || mIsDragging) {
//            return 0;
//        }
//
//        int position = mMediaPlayerControlListener.getCurrentPosition();
//        int duration = mMediaPlayerControlListener.getDuration();
//        if (mSeekBar != null) {
//            if (duration > 0) {
//                // use long to avoid overflow
//                long pos = 1000L * position / duration;
//                mSeekBar.setProgress((int) pos);
//            }
//            //get buffer percentage
//            int percent = mMediaPlayerControlListener.getBufferPercentage();
//            //set buffer progress
//            mSeekBar.setSecondaryProgress(percent * 10);
//        }
//
//        if (mEndTime != null)
//            mEndTime.setText(stringToTime(duration));
//        if (mCurrentTime != null) {
//            Log.e(TAG, "position:" + position + " -> duration:" + duration);
//            mCurrentTime.setText(stringToTime(position));
////            if (mMediaPlayerControlListener.isComplete()) {
////                mCurrentTime.setText(stringToTime(duration));
////            }
//        }
//        mTitleText.setText(mVideoTitle);
//        return position;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//                mCurVolume = -1;
//                mCurBrightness = -1;
//                mCenterLayout.setVisibility(GONE);
////                break;// do need bread,should let gestureDetector to handle event
//            default://gestureDetector handle other MotionEvent
//        }
//        return true;
//
//    }
//
//    /**
//     * toggle pause or play
//     */
//    private void togglePausePlay() {
//        if (mRootView == null || mPauseButton == null || mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        if (mMediaPlayerControlListener.isPlaying()) {
//            mPauseButton.setImageResource(mPauseIcon);
//        } else {
//            mPauseButton.setImageResource(mPlayIcon);
//        }
//    }
//
//    /**
//     * toggle full screen or not
//     */
//    public void toggleFullScreen() {
//        if (mRootView == null || mFullscreenButton == null || mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        if (mMediaPlayerControlListener.isFullScreen()) {
//            mFullscreenButton.setImageResource(mShrinkIcon);
//        } else {
//            mFullscreenButton.setImageResource(mStretchIcon);
//        }
//    }
//
//    private void doPauseResume() {
//        if (mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        if (mMediaPlayerControlListener.isPlaying()) {
//            mMediaPlayerControlListener.pause();
//        } else {
//            mMediaPlayerControlListener.start();
//        }
//        togglePausePlay();
//    }
//
//    private void doToggleFullscreen() {
//        if (mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        mMediaPlayerControlListener.toggleFullScreen();
//    }
//
//    /**
//     * Seek bar drag listener
//     */
//    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
//        public void onStartTrackingTouch(SeekBar bar) {
//            show();
//            mIsDragging = true;
//            mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
//        }
//
//        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
//            if (mMediaPlayerControlListener == null) {
//                return;
//            }
//
//            if (!fromuser) {
//                return;
//            }
//
//            long duration = mMediaPlayerControlListener.getDuration();
//            long newPosition = (duration * progress) / 1000L;
//            mMediaPlayerControlListener.seekTo((int) newPosition);
//            if (mCurrentTime != null)
//                mCurrentTime.setText(stringToTime((int) newPosition));
//        }
//
//        public void onStopTrackingTouch(SeekBar bar) {
//            mIsDragging = false;
//            setSeekProgress();
//            togglePausePlay();
//            show();
//            mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
//        }
//    };
//
//    @Override
//    public void setEnabled(boolean enabled) {
//        if (mPauseButton != null) {
//            mPauseButton.setEnabled(enabled);
//        }
//        if (mSeekBar != null) {
//            mSeekBar.setEnabled(enabled);
//        }
//        super.setEnabled(enabled);
//    }
//
//    private View.OnClickListener mBackListener = new View.OnClickListener() {
//        public void onClick(View v) {
////            mMediaPlayerControlListener.exit();
//        }
//    };
//
//    private View.OnClickListener mPauseListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            doPauseResume();
//            show();
//        }
//    };
//
//    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            doToggleFullscreen();
//            show();
//        }
//    };
//
//
//    public void setMediaPlayerControlListener(ControlListener controlListener) {
//        mMediaPlayerControlListener = controlListener;
//        togglePausePlay();
//        toggleFullScreen();
//    }
//
//    /**
//     * set anchor view
//     *
//     * @param view view that hold controller view
//     */
//    private void setAnchorView(ViewGroup view) {
//        mAnchorView = view;
//        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//        //remove all before add view
//        removeAllViews();
//        View v = makeControllerView();
//        addView(v, frameParams);
//
//    }
//
//    private void seekBackWard() {
//        if (mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        int pos = mMediaPlayerControlListener.getCurrentPosition();
//        pos -= PROGRESS_SEEK;
//        mMediaPlayerControlListener.seekTo(pos);
//        setSeekProgress();
//
//        show();
//    }
//
//    private void seekForWard() {
//        if (mMediaPlayerControlListener == null) {
//            return;
//        }
//
//        int pos = mMediaPlayerControlListener.getCurrentPosition();
//        pos += PROGRESS_SEEK;
//        mMediaPlayerControlListener.seekTo(pos);
//        setSeekProgress();
//
//        show();
//    }
//
//    /**
//     * update volume by seek percent
//     *
//     * @param percent seek percent
//     */
//    private void updateVolume(float percent) {
//
//        mCenterLayout.setVisibility(VISIBLE);
//
//        if (mCurVolume == -1) {
//            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            if (mCurVolume < 0) {
//                mCurVolume = 0;
//            }
//        }
//
//        int volume = (int) (percent * mMaxVolume) + mCurVolume;
//        if (volume > mMaxVolume) {
//            volume = mMaxVolume;
//        }
//
//        if (volume < 0) {
//            volume = 0;
//        }
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
//
//        int progress = volume * 100 / mMaxVolume;
//        mCenterProgress.setProgress(progress);
//    }
//
//    /**
//     * update brightness by seek percent
//     *
//     * @param percent seek percent
//     */
//    private void updateBrightness(float percent) {
//
//        if (mCurBrightness == -1) {
//            mCurBrightness = mContext.getWindow().getAttributes().screenBrightness;
//            if (mCurBrightness <= 0.01f) {
//                mCurBrightness = 0.01f;
//            }
//        }
//
//        mCenterLayout.setVisibility(VISIBLE);
//
//        WindowManager.LayoutParams attributes = mContext.getWindow().getAttributes();
//        attributes.screenBrightness = mCurBrightness + percent;
//        if (attributes.screenBrightness >= 1.0f) {
//            attributes.screenBrightness = 1.0f;
//        } else if (attributes.screenBrightness <= 0.01f) {
//            attributes.screenBrightness = 0.01f;
//        }
//        mContext.getWindow().setAttributes(attributes);
//
//        float p = attributes.screenBrightness * 100;
//        mCenterProgress.setProgress((int) p);
//
//    }
//}
//

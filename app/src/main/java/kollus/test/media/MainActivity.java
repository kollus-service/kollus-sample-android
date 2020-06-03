package kollus.test.media;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.List;

import kollus.test.media.ui.fragment.ContentsListFragment;
import kollus.test.media.ui.fragment.DownLoadFragment;
import kollus.test.media.ui.fragment.ExoPlayerFragment;
import kollus.test.media.ui.fragment.PlayMultiDrmFragment;
import kollus.test.media.ui.fragment.PlayVideoFragment;
import kollus.test.media.ui.fragment.WebViewPlayFragment;
import kollus.test.media.utils.LogUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int currentPos = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate()");

        /*
        File file = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "testFile");
        if (!file.mkdirs()) {
            LogUtil.e(TAG, "Directory not created");
        }

        LogUtil.e(TAG, "Directory : " + file.getAbsolutePath());
                */

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.main_activity);


        Button btn_first = (Button) findViewById(R.id.btn_first);
        Button btn_second = (Button) findViewById(R.id.btn_second);
        Button btn_third = (Button) findViewById(R.id.btn_third);
        Button btn_fourth = (Button) findViewById(R.id.btn_fourth);
        Button btn_fifth = (Button) findViewById(R.id.btn_fifth);
        Button btn_sixth = (Button) findViewById(R.id.btn_sixth);

        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);
        btn_fourth.setOnClickListener(movePageListener);
        btn_fourth.setTag(3);
        btn_fifth.setOnClickListener(movePageListener);
        btn_fifth.setTag(4);
        btn_sixth.setOnClickListener(movePageListener);
        btn_sixth.setTag(5);

        startFragment(0);

    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int tag = (int) view.getTag();
            LogUtil.d(TAG, "onClick() : pos : " + tag);
            startFragment(tag);
        }
    };

    private void startFragment(int tagNo) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentPos == tagNo) {
            return;
        }
        switch (tagNo) {
            case 0:
                transaction.replace(R.id.container, new PlayVideoFragment());
                transaction.commit();
                currentPos = 0;
                break;

            case 1:
                transaction.replace(R.id.container, new DownLoadFragment());
                transaction.commit();
                currentPos = 1;
                break;

            case 2:
                transaction.replace(R.id.container, new ContentsListFragment());
                transaction.commit();
                currentPos = 2;
                break;

            case 3:
                transaction.replace(R.id.container, new WebViewPlayFragment());
                transaction.commit();
                currentPos = 3;
                break;
            case 4:
                //transaction.replace(R.id.container, new ExoPlayerFragment());
                transaction.replace(R.id.container, new PlayMultiDrmFragment());
                transaction.commit();
                currentPos = 4;
                break;
            case 5:
                transaction.replace(R.id.container, new ExoPlayerFragment());
                //transaction.replace(R.id.container, new PlayMultiDrmFragment());
                transaction.commit();
                currentPos = 5;
                break;

            default:
                break;
        }
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void replaceFragment(Fragment fragment, int type, String urlOrMcKey) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putInt("playType", type);
        bundle.putString("urlOrMcKey", urlOrMcKey);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container, fragment).commit();
    }


    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof OnBackPressedListener) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                }
            }
        }
    }

    public void setHiddneIcon(){

    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}

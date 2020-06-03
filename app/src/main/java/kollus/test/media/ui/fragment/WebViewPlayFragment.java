package kollus.test.media.ui.fragment;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import kollus.test.media.MainActivity;
import kollus.test.media.R;
import kollus.test.media.hybrid.FullscreenableChromeClient;
import kollus.test.media.hybrid.HybridWebView;
import kollus.test.media.utils.CommonUtils;
import kollus.test.media.utils.LogUtil;

import static kollus.test.media.Config.MODE_MAKE_JWT;

public class WebViewPlayFragment extends BaseFragment implements MainActivity.OnBackPressedListener {

    private static final String TAG = WebViewPlayFragment.class.getSimpleName();

    private HybridWebView mWebView;
    private String url = "http://v.kr.kollus.com/s?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdWlkIjoidGVzdFVzZXIiLCJleHB0IjoxNTc3NDI5MjkwLCJ2aWRlb193YXRlcm1hcmtpbmdfY29kZV9wb2xpY3kiOnsiY29kZV9raW5kIjoidGVzdFVzZXIhISEhIiwiZm9udF9zaXplIjo3MCwiZm9udF9jb2xvciI6IkZGMDAwMCIsInNob3dfdGltZSI6MTAsImhpZGVfdGltZSI6MCwiYWxwaGEiOjkwLCJlbmFibGVfaHRtbDVfcGxheWVyIjp0cnVlfSwibWMiOlt7Im1ja2V5IjoiZnYzSHpxeHcifV19.qfFM7Hv-VqUV7gSVtVMlcEt769s5ey8bUf9bGE4tzTg&custom_key=e85dfe20589e9a8d767cf8feb070fb9dcd991176ec0a70a89eae05351492e2df";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_webview, container, false);

        mWebView = (HybridWebView) root.findViewById(R.id.webView);
        mWebView.setWebViewClient(new CustomWebViewClient());
//        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.setWebChromeClient(new FullscreenableChromeClient(getActivity()));
        mWebView.setWebContentsDebuggingEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true);

        if (MODE_MAKE_JWT) {
            String mckey = "VoeuqoGi";
            try {
                url = CommonUtils.createUrl("testUser", mckey, false);
//                url = "http://tech.kollus.com/sample_seo/index.php";
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        url ="https://v.jp.kollus.com/s?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHB0IjoxNTgyOTM2MjQ1LCJjdWlkIjoia29sbHVzX3Rlc3QiLCJtYyI6W3sibWNrZXkiOiJPZG0wZmNQUiIsImRybV9wb2xpY3kiOnsia2luZCI6Imlua2EiLCJzdHJlYW1pbmdfdHlwZSI6ImRhc2giLCJkYXRhIjp7ImxpY2Vuc2VfdXJsIjoiaHR0cHM6XC9cL2xpY2Vuc2UucGFsbHljb24uY29tXC9yaVwvbGljZW5zZU1hbmFnZXIuZG8iLCJjZXJ0aWZpY2F0ZV91cmwiOiJodHRwczpcL1wvbGljZW5zZS5wYWxseWNvbi5jb21cL3JpXC9mcHNLZXlNYW5hZ2VyLmRvP3NpdGVJZD1KS1BCIiwiY3VzdG9tX2hlYWRlciI6eyJrZXkiOiJwYWxseWNvbi1jdXN0b21kYXRhLXYyIiwidmFsdWUiOiJleUprY20xZmRIbHdaU0k2SWxkcFpHVjJhVzVsSWl3aWMybDBaVjlwWkNJNklrcExVRUlpTENKMWMyVnlYMmxrSWpvaWEyOXNiSFZ6WDNSbGMzUWlMQ0pqYVdRaU9pSXlNREU1TURreU5pMWpabTk2ZUdKNk5pSXNJblJ2YTJWdUlqb2labmRtVFZJNE1ERlRSR3cwU0dRd01FTmthMHN5UW10WlJEUnhNR1ZLWWtOVGJEaDZaa0ZCZEZsTWNsVXpabk5wZFcxdVkyMXliVEJMVG5VNVpYRkplbEZPV0dWMlNESnlUVXh2WmpOUGN6SktiR3hHVjFSa1VEWTFhWGwzYVV4blJWWnFSREJVZVdWVWJIY3pZM1U0TTJ4dFdHRk5WVTQwUkdKb1MwWlpSRVpKZUdFcmFDdGNMMjlLTTJ4T1JXTnZTRTV4TVhaUVpVbG5XbXRpYlZ3dlFsWmtNMkZEU1VSRU5uRjBOamRVVVRsY0wyeEZUaXR0WTNOdk1WSk1VMWRNYW1WVElpd2lkR2x0WlhOMFlXMXdJam9pTWpBeU1DMHdNaTB5T0ZRd01Eb3pNRG8wTlZvaUxDSm9ZWE5vSWpvaVhDOTFVMjFLVm1sRE5FWjRPSGxqWVhsUlltaExXSEpZUm13MVltOURlV29yTURGUlYzRTRSazlSWjJNOUluMD0ifX19fV19.dwgOKISFDGTeuqzPBKN1cdoJ0k7F1-47PFo-pJtZBqo&custom_key=8caaa0e1a8abffb29ff31efe2e389d7a7a270933646fbc0a115f9db1a2c59696";
        //mWebView.loadUrl(url);
        mWebView.loadUrl("file:///android_asset/index.html");

        return root;
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            getActivity().finish();
        }
    }

    class CustomWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    class CustomWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            LogUtil.d(TAG, cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            LogUtil.d(TAG, message + " -- From line "
                    + lineNumber + " of "
                    + sourceID);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }
}

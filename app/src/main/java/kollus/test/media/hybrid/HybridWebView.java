package kollus.test.media.hybrid;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HybridWebView extends WebView {
    private static final String DEFAULT_JAVASCRIPT_INTERFACE_NAME = "Native";
    private static final String TAG = HybridWebView.class.getSimpleName();

    private String javascriptInterfaceName = DEFAULT_JAVASCRIPT_INTERFACE_NAME;
    private WebSettings mWebSettings;
    private BridgeProxy mBridgeProxy;
    protected Context mContext;
    protected boolean mImeBackKeyEvent = false;

    public HybridWebView(Context context) {
        super(context);
        mContext = context;
        setup();
    }

    public HybridWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setup();
    }

    public HybridWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setup();
    }

    protected void setup() {
        mWebSettings = getSettings();
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mWebSettings.setTextZoom(100);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDefaultTextEncodingName("euc-kr");
    }

    public void registerBridge(ArrayList<Object> receiver) {
        Map<String, BridgeInfo> methodSet = new HashMap<String, BridgeInfo>();

        for (Object obj : receiver) {
            Class<?> cls = obj.getClass();
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                Bridge bridge = method.getAnnotation(Bridge.class);
                if (bridge != null) {
                    methodSet.put(bridge.value(), new BridgeInfo(obj, method));
                }
            }
        }

        Handler handler = new Handler(getContext().getMainLooper());
        mBridgeProxy = new BridgeProxy(this, handler);
        addJavascriptInterface(mBridgeProxy, javascriptInterfaceName);
        mBridgeProxy.registerBridgeReceiver(methodSet);
    }

    public void call(String command, String str) {
        mBridgeProxy.notifyToWeb(command, str);
    }

    public void setInterFaceName(String inrerface) {
        javascriptInterfaceName = inrerface;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mImeBackKeyEvent = true;
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}

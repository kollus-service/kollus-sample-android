package kollus.test.media.hybrid;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kollus.test.media.utils.LogUtil;

public class BridgeProxy {
    private static final String TAG = BridgeProxy.class.getSimpleName();

    private static final String WEB_FUNC_NAME = "window.OnNativeEvent";

    private Handler mHandler = null;
    private WebView mWebView = null;
    //    private Object mBridgeReceiver;
    private Map<String, BridgeInfo> mBridgeMethodSet = new HashMap<String, BridgeInfo>();

    static Set shouldWaitResultCommands = new HashSet();

    public BridgeProxy(WebView webView, Handler handler) {
        this.mWebView = webView;
        this.mHandler = handler;
    }

    public void registerBridgeReceiver(Map<String, BridgeInfo> bridgeMethodSet) {
        //        this.mBridgeReceiver = bridgeReceiver;
        this.mBridgeMethodSet = bridgeMethodSet;
    }

    public void setBridgeHandler(Handler handler) {
        mHandler = handler;
    }

    @JavascriptInterface
    public void Execute(final String command, final String param) {
        if (mHandler == null || command == null && param == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invokeMethod(command, param);
            }
        });
    }

    // web --> native
    private void invokeMethod(String command, String param) {
        try {
            Object result = null;
            BridgeInfo bridge = mBridgeMethodSet.get(command);

            if (bridge != null) {
                Method method = bridge.getMethodName();
                if (method == null) {
                }

                if (param == null || param.equals("undefined")) {
                    result = method.invoke(bridge.getObjectName());
                } else {
                    result = method.invoke(bridge.getObjectName(), param);
                }
                if (!shouldWaitResultCommands.contains(command)) {
                    notifyToWeb(command, result == null ? null : result.toString());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // native --> web
    public void notifyToWeb(final String command, final String obj) {
        if (mHandler != null && mWebView != null) {
            LogUtil.d(TAG, "[native --> web] command : " + command + ", obj : " + obj + ", [to : " + mWebView.getClass().getSimpleName() + "]");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(makeJavaScript(WEB_FUNC_NAME, command, obj));
                }
            });
        }
    }

    public String makeJavaScript(String func, String command, String obj) {
        StringBuilder builder = new StringBuilder();
        builder.append("javascript:");
        builder.append(func);
        builder.append("('" + command + "'");
        builder.append(", ");
        builder.append("'" + obj + "')");

        return builder.toString();
    }

}

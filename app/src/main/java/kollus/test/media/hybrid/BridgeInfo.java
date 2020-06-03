package kollus.test.media.hybrid;

import java.lang.reflect.Method;

public class BridgeInfo {
    private Object obj;
    private Method method;

    public BridgeInfo(Object object, Method method) {
        this.method = method;
        this.obj = object;
    }

    public Object getObjectName() {
        return obj;
    }

    public Method getMethodName() {
        return method;
    }

}

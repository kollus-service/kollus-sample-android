package kollus.test.media.player;

import android.content.Context;

import com.kollus.sdk.media.util.Utils;

public class KollusConstant {
    //public static final String KEY = "24aea28d5b882a72d230eed446e97e4bccb6de20";
    //public static final String EXPIRE_DATE = "2019/12/31";

    public static final String KEY = "KEY";
    public static final String EXPIRE_DATE = "EXPIRE_DATE";
    public static final int ZAPPING_INTERVAL = 100;

    public static final boolean AUTO_UPDATE = true;
    public static final boolean RELEASE_MODE = false;
    public static final boolean SECURE_MODE = true;

    public static final int NETWORK_TIMEOUT_SEC = 10;
    public static final int NETWORK_RETRY_COUNT = 3;

    public static String getPlayerId(Context context) {
        String playerId;
        playerId = Utils.createUUIDSHA1(context);

        return playerId;
    }

    public static String getPlayerIdWithMD5(Context context) {
        String playerIdWidthMd5;
        playerIdWidthMd5 = Utils.createUUIDMD5(context);
        return playerIdWidthMd5;
    }
}

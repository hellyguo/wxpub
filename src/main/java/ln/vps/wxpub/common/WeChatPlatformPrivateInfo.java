package ln.vps.wxpub.common;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Helly on 2017/02/10.
 */
public final class WeChatPlatformPrivateInfo {
    public static final String APP_ID;
    public static final String APP_SECRET;
    public static final String TOKEN;
    public static final String ENCODING_AES_KEY;
    public static final String ORIGIN_ID;
    public static final String PUBLIC_ID;
    public static final String SELF_FOLLOWER_ID;

    static {
        try {
            Properties prop = new Properties();
            prop.load(WeChatPlatformPrivateInfo.class.getClassLoader().getResourceAsStream("wx_private.properties"));
            APP_ID = prop.getProperty("APP_ID");
            APP_SECRET = prop.getProperty("APP_SECRET");
            TOKEN = prop.getProperty("TOKEN");
            ENCODING_AES_KEY = prop.getProperty("ENCODING_AES_KEY");
            ORIGIN_ID = prop.getProperty("ORIGIN_ID");
            PUBLIC_ID = prop.getProperty("PUBLIC_ID");
            SELF_FOLLOWER_ID = prop.getProperty("SELF_FOLLOWER_ID");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WeChatPlatformPrivateInfo() {
    }
}

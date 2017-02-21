package ln.vps.wxpub.util;

/**
 * Created by Helly on 2017/02/15.
 */
public final class IntTimeStamp {
    public static int timestamp() {
        long longTimestamp = System.currentTimeMillis();
        return (int) (longTimestamp / 1000);
    }

    private IntTimeStamp() {
    }
}

package ln.vps.wxpub.common;

/**
 * Created by Helly on 2017/02/15.
 */
public class WxPubException extends Exception {
    public WxPubException() {
    }

    public WxPubException(String message) {
        super(message);
    }

    public WxPubException(String message, Throwable cause) {
        super(message, cause);
    }

    public WxPubException(Throwable cause) {
        super(cause);
    }

    public WxPubException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package ln.vps.wxpub.process;

/**
 * Created by Helly on 2017/02/15.
 */
public class RetData {
    private String retMsg;
    private boolean sendMail;

    public RetData(String retMsg, boolean sendMail) {
        this.retMsg = retMsg;
        this.sendMail = sendMail;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public boolean isSendMail() {
        return sendMail;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }
}

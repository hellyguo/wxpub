package ln.vps.wxpub.command;

/**
 * Created by Helly on 2017/02/15.
 */
public interface Commander {

    String execute(String fromUserName, String cmd, String[] params);
}

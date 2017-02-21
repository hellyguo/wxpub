package ln.vps.wxpub.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helly on 2017/02/15.
 */
public class CommandDispatcher {
    private Map<String, Commander> commanderMap = new HashMap<>();

    public CommandDispatcher() {
        commanderMap.put("help", new HelpCommander());
        commanderMap.put("javadoc", new JavaDocCommander());
        commanderMap.put("linuxman",new LinuxManCommander());
    }

    public String dispatch(String fromUserName, String cmd, String[] params) {
        Commander commander = commanderMap.get(cmd);
        if (commander == null) {
            return null;
        } else {
            return commander.execute(fromUserName, cmd, params);
        }
    }
}

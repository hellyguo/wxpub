package ln.vps.wxpub.command;

import ln.vps.wxpub.util.LruMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Helly on 2017/02/15.
 */
public class LinuxManCommander implements Commander {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxManCommander.class);
    private static final int MAX_SIZE = 2000;
    private LruMap<String, String> manPageMap = new LruMap<>(MAX_SIZE, LruMap.KeepOrder.ACCESS_ORDER);
    private Properties manProp = new Properties();

    public LinuxManCommander() {
        try {
            manProp.load(this.getClass().getClassLoader().getResourceAsStream("man.properties"));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public String execute(String fromUserName, String cmd, String[] params) {
        String manTarget = params[0].trim();
        if (manPageMap.containsKey(manTarget)) {
            return manPageMap.get(manTarget);
        } else {
            List<String> manPages = queryManPages(manTarget);
            if (manPages.isEmpty()) {
                return "未找到" + manTarget + "对应的手册";
            } else {
                StringBuilder buf = new StringBuilder();
                manPages.forEach(page -> buf.append(manProp.getProperty(page))
                        .append('\n')
                        .append('\n'));
                buf.setLength(buf.length() - 2);
                String retMsg = buf.toString();
                manPageMap.put(manTarget, retMsg);
                return retMsg;
            }
        }
    }

    private List<String> queryManPages(String manTarget) {
        List<String> cmds = new ArrayList<>();
        manProp.keySet().forEach(key -> {
            String keyStr = (String) key;
            if (keyStr.startsWith(manTarget + "_")) {
                cmds.add(keyStr);
            }
        });
        return cmds;
    }
}

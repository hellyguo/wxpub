package ln.vps.wxpub.process;

import java.util.Map;

/**
 * Created by Helly on 2017/02/15.
 */
public interface Processor {
    String RET_MSG_SUCCESS = "success";
    String RET_MSG_EMPTY = "";

    RetData process(Map<String, String> msg);
}

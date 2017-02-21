package ln.vps.wxpub.process;

import ln.vps.wxpub.common.WeChatMsgType;

import java.util.HashMap;
import java.util.Map;

import static ln.vps.wxpub.common.WeChatMsgType.*;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_MSG_TYPE;
import static ln.vps.wxpub.process.Processor.RET_MSG_SUCCESS;

/**
 * Created by Helly on 2017/02/15.
 */
public class ProcessDispatcher {
    private Map<WeChatMsgType, Processor> processorMap = new HashMap<>();

    public ProcessDispatcher() {
        processorMap.put(text, new TextMessageProcessor());
        processorMap.put(voice, new VoiceMessageProcessor());
        processorMap.put(event, new EventMessageProcessor());
    }

    public RetData dispatch(Map<String, String> msg) {
        WeChatMsgType type = WeChatMsgType.valueOf(msg.get(ELEMENT_MSG_TYPE));
        Processor processor = processorMap.get(type);
        if (processor == null) {
            return new RetData(RET_MSG_SUCCESS, true);
        } else {
            return processor.process(msg);
        }
    }
}

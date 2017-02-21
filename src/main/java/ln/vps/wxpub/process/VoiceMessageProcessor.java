package ln.vps.wxpub.process;

import ln.vps.wxpub.common.WeChatMsgType;
import ln.vps.wxpub.util.IntTimeStamp;

import java.util.Map;

import static ln.vps.wxpub.common.WeChatPlatformPrivateInfo.ORIGIN_ID;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_FROM_USER_NAME;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_RECOGNITION;

/**
 * Created by Helly on 2017/02/15.
 */
public class VoiceMessageProcessor implements Processor {
    private static final String FORMAT = "<xml>" +
            "<ToUserName><![CDATA[%s]]></ToUserName>" +
            "<FromUserName><![CDATA[%s]]></FromUserName>" +
            "<CreateTime>%d</CreateTime>" +
            "<MsgType><![CDATA[%s]]></MsgType>" +
            "<Content><![CDATA[%s]]></Content>" +
            "</xml>";

    public VoiceMessageProcessor() {
    }

    @Override
    public RetData process(Map<String, String> msg) {
        String fromUserName = msg.get(ELEMENT_FROM_USER_NAME);
        String recongnition = msg.get(ELEMENT_RECOGNITION);
        //发送者即回复时的接受者
        return new RetData(outputXml(fromUserName, recongnition), true);
    }

    private String outputXml(String toUserName, String content) {
        return String.format(FORMAT, toUserName, ORIGIN_ID, IntTimeStamp.timestamp(), WeChatMsgType.text.name(), content);
    }
}

package ln.vps.wxpub.process;

import ln.vps.wxpub.common.WeChatEventType;
import ln.vps.wxpub.common.WeChatMsgType;
import ln.vps.wxpub.util.IntTimeStamp;

import java.util.Map;
import java.util.Random;

import static ln.vps.wxpub.common.WeChatPlatformPrivateInfo.ORIGIN_ID;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_EVENT;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_FROM_USER_NAME;

/**
 * Created by Helly on 2017/02/15.
 */
public class EventMessageProcessor implements Processor {
    private static final String FORMAT = "<xml>" +
            "<ToUserName><![CDATA[%s]]></ToUserName>" +
            "<FromUserName><![CDATA[%s]]></FromUserName>" +
            "<CreateTime>%d</CreateTime>" +
            "<MsgType><![CDATA[%s]]></MsgType>" +
            "<Content><![CDATA[%s]]></Content>" +
            "</xml>";
    private static final Random RANDOM = new Random(System.nanoTime());
    private static final String[] STRINGS = {"己所不欲\n勿施于人",
            "天生我材必有用\n千金散尽还复来",
            "不识庐山真面目\n只缘身在此山中",
            "勿以善小而不为\n勿以恶小而为之",
            "路漫漫其修远兮\n吾将上下而求索",
            "长风破浪会有时\n直挂云帆济沧海",
            "不要人夸颜色好\n只留清气满乾坤",
            "时人莫小池中水\n浅处无妨有卧龙"
    };

    @Override
    public RetData process(Map<String, String> msg) {
        if (WeChatEventType.subscribe.name().equals(msg.get(ELEMENT_EVENT))) {
            String toUserName = msg.get(ELEMENT_FROM_USER_NAME);
            String content = "欢迎关注“悠云闲语”\n这里是我个人插科打诨的地方，有吐槽，有随笔，也有些个人的分享\n不一定有价值，您看着办\n\n"
                    + STRINGS[RANDOM.nextInt(STRINGS.length)];
            return new RetData(outputXml(toUserName, content), false);
        } else {
            return new RetData(RET_MSG_SUCCESS, false);
        }
    }

    private String outputXml(String toUserName, String content) {
        return String.format(FORMAT, toUserName, ORIGIN_ID, IntTimeStamp.timestamp(), WeChatMsgType.text.name(), content);
    }
}

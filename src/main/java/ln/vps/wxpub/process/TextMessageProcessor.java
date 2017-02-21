package ln.vps.wxpub.process;

import ln.vps.wxpub.command.CommandDispatcher;
import ln.vps.wxpub.common.WeChatMsgType;
import ln.vps.wxpub.util.IntTimeStamp;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ln.vps.wxpub.common.WeChatPlatformPrivateInfo.ORIGIN_ID;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_CONTENT;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_FROM_USER_NAME;

/**
 * Created by Helly on 2017/02/15.
 */
public class TextMessageProcessor implements Processor {
    private static final String FORMAT = "<xml>" +
            "<ToUserName><![CDATA[%s]]></ToUserName>" +
            "<FromUserName><![CDATA[%s]]></FromUserName>" +
            "<CreateTime>%d</CreateTime>" +
            "<MsgType><![CDATA[%s]]></MsgType>" +
            "<Content><![CDATA[%s]]></Content>" +
            "</xml>";
    private static final String REGEX_CMD = "^\\$([a-z]+)(?:(?:)|(?:#((?:[^,]+,+)*[^,]+)))$";
    public static final String RET_MSG_RECEIVED = "您发送的消息已经收到。\n如有必要，将稍后回复您。";
    public static final int PART_TWO = 2;
    public static final int GROUP_1 = 1;
    public static final int GROUP_2 = 2;

    private Pattern pattern = Pattern.compile(REGEX_CMD);
    private CommandDispatcher commandDispatcher = new CommandDispatcher();

    public TextMessageProcessor() {
    }

    @Override
    public RetData process(Map<String, String> msg) {
        String fromUserName = msg.get(ELEMENT_FROM_USER_NAME);
        String receiveContent = msg.get(ELEMENT_CONTENT);
        boolean sendMail;
        String content;
        Matcher matcher = pattern.matcher(receiveContent);
        if (matcher.matches()) {
            sendMail = false;
            int group = matcher.groupCount();
            String cmd;
            String[] params;
            if (group == PART_TWO) {
                cmd = matcher.group(GROUP_1);
                String param = matcher.group(GROUP_2);
                if (param == null) {
                    params = new String[0];
                } else {
                    params = param.split(",");
                }
                content = parseAndExecute(fromUserName, cmd, params);
            } else {
                content = RET_MSG_RECEIVED;
            }
        } else {
            sendMail = true;
            content = RET_MSG_RECEIVED;
        }
        //发送者即回复时的接受者
        return new RetData(outputXml(fromUserName, content), sendMail);
    }

    private String parseAndExecute(String fromUserName, String cmd, String[] params) {
        //fromUserName是预留，打算某些命令只给自己使用
        String retMsg = commandDispatcher.dispatch(fromUserName, cmd, params);
        if (retMsg == null) {
            return RET_MSG_RECEIVED;
        } else {
            return retMsg;
        }
    }

    private String outputXml(String toUserName, String content) {
        return String.format(FORMAT, toUserName, ORIGIN_ID, IntTimeStamp.timestamp(), WeChatMsgType.text.name(), content);
    }
}

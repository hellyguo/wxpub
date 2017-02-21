package ln.vps.wxpub.process;

import ln.vps.wxpub.common.WeChatPlatformPrivateInfo;
import ln.vps.wxpub.common.WxPubException;
import ln.vps.wxpub.util.MessageXMLParse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TextMessageProcessorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextMessageProcessorTest.class);

    private Processor processor = new TextMessageProcessor();

    @Test
    public void test() throws Exception {
        LOGGER.info("---------------test help");
        testProcess_CMD_help();
        LOGGER.info("---------------test javadoc");
        testProcess_CMD_javadoc();
        LOGGER.info("---------------test linuxman");
        testProcess_CMD_linuxman();
        LOGGER.info("---------------test unknown");
        testProcess_CMD_unknown();
    }

    public void testProcess_CMD_help() throws Exception {
        String xml = "<xml><ToUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.ORIGIN_ID +
                "]]></ToUserName>\n" +
                "<FromUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.SELF_FOLLOWER_ID +
                "]]></FromUserName>\n" +
                "<CreateTime>1487056668</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[$help]]></Content>\n" +
                "<MsgId>6386859756773323358</MsgId>\n" +
                "</xml>";
        test(xml);
    }

    public void testProcess_CMD_javadoc() throws Exception {
        String xml = "<xml><ToUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.ORIGIN_ID +
                "]]></ToUserName>\n" +
                "<FromUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.SELF_FOLLOWER_ID +
                "]]></FromUserName>\n" +
                "<CreateTime>1487056668</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[$javadoc#String]]></Content>\n" +
                "<MsgId>6386859756773323358</MsgId>\n" +
                "</xml>";
        test(xml);
    }

    public void testProcess_CMD_linuxman() throws Exception {
        String xml = "<xml><ToUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.ORIGIN_ID +
                "]]></ToUserName>\n" +
                "<FromUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.SELF_FOLLOWER_ID +
                "]]></FromUserName>\n" +
                "<CreateTime>1487056668</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[$linuxman#gcc]]></Content>\n" +
                "<MsgId>6386859756773323358</MsgId>\n" +
                "</xml>";
        test(xml);
    }

    public void testProcess_CMD_unknown() throws Exception {
        String xml = "<xml><ToUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.ORIGIN_ID +
                "]]></ToUserName>\n" +
                "<FromUserName><![CDATA[" +
                WeChatPlatformPrivateInfo.SELF_FOLLOWER_ID +
                "]]></FromUserName>\n" +
                "<CreateTime>1487056668</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[$xxxx]]></Content>\n" +
                "<MsgId>6386859756773323358</MsgId>\n" +
                "</xml>";
        test(xml);
    }

    private void test(String xml) throws WxPubException {
        Map<String, String> msg = MessageXMLParse.parse(xml);
        RetData data = processor.process(msg);
        LOGGER.info("{}|{}", data.getRetMsg(), data.isSendMail());
    }
}
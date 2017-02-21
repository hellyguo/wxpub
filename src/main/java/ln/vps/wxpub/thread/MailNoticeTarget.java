package ln.vps.wxpub.thread;

import ln.vps.wxpub.util.DoubleBufferArrayList;
import ln.vps.wxpub.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static ln.vps.wxpub.common.WeChatPlatformPrivateInfo.SELF_FOLLOWER_ID;
import static ln.vps.wxpub.common.WeChatXmlElementName.ELEMENT_FROM_USER_NAME;

/**
 * Created by Helly on 2017/02/12.
 */
public class MailNoticeTarget implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailNoticeTarget.class);

    private static final String TARGET_ADDRESS = "135xxxxxxxx@139.com";

    private AtomicBoolean enable = new AtomicBoolean(true);
    private AtomicBoolean quit = new AtomicBoolean(false);
    private DoubleBufferArrayList<String> dbList = new DoubleBufferArrayList<>();
    private MailUtil util = MailUtil.getInstance();

    @Override
    public void run() {
        while (enable.get()) {
            List<String> xmlStrings = dbList.getData(quit);
            if (!xmlStrings.isEmpty()) {
                LOGGER.info("need to send [{}] emails to [{}]", xmlStrings.size(), TARGET_ADDRESS);
                xmlStrings.forEach(xml -> {
                    util.mail("receive xml", xml, TARGET_ADDRESS);
                    LOGGER.info("send an email to [{}]", TARGET_ADDRESS);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        //
                    }
                });
            }
        }
    }

    public void noticeWeChatPost(Map<String, String> msg) {
        if (SELF_FOLLOWER_ID.equals(msg.get(ELEMENT_FROM_USER_NAME))) {
            LOGGER.info("sent by self. just record data done[{}]", msg);
        } else {
            dbList.addData(msg.toString());
            LOGGER.info("add data done[{}]", msg);
        }
    }

    public void shutdown() {
        quit.set(true);
        enable.set(false);
    }
}

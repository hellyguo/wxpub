package ln.vps.wxpub.web;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import ln.vps.wxpub.process.ProcessDispatcher;
import ln.vps.wxpub.process.RetData;
import ln.vps.wxpub.thread.MailNoticeTarget;
import ln.vps.wxpub.util.LruMap;
import ln.vps.wxpub.util.MessageXMLParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static ln.vps.wxpub.common.WeChatHttpParamName.*;
import static ln.vps.wxpub.common.WeChatPlatformPrivateInfo.*;
import static ln.vps.wxpub.common.WeChatXmlElementName.*;
import static ln.vps.wxpub.process.Processor.RET_MSG_EMPTY;

/**
 * Created by Helly on 2017/02/10.
 */
@WebServlet(name = "wxpub.shtml", urlPatterns = {"/wxpub.shtml"}, loadOnStartup = 1)
public class WeChatPublicResponseServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatPublicResponseServlet.class);

    private static final int MAX_SIZE = 2000;

    private static final String EMPTY_STR = "";

    private static LruMap<String, String> POST_KEY_MAP = new LruMap<>(MAX_SIZE, LruMap.KeepOrder.INSERT_ORDER);

    private WXBizMsgCrypt crypt;
    private MailNoticeTarget target = new MailNoticeTarget();
    private ProcessDispatcher dispatcher = new ProcessDispatcher();

    @Override
    public void init() throws ServletException {
        super.init();
        LOGGER.info("servlet start");
        try {
            crypt = new WXBizMsgCrypt(TOKEN, ENCODING_AES_KEY, APP_ID);
        } catch (AesException e) {
            throw new RuntimeException(e);
        }
        Thread thread = new Thread(target, "Mail Notice Thread");
        thread.setDaemon(true);
        thread.start();
        LOGGER.info("thread started");
        LOGGER.info("servlet started");
    }

    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("servlet shutdown");
        target.shutdown();
        LOGGER.info("servlet downed");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String signature = req.getParameter(PARAM_SIGNATURE);
        String timestamp = req.getParameter(PARAM_TIMESTAMP);
        String nonce = req.getParameter(PARAM_NONCE);
        String echoStr = req.getParameter(PARAM_ECHO_STR);
        if (signature == null || timestamp == null || nonce == null || echoStr == null) {
            handleFailAndReturn(resp, null);
            return;
        }
        try {
            crypt.verifyUrl(signature, timestamp, nonce, echoStr);
        } catch (AesException e) {
            handleFailAndReturn(resp, e);
        }
        returnToClient(resp, echoStr);
    }

    private void returnToClient(HttpServletResponse resp, String data) throws IOException {
        LOGGER.info("return to WeChat:[{}]", data);
        resp.getWriter().write(data);
        resp.getWriter().flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String signature = req.getParameter(PARAM_SIGNATURE);
        String msgSignature = req.getParameter(PARAM_MSG_SIGNATURE);
        String timestamp = req.getParameter(PARAM_TIMESTAMP);
        String nonce = req.getParameter(PARAM_NONCE);
        String xml = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (timestamp == null || nonce == null || xml == null) {
            handleFailAndReturn(resp, null);
            return;
        }
        LOGGER.info("msgSignature:[{}] timestamp:[{}] nonce:[{}]", msgSignature, timestamp, nonce);
        LOGGER.info("xml is->[{}]", xml);
        try {
            String decodedXml = crypt.decryptMsg(msgSignature, timestamp, nonce, xml);
            LOGGER.info("decoded xml is->[{}]", decodedXml);
            Map<String, String> map = MessageXMLParse.parse(decodedXml);
            boolean isNew = repeatCheck(map);
            if (isNew) {
                String encodedRetMsg = noticeAndProcess(signature, timestamp, nonce, map);
                returnToClient(resp, encodedRetMsg);
            } else {
                LOGGER.info("repeat post data:[{}]", map);
                handleFailAndReturn(resp, null);
            }
        } catch (Exception e) {
            handleFailAndReturn(resp, e);
        }
    }

    private void handleFailAndReturn(HttpServletResponse resp, Exception e) throws IOException {
        if (e != null) {
            LOGGER.warn(e.getMessage(), e);
        }
        returnToClient(resp, RET_MSG_EMPTY);
    }

    private String noticeAndProcess(String signature, String timestamp, String nonce, Map<String, String> map) {
        return process(signature, timestamp, nonce, map);
    }

    private void notice(Map<String, String> map) {
        target.noticeWeChatPost(map);
    }

    private String process(String signature, String timestamp, String nonce, Map<String, String> map) {
        String retMsg = processMsg(map);
        String encodedRetMsg;
        try {
            if (RET_MSG_EMPTY.equals(retMsg)) {
                encodedRetMsg = retMsg;
            } else {
                encodedRetMsg = crypt.encryptMsg(retMsg, timestamp, nonce);
            }
        } catch (AesException e) {
            LOGGER.warn(e.getMessage(), e);
            encodedRetMsg = RET_MSG_EMPTY;
        }
        return encodedRetMsg;
    }

    private String processMsg(Map<String, String> map) {
        RetData retData = dispatcher.dispatch(map);
        if (retData.isSendMail()) {
            notice(map);
        }
        return retData.getRetMsg();
    }

    private boolean repeatCheck(Map<String, String> map) {
        String repeatCheckKey;
        if (map.containsKey(ELEMENT_EVENT)) {
            repeatCheckKey = repeatEventCheckKey(map);
        } else {
            repeatCheckKey = repeatMsgCheckKey(map);
        }
        boolean isNew = !POST_KEY_MAP.containsKey(repeatCheckKey);
        if (isNew) {
            POST_KEY_MAP.put(repeatCheckKey, EMPTY_STR);
        }
        return isNew;
    }

    private String repeatEventCheckKey(Map<String, String> map) {
        return map.get(ELEMENT_FROM_USER_NAME) + map.get(ELEMENT_CREATE_TIME);
    }

    private String repeatMsgCheckKey(Map<String, String> map) {
        return map.get(ELEMENT_MSG_ID);
    }

}

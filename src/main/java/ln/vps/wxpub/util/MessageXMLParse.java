package ln.vps.wxpub.util;

import ln.vps.wxpub.common.WeChatEventType;
import ln.vps.wxpub.common.WeChatMsgType;
import ln.vps.wxpub.common.WxPubException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static ln.vps.wxpub.common.WeChatMsgType.event;
import static ln.vps.wxpub.common.WeChatXmlElementName.*;

/**
 * XMLParse class
 */
public final class MessageXMLParse {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageXMLParse.class);

    /**
     * 提取出xml数据包中的加密消息
     *
     * @param xmltext 待提取的xml字符串
     * @return 提取出的加密消息字符串
     */
    public static Map<String, String> parse(String xmltext) throws WxPubException {
        try {
            Map<String, String> msg = new HashMap<>();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xmltext);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList toUserNameNode = root.getElementsByTagName(ELEMENT_TO_USER_NAME);
            NodeList fromUserNameNode = root.getElementsByTagName(ELEMENT_FROM_USER_NAME);
            NodeList createTimeNode = root.getElementsByTagName(ELEMENT_CREATE_TIME);
            NodeList msgTypeNode = root.getElementsByTagName(ELEMENT_MSG_TYPE);

            String msgType = msgTypeNode.item(0).getTextContent();

            msg.put(ELEMENT_TO_USER_NAME, toUserNameNode.item(0).getTextContent());
            msg.put(ELEMENT_FROM_USER_NAME, fromUserNameNode.item(0).getTextContent());
            msg.put(ELEMENT_CREATE_TIME, createTimeNode.item(0).getTextContent());
            msg.put(ELEMENT_MSG_TYPE, msgType);

            parseByType(msgType, root, msg);

            return msg;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            throw new WxPubException(e);
        }
    }

    private static void parseByType(String msgType, Element root, Map<String, String> msg) {
        WeChatMsgType type = WeChatMsgType.valueOf(msgType);
        if (event.equals(type)) {
            NodeList eventNode = root.getElementsByTagName(ELEMENT_EVENT);
            String eventType = eventNode.item(0).getTextContent();
            msg.put(ELEMENT_EVENT, eventType);
            parseByEvent(eventType, root, msg);
        } else {
            NodeList msgIdNode = root.getElementsByTagName(ELEMENT_MSG_ID);
            msg.put(ELEMENT_MSG_ID, msgIdNode.item(0).getTextContent());
            parseMsg(root, msg, type);
        }
    }

    private static void parseMsg(Element root, Map<String, String> msg, WeChatMsgType type) {
        switch (type) {
            case text: {
                NodeList contentNode = root.getElementsByTagName(ELEMENT_CONTENT);
                msg.put(ELEMENT_CONTENT, contentNode.item(0).getTextContent());
                break;
            }
            case image: {
                putMediaId(root, msg);
                NodeList recognitionNode = root.getElementsByTagName(ELEMENT_PIC_URL);
                msg.put(ELEMENT_PIC_URL, recognitionNode.item(0).getTextContent());
                break;
            }
            case voice: {
                putMediaId(root, msg);
                NodeList recognitionNode = root.getElementsByTagName(ELEMENT_RECOGNITION);
                msg.put(ELEMENT_RECOGNITION, recognitionNode.item(0).getTextContent());
                break;
            }
            case video:
            case shortvideo: {
                putMediaId(root, msg);
                NodeList thumbMediaIdNode = root.getElementsByTagName(ELEMENT_THUMB_MEDIA_ID);
                msg.put(ELEMENT_THUMB_MEDIA_ID, thumbMediaIdNode.item(0).getTextContent());
                break;
            }
            case location: {
                NodeList locationXNode = root.getElementsByTagName(ELEMENT_LOCATION_X);
                NodeList locationYNode = root.getElementsByTagName(ELEMENT_LOCATION_Y);
                NodeList scalNode = root.getElementsByTagName(ELEMENT_SCALE);
                NodeList labelNode = root.getElementsByTagName(ELEMENT_LABEL);
                msg.put(ELEMENT_LOCATION_X, locationXNode.item(0).getTextContent());
                msg.put(ELEMENT_LOCATION_Y, locationYNode.item(0).getTextContent());
                msg.put(ELEMENT_SCALE, scalNode.item(0).getTextContent());
                msg.put(ELEMENT_LABEL, labelNode.item(0).getTextContent());
                break;
            }
            case link: {
                NodeList titleNode = root.getElementsByTagName(ELEMENT_TITLE);
                NodeList descriptionNode = root.getElementsByTagName(ELEMENT_DESCRIPTION);
                NodeList urlNode = root.getElementsByTagName(ELEMENT_URL);
                msg.put(ELEMENT_TITLE, titleNode.item(0).getTextContent());
                msg.put(ELEMENT_DESCRIPTION, descriptionNode.item(0).getTextContent());
                msg.put(ELEMENT_URL, urlNode.item(0).getTextContent());
                break;
            }
            default:
                break;
        }
    }

    private static void putMediaId(Element root, Map<String, String> msg) {
        NodeList mediaIdNode = root.getElementsByTagName(ELEMENT_MEDIA_ID);
        msg.put(ELEMENT_MEDIA_ID, mediaIdNode.item(0).getTextContent());
    }

    private static void parseByEvent(String eventType, Element root, Map<String, String> msg) {
        WeChatEventType type = WeChatEventType.valueOf(eventType);
        switch (type) {
            case subscribe: {
                break;
            }
            case unsubscribe: {
                break;
            }
            case SCAN: {
                break;
            }
            case LOCATION: {
                break;
            }
            case CLICK: {
                break;
            }
            case VIEW: {
                break;
            }
            default:
                break;
        }
    }

    private MessageXMLParse() {
    }
}

package com.dashiji.biyun.xmpp;

import com.dashiji.biyun.model.MUCInfo;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/1/9.
 */

public class MUCPacketExtensionProvider extends IQProvider {

    private List<MUCInfo> myRooms = new ArrayList<>();

    public List<MUCInfo> getMyRoom() {
        return myRooms;
    }

    @Override
    public Element parse(XmlPullParser parser, int initialDepth) throws Exception {
        int eventType = parser.getEventType();
        MUCInfo info = null;
        while (true) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("room".equals(parser.getName())) {
                    String account = parser.getAttributeValue("", "account");
                    String room = parser.nextText();

                    info = new MUCInfo();
                    info.setAccount(account);
                    info.setRoom(room);
                    info.setNickname(account);

                    myRooms.add(info);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("muc".equals(parser.getName())) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return null;
    }
}

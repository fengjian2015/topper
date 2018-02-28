package com.bclould.tocotalk.model;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;

/**
 * Created by GA on 2018/2/28.
 */

public class VoiceInfo extends DataPacketExtension {
    //用户信息元素名称
    public static final String ELEMENT_NAME = "attachment";
    public static final String NAME_SPACE = " xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'";
    private String elementText = "";

    /**
     * Creates a new In-Band Bytestream data packet.
     *
     * @param sessionID unique session ID identifying this In-Band Bytestream
     * @param seq       sequence of this stanza(/packet) in regard to the other data packets
     * @param data      the base64 encoded data contained in this packet
     */
    public VoiceInfo(String sessionID, long seq, String data) {
        super(sessionID, seq, data);
    }


    public String getElementText() {
        return elementText;
    }

    public void setElementText(String elementText) {
        this.elementText = elementText;
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder sb = new XmlStringBuilder();
        sb.append("<");
        sb.append(ELEMENT_NAME);
        sb.append(NAME_SPACE);
        sb.append(">");
        sb.append(elementText);
        sb.append("</");
        sb.append(ELEMENT_NAME);
        sb.append(">");
        return sb;
    }
}

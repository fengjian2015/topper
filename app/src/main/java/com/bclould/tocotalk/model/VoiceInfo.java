package com.bclould.tocotalk.model;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Created by GA on 2018/2/28.
 */

public class VoiceInfo implements ExtensionElement {
    //用户信息元素名称
    public static final String NAME_SPACE = " xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'";
    public static final String ELEMENT_NAME = "attachment";
    private String elementText = "";


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

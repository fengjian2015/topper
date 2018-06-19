package com.bclould.tea.model;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by GA on 2017/12/27.
 */

public class RedPacketInfo implements ExtensionElement {
    public static final String NAME_SPACE = "com.xml.extension";
    //用户信息元素名称
    public static final String ELEMENT_NAME = "redpacketinfo";

    //用户昵称元素名称
    private String redPacketElement = "redpacke";
    //用户昵称元素文本(对外开放)
    private int redPacketType = 0;

    //用户头像地址元素名称
    private String countElement = "count";
    //用户头像地址元素文本(对外开放)
    private String count = "";
    //用户头像地址元素名称
    private String coinElement = "coin";
    //用户头像地址元素文本(对外开放)
    private String coin = "";

    public int getRedPacketType() {
        return redPacketType;
    }

    public void setRedPacketType(int redPacketType) {
        this.redPacketType = redPacketType;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
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
    public CharSequence toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(ELEMENT_NAME).append(" xmlns=\"").append(NAME_SPACE).append("\">");
        sb.append("<" + redPacketElement + ">").append(redPacketType).append("</" + redPacketElement + ">");
        sb.append("<" + countElement + ">").append(count).append("</" + countElement + ">");
        sb.append("<" + coinElement + ">").append(coin).append("</" + coinElement + ">");
        sb.append("</" + ELEMENT_NAME + ">");

        return sb.toString();
    }
}

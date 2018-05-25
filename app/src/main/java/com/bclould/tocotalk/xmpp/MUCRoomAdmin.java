package com.bclould.tocotalk.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.muc.packet.MUCItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by GIjia on 2018/5/24.
 */

public class MUCRoomAdmin extends IQ {

    public static final String ELEMENT = QUERY_ELEMENT;
    public static final String NAMESPACE = MUCInitialPresence.NAMESPACE + "#admin";

    private final List<MUCRoomItem> items = new ArrayList<MUCRoomItem>();

    public MUCRoomAdmin() {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * Returns a List of item childs that holds information about roles, affiliation,
     * jids and nicks.
     *
     * @return a List of item childs that holds information about roles, affiliation,
     *          jids and nicks.
     */
    public List<MUCRoomItem> getItems() {
        synchronized (items) {
            return Collections.unmodifiableList(new ArrayList<MUCRoomItem>(items));
        }
    }

    /**
     * Adds an item child that holds information about roles, affiliation, jids and nicks.
     *
     * @param item the item child that holds information about roles, affiliation, jids and nicks.
     */
    public void addItem(MUCRoomItem item) {
        synchronized (items) {
            items.add(item);
        }
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();

        synchronized (items) {
            for (MUCRoomItem item : items) {
                xml.append(item.toXML());
            }
        }

        return xml;
    }
}

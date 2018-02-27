package com.bclould.tocotalk.Presenter;

import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.Collection;

/**
 * Created by GA on 2017/12/8.
 */

public class CloudMessagePresenter {

    public Collection<RosterGroup> getContact() {
        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
        Collection<RosterGroup> groups = roster.getGroups();
        return groups;
    }
}

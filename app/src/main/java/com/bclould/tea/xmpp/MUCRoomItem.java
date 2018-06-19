package com.bclould.tea.xmpp;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

/**
 * Created by GIjia on 2018/5/24.
 */

public class MUCRoomItem implements NamedElement {
    public static final String ELEMENT = Stanza.ITEM;

    private final MUCAffiliation affiliation;
    private final MUCRole role;
    private final Jid actor;
    private final Resourcepart actorNick;
    private final String reason;
    private final Jid jid;
    private final Resourcepart nick;
    private final String roomName;

    public MUCRoomItem(MUCAffiliation affiliation) {
        this(affiliation, null, null, null, null, null, null,null);
    }

    public MUCRoomItem(MUCRole role) {
        this(null, role, null, null, null, null, null,null);
    }

    public MUCRoomItem(MUCRole role, Resourcepart nick) {
        this(null, role, null, null, null, nick, null,null);
    }

    public MUCRoomItem(MUCAffiliation affiliation, Jid jid, String reason,String roomName) {
        this(affiliation, null, null, reason, jid, null, null,roomName);
    }

    public MUCRoomItem(MUCAffiliation affiliation, Jid jid) {
        this(affiliation, null, null, null, jid, null, null,null);
    }

    public MUCRoomItem(MUCRole role, Resourcepart nick, String reason) {
        this(null, role, null, reason, null, nick, null,null);
    }

    /**
     * Creates a new item child.
     *
     * @param affiliation the actor's affiliation to the room
     * @param role the privilege level of an occupant within a room.
     * @param actor
     * @param reason
     * @param jid
     * @param nick
     * @param actorNick
     */
    public MUCRoomItem(MUCAffiliation affiliation, MUCRole role, Jid actor,
                   String reason, Jid jid, Resourcepart nick, Resourcepart actorNick,String roomName) {
        this.affiliation = affiliation;
        this.role = role;
        this.actor = actor;
        this.reason = reason;
        this.jid = jid;
        this.nick = nick;
        this.actorNick = actorNick;
        this.roomName=roomName;
    }

    /**
     * Returns the actor (JID of an occupant in the room) that was kicked or banned.
     *
     * @return the JID of an occupant in the room that was kicked or banned.
     */
    public Jid getActor() {
        return actor;
    }

    /**
     * Get the nickname of the actor.
     *
     * @return the nickname of the actor.
     * @since 4.2
     */
    public Resourcepart getActorNick() {
        return actorNick;
    }

    /**
     * Returns the reason for the item child. The reason is optional and could be used to explain
     * the reason why a user (occupant) was kicked or banned.
     *
     * @return the reason for the item child.
     */
    public String getReason() {
        return reason;
    }


    public String getRoomName(){
        return roomName;
    }

    /**
     * Returns the occupant's affiliation to the room. The affiliation is a semi-permanent
     * association or connection with a room. The possible affiliations are "owner", "admin",
     * "member", and "outcast" (naturally it is also possible to have no affiliation). An
     * affiliation lasts across a user's visits to a room.
     *
     * @return the actor's affiliation to the room
     */
    public MUCAffiliation getAffiliation() {
        return affiliation;
    }

    /**
     * Returns the <room@service/nick> by which an occupant is identified within the context of a
     * room. If the room is non-anonymous, the JID will be included in the item.
     *
     * @return the room JID by which an occupant is identified within the room.
     */
    public Jid getJid() {
        return jid;
    }

    /**
     * Returns the new nickname of an occupant that is changing his/her nickname. The new nickname
     * is sent as part of the unavailable presence.
     *
     * @return the new nickname of an occupant that is changing his/her nickname.
     */
    public Resourcepart getNick() {
        return nick;
    }

    /**
     * Returns the temporary position or privilege level of an occupant within a room. The possible
     * roles are "moderator", "participant", "visitor" and "none" (it is also possible to have no defined
     * role). A role lasts only for the duration of an occupant's visit to a room.
     *
     * @return the privilege level of an occupant within a room.
     */
    public MUCRole getRole() {
        return role;
    }


    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.optAttribute("affiliation", getAffiliation());
        xml.optAttribute("jid", getJid());
        xml.optAttribute("nick", getNick());
        xml.optAttribute("role", getRole());
        xml.rightAngleBracket();
        xml.optElement("reason", getReason());
        xml.optElement("roomName", getRoomName());
        if (getActor() != null) {
            xml.halfOpenElement("actor").attribute("jid", getActor()).closeEmptyElement();
        }
        xml.closeElement(Stanza.ITEM);
        return xml;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }
}

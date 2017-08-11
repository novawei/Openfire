package com.nfs.mucextend;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.CannotBeInvitedException;
import org.jivesoftware.openfire.muc.ConflictException;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.MUCEventListener;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.NotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class MUCExtendEventListener implements MUCEventListener {
	
	private static final Logger Log = LoggerFactory.getLogger(MUCExtendEventListener.class);

	@Override
	public void roomCreated(JID roomJID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void roomDestroyed(JID roomJID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void occupantJoined(JID roomJID, JID user, String nickname) {
		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		if (mucroom == null) {
			return;
		}
		long roomID = mucroom.getID();
		String jid = user.toBareJID();
		// 用户即可能存在ofmucaffiliation表，也可能存在ofmucmember表
		if (MUCExtendDao.isAffiliationExist(roomID, jid) || MUCExtendDao.isMemberExist(roomID, jid)) {
			return;
		}
		
		Log.info(roomJID.toString() + " " + user.toString() + " " + nickname);
		
		IQ iq = new IQ(IQ.Type.set); 
		Element frag = iq.setChildElement("query", "http://jabber.org/protocol/muc#admin");
        Element item = frag.addElement("item");
        item.addAttribute("affiliation", "member");
        item.addAttribute("jid", jid);
		item.addAttribute("nick", nickname);
        // Send the IQ packet that will modify the room's configuration
		try {
			mucroom.getIQAdminHandler().handleIQ(iq, mucroom.getRole());
		} catch (ForbiddenException 
				| ConflictException 
				| NotAllowedException
				| CannotBeInvitedException e) {
			Log.error(e.getMessage());
		}
	}

	@Override
	public void occupantLeft(JID roomJID, JID user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nicknameChanged(JID roomJID, JID user, String oldNickname,
			String newNickname) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(JID roomJID, JID user, String nickname,
			Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void privateMessageRecieved(JID toJID, JID fromJID, Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void roomSubjectChanged(JID roomJID, JID user, String newSubject) {
		// TODO Auto-generated method stub

	}

}

package com.nfs.mucextend;

import java.util.Collection;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.MUCEventListener;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class MUCExtendEventListener implements MUCEventListener {

	@Override
	public void roomCreated(JID roomJID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void roomDestroyed(JID roomJID) {
		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		if (mucroom == null) {
			return;
		}
		long roomID = mucroom.getID();
		MUCDao.deleteRoomFromDB(roomID);
	}

	@Override
	public void occupantJoined(JID roomJID, JID user, String nickname) {
		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		if (mucroom == null) {
			return;
		}
		long roomID = mucroom.getID();
		String jid = user.toBareJID();
		if (MUCDao.getUser(roomID, jid) != null) {
			return;
		}
		
		MUCDao.saveUserToDB(roomID, jid, nickname);
		// TODO 通知组内成员，成员列表变动
	}

	@Override
	public void occupantLeft(JID roomJID, JID user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nicknameChanged(JID roomJID, JID user, String oldNickname,
			String newNickname) {
		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		if (mucroom == null) {
			return;
		}
		long roomID = mucroom.getID();
		String jid = user.toBareJID();
		
		MUCDao.updateUserToDB(roomID, jid, newNickname);
		// TODO 通知组内成员，成员昵称变动
	}

	@Override
	public void messageReceived(JID roomJID, JID user, String nickname,
			Message message) {
		if (message.getBody() == null) {
			return;
		}
		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJID).getChatRoom(roomJID.getNode());
		if (mucroom == null) {
			return;
		}
		long roomID = mucroom.getID();
		Collection<MUCRole> occupants = mucroom.getOccupants();
		for (MUCRole role : occupants) {
			role.getUserAddress().toBareJID();
		}
		// 获取所有的用户，然后给群组成员发送消息
		List<UserEntity> userList = MUCDao.getUserList(roomID);
		/**
		 * <message to="novawei@10.50.200.45/Spark" id="iJ0Vm-713" type="groupchat" from="test1@conference.10.50.200.45/novawei">
		 * <body>22</body>
		 * <x xmlns="jabber:x:event"><offline/><delivered/><displayed/><composing/></x>
		 * </message> 
		 * 
		 *<message to='multicast.jabber.org'>
   		 *	<addresses xmlns='http://jabber.org/protocol/address'>
       	 *		<address type='to' jid='hildjj@jabber.org/Work' desc='Joe Hildebrand'/>
       	 *		<address type='cc' jid='jer@jabber.org/Home' desc='Jeremie Miller'/>
   		 *	</addresses>
   		 *	<body>Hello, world!</body>
		 *</message>
		 */
		Message messageCopy = message.createCopy();
		Element element = messageCopy.getElement();
		element.remove(element.attribute("to"));
		element.remove(element.element("x"));

		/**
		 * MulticastRouter和MessageRouter，通过判断条件屏蔽了groupchat类型的广播 
		 * Element addressesElement = element.addElement("addresses"); 
		 * Element addressElement = null;
		 * for (UserEntity entity : userList) {
		 *     addressElement = addressesElement.addElement("address");
		 *     addressElement.addAttribute("type", "to");
		 *     addressElement.addAttribute("jid", entity.getJid()); 
		 * }
		 * 
		 * MulticastRouter router = XMPPServer.getInstance().getMulticastRouter();
		 * router.route(messageCopy);
		 */
		
		SessionManager sessionManager = XMPPServer.getInstance().getSessionManager();
		RoutingTable routingTable = XMPPServer.getInstance().getRoutingTable();
		JID entityJID = null;
		Message packet = null;
		for (UserEntity entity : userList) {
			entityJID = new JID(entity.getJid());
			if (!canBroadcast(entityJID, user, mucroom)) {
				continue;
			}
			List<JID> routes = routingTable.getRoutes(entityJID, null);
			for (JID route : routes) {
				ClientSession clientSession = sessionManager.getSession(route);
				if (clientSession != null && clientSession.getStatus() == ClientSession.STATUS_AUTHENTICATED) {
					packet = messageCopy.createCopy();
					packet.setTo(route);
					clientSession.process(packet);
				}
			}
		}
	}

	@Override
	public void privateMessageRecieved(JID toJID, JID fromJID, Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void roomSubjectChanged(JID roomJID, JID user, String newSubject) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 加入房间的用户,mucroom会自动发送消息到该用户
	 * 只发送给非本人且不在房间的用户
	 * @param toJID
	 * @param fromJID
	 * @param room
	 * @return
	 */
	private boolean canBroadcast(JID toJID, JID fromJID, MUCRoom room) {
		if (toJID.compareTo(fromJID.asBareJID()) == 0) {
			return false;
		}
		boolean joined = false;
		try {
			List<MUCRole> roles = room.getOccupantsByBareJID(toJID);
			joined = roles.size() > 0;
		}
		catch (UserNotFoundException e) {
			
		}
		return joined == false;
	}

}

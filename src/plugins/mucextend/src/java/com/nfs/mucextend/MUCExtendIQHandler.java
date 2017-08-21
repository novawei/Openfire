package com.nfs.mucextend;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MUCRole;
import org.xmpp.packet.IQ;

public class MUCExtendIQHandler extends IQHandler {
	private static final String MODULE_NAME = "muc extend";  
	private static final String NAME_SPACE = "com:nfs:mucextend";
	
	// 用户加入的房间列表
	private static final String CMD_USER_ROOM_LIST = "user:room:list";
	// 房间的用户列表
	private static final String CMD_ROOM_USER_LIST = "room:user:list";
	// 查询/设置房间内某用户的信息
	private static final String CMD_ROOM_USER = "room:user:";
	
	private IQHandlerInfo info;
	
	public MUCExtendIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo(MODULE_NAME, NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = null;
		Element query = packet.getChildElement();
		String cmd = query.attributeValue("cmd");
		if (CMD_USER_ROOM_LIST.equalsIgnoreCase(cmd)) {
			reply = handleUserRoomListIQ(packet);
		} else if (CMD_ROOM_USER_LIST.equalsIgnoreCase(cmd)) {
			reply = handleRoomUserListIQ(packet);
		} else if (CMD_ROOM_USER.equalsIgnoreCase(cmd)) {
			reply = handleRoomUserIQ(packet);
		} else {
			reply = IQ.createResultIQ(packet);
			reply.setType(IQ.Type.error);
		}
		
		return reply;
	}
	
	private IQ handleUserRoomListIQ(IQ packet) {
		IQ reply = IQ.createResultIQ(packet);
		String jid = packet.getFrom().toBareJID();
		List<RoomEntity> roomList = MUCDao.getRoomList(jid);
		
		Element element = reply.getElement();
		Element queryElement = element.addElement("query", NAME_SPACE);
		queryElement.addAttribute("cmd", CMD_USER_ROOM_LIST);
		Element roomElement = null;
		
		for (RoomEntity room : roomList) {
			roomElement = queryElement.addElement("room");
			roomElement.addAttribute("roomID", String.valueOf(room.getRoomID()));
			roomElement.addAttribute("name", room.getName());
			roomElement.addAttribute("naturalName", room.getNaturalName());
			roomElement.addAttribute("description", room.getDescription());
		}
		
		return reply;
	}
	
	private IQ handleRoomUserListIQ(IQ packet) {
		IQ reply = IQ.createResultIQ(packet);
		Element query = packet.getChildElement();
		String roomName = query.attributeValue("roomName");
		List<UserEntity> userList = MUCDao.getUserList(roomName);
		
		Element element = reply.getElement();
		Element queryElement = element.addElement("query", NAME_SPACE);
		queryElement.addAttribute("cmd", CMD_ROOM_USER_LIST);
		Element userElement = null;
		
		for (UserEntity user : userList) {
			userElement = queryElement.addElement("user");
			userElement.addAttribute("jid", user.getJid());
			userElement.addAttribute("nickname", user.getNickname());
			if (MUCRole.Affiliation.owner.getValue() == user.getAffiliation()) {
				userElement.addAttribute("owner", String.valueOf(true));
			}
			if (MUCRole.Affiliation.admin.getValue() == user.getAffiliation()) {
				userElement.addAttribute("admin", String.valueOf(true));
			}
		}
		
		return reply;
	}
	
	private IQ handleRoomUserIQ(IQ packet) {
		IQ reply = IQ.createResultIQ(packet);
		Element query = packet.getChildElement();
		if (packet.getType() == IQ.Type.get) {
			String jid = packet.getFrom().toBareJID();
			String roomName = query.attributeValue("roomName");
			UserEntity user = MUCDao.getUser(roomName, jid);
			if (user != null) {
				Element element = reply.getElement();
				Element queryElement = element.addElement("query", NAME_SPACE);
				queryElement.addAttribute("cmd", CMD_ROOM_USER);
				Element userElement = queryElement.addElement("user");
				userElement.addAttribute("jid", user.getJid());
				userElement.addAttribute("nickname", user.getNickname());
			} else {
				reply.setType(IQ.Type.error);
			}
		} else if (packet.getType() == IQ.Type.set) {
			String jid = packet.getFrom().toBareJID();
			String roomName = query.attributeValue("roomName");
			String nickname = query.attributeValue("nickname");
			MUCDao.updateUserToDB(roomName, jid, nickname);
		}
		
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}
}

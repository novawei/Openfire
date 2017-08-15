package com.nfs.mucextend;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

public class RoomIQHandler extends IQHandler {
	
	private static final String MODULE_NAME = "muc extend room";  
	private static final String NAME_SPACE = "com:nfs:mucextend:room"; 
	    
	private IQHandlerInfo info;
	
	public RoomIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo(MODULE_NAME, NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		String jid = packet.getFrom().toBareJID();
		List<RoomEntity> roomList = MUCDao.getRoomList(jid);
		
		Element element = reply.getElement();
		Element queryElement = element.addElement("query", NAME_SPACE);
		Element roomElement = null;
		
		for (RoomEntity room : roomList) {
			roomElement = queryElement.addElement("room");
			roomElement.addAttribute("roomID", String.valueOf(room.getRoomID()));
			roomElement.addAttribute("name", room.getName());
			roomElement.addAttribute("naturalName", room.getNaturalName());
			roomElement.addAttribute("description", room.getDescription());
			roomElement.addAttribute("nickname", room.getNickname());
		}
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}

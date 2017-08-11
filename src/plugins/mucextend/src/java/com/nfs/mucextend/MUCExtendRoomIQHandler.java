package com.nfs.mucextend;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

public class MUCExtendRoomIQHandler extends IQHandler {
	
	private static final String MODULE_NAME = "muc extend";  
	private static final String NAME_SPACE = "com:nfs:mucextend:room"; 
	    
	private IQHandlerInfo info;
	
	public MUCExtendRoomIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo(MODULE_NAME, NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		String jid = packet.getFrom().toBareJID();
		List<MUCExtendRoom> roomList = MUCExtendDao.getRoomList(jid);
		Element element = reply.getElement();
		Element queryElement = element.addElement("query", NAME_SPACE);
		Element roomElement = null;
		for (MUCExtendRoom room : roomList) {
			roomElement = queryElement.addElement("room");
			roomElement.addAttribute("roomID", String.valueOf(room.getRoomID()));
			roomElement.addAttribute("name", room.getName());
			roomElement.addAttribute("naturalName", room.getNaturalName());
			roomElement.addAttribute("description", room.getDescription());
		}
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}

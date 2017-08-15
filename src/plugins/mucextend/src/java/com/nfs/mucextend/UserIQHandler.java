package com.nfs.mucextend;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MUCRole;
import org.xmpp.packet.IQ;

public class UserIQHandler extends IQHandler {
	
	private static final String MODULE_NAME = "muc extend user";  
	private static final String NAME_SPACE = "com:nfs:mucextend:user"; 
	    
	private IQHandlerInfo info;
	
	public UserIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo(MODULE_NAME, NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		Element query = packet.getChildElement();
		long roomID = Long.valueOf(query.attributeValue("roomID"));
		List<UserEntity> userList = MUCDao.getUserList(roomID);
		
		Element element = reply.getElement();
		Element queryElement = element.addElement("query", NAME_SPACE);
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

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}

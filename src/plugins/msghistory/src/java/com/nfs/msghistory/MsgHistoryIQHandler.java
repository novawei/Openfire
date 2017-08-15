package com.nfs.msghistory;

import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.util.XMPPDateTimeFormat;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;


public class MsgHistoryIQHandler extends IQHandler {
	private static final String MODULE_NAME = "msg history";  
	private static final String NAME_SPACE = "com:nfs:msghistory:query";
	
	private IQHandlerInfo info;

	public MsgHistoryIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo(MODULE_NAME, NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		Element query = packet.getChildElement();
		if (query != null) {
			String domain = packet.getFrom().getDomain();
			String type = query.attributeValue("type");
			long pageNum = Long.valueOf(query.attributeValue("pageNum"));
			long pageSize = Long.valueOf(query.attributeValue("pageSize"));
			if ("chat".equalsIgnoreCase(type)) {
				/**
				 * <message xmlns="jabber:client" to="admin@10.50.200.45" id="iJ0Vm-206" type="chat" from="novawei@10.50.200.45/Spark">
				 * <body>222</body>
				 * <thread>2N12M7</thread>
				 * <x xmlns="jabber:x:event"><offline/><composing/></x>
				 * <active xmlns="http://jabber.org/protocol/chatstates"/>
				 * </message>
				 */
				String peer = query.attributeValue("peer");
				String user = packet.getFrom().getNode();
				List<ChatMsgEntity> msgList = MsgHistoryDao.queryChatMsg(user, peer, pageNum, pageSize);
				
				Element element = reply.getElement();
				Element queryElement = element.addElement("query", NAME_SPACE);
				queryElement.addAttribute("type", type);
				Element msgElement = null;
				Element bodyElement = null;
				JID jid = null;
				for (ChatMsgEntity msg : msgList) {
					msgElement = queryElement.addElement("message");
					jid = new JID(msg.getSender(), domain, null);
					msgElement.addAttribute("from", jid.toBareJID());
					jid = new JID(msg.getReceiver(), domain, null);
					msgElement.addAttribute("to", jid.toBareJID());
					msgElement.addAttribute("type", "chat");
					msgElement.addAttribute("stamp", XMPPDateTimeFormat.format(msg.getCreatedDate()));
					bodyElement = msgElement.addElement("body");
					bodyElement.setText(msg.getBody());
				}
			} else {
				/**
				 * <message to="admin@10.50.200.45/Spark" id="9jwQG-331" type="groupchat" from="test1@conference.10.50.200.45/admin">
				 * <body>333</body>
				 * <x xmlns="jabber:x:event"><offline/><delivered/><displayed/><composing/></x>
				 * </message>
				 */
				List<MultiUserChatService> serviceList = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatServices();
				domain = (serviceList.size() > 0) ? serviceList.get(0).getServiceDomain() : "conference." + domain;
				
				long roomID = Long.valueOf(query.attributeValue("roomID"));
				List<GroupChatMsgEntity> msgList = MsgHistoryDao.queryGroupChatMsg(roomID, pageNum, pageSize);
				
				Element element = reply.getElement();
				Element queryElement = element.addElement("query", NAME_SPACE);
				queryElement.addAttribute("type", type);
				Element msgElement = null;
				Element bodyElement = null;
				JID jid = null;
				for (GroupChatMsgEntity msg : msgList) {
					msgElement = queryElement.addElement("message");
					jid = new JID(msg.getRoomName(), domain, msg.getNickname());
					msgElement.addAttribute("from", jid.toFullJID());
					msgElement.addAttribute("type", "groupchat");
					msgElement.addAttribute("stamp", XMPPDateTimeFormat.format(msg.getCreatedDate()));
					bodyElement = msgElement.addElement("body");
					bodyElement.setText(msg.getBody());
				}
			}
		}
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}

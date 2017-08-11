package com.nfs.msghistory;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MsgHistoryPlugin implements Plugin, PacketInterceptor {
	private static final Logger Log = LoggerFactory.getLogger(MsgHistoryPlugin.class);

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		InterceptorManager.getInstance().addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		InterceptorManager.getInstance().removeInterceptor(this);
	}

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		if (processed == false || incoming == false) {
			// incoming = true 且消息处理完成在保存，消息有可能被reject
			return;
		}
        if (packet instanceof Message) {
        	Message msg = (Message) packet;
        	JID toJID = msg.getTo();
        	if (toJID == null) {
        		return;
        	}
        	if (StringUtils.isEmpty(msg.getBody())) {
        		// 有些消息chatstat，不包含消息内容
        		return;
        	}
        	String username = toJID.getNode();
        	if (username == null || !UserManager.getInstance().isRegisteredUser(toJID)) {
        		// 广播消息或者没有注册的用户
        		return;
        	}
        	if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(toJID.getDomain())) {
                // 非当前openfire服务器信息
                return;
            }
        	
        	if (msg.getType() == Message.Type.chat) {
        		Log.debug("processed = " + processed + "    incoming = " + incoming);
        		Log.debug(msg.toXML());
        		MsgHistoryDao.saveChatMsgToDB(msg);
        	} else if (msg.getType() == Message.Type.groupchat) {
        		Log.debug("processed = " + processed + "    incoming = " + incoming);
        		Log.debug(msg.toXML());
        		String roomName = msg.getFrom().getNode();
        		MUCRoom mucroom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService("conference").getChatRoom(roomName.toLowerCase());
        		if (mucroom != null) {
        			MUCRole senderRole = null;
					JID senderJID;
					// convert the MUC nickname/role JID back into a real user
					// JID
					if (msg.getFrom() != null && msg.getFrom().getResource() != null) {
						// get the first MUCRole for the sender
						List<MUCRole> occupants = null;
						try {
							occupants = mucroom.getOccupantsByNickname(msg.getFrom().getResource().toLowerCase());
						} catch (UserNotFoundException e) {
							Log.error("用户不存在" + e.getMessage());
						}
						senderRole = occupants == null ? null : occupants.get(0);
					}
					if (senderRole == null) {
						// The room itself is sending the message
						senderJID = mucroom.getRole().getRoleAddress();
					} else {
						// An occupant is sending the message
						senderJID = senderRole.getUserAddress();
					}
					MsgHistoryDao.saveGroupChatMsgToDB(mucroom, msg, senderJID);
        		} else {
        			Log.error("未找到房间" + roomName);
        		}
        	}
        }
	}
}

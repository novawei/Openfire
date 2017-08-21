package com.nfs.mucextend;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * 屏蔽，消息发送以用户名为node，用户昵称应该通过客户端获取房间的用户列表之后，进行解析，减轻服务端的压力
 * @author Nova
 *
 */
public class RoomPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		if (processed || !incoming) {
			return;
		}
		if (packet instanceof Presence) {
			String domain = packet.getTo().getDomain();
			String serverName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
			int index = domain.indexOf(serverName);
			if (index > 1) {
				// [conference.]10.50.200.45，如果有subdomain，index应该大于1
				String subdomain = domain.substring(0, index-1);
				MultiUserChatService service = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(subdomain);
				if (service != null) {
					String roomName = packet.getTo().getNode();
					MUCRoom mucroom = service.getChatRoom(roomName);
					if (mucroom == null) {
						return;
					}
					UserEntity user = MUCDao.getUser(roomName, packet.getFrom().toBareJID());
					if (user == null) {
						return;
					}
					packet.setTo(new JID(roomName, domain, user.getNickname()));
				}
			}
		}
	}

}

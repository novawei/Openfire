package com.nfs.mucextend.iq;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MUCRole;
import org.xmpp.packet.IQ;

import com.nfs.mucextend.MUCDao;
import com.nfs.mucextend.RoomEntity;
import com.nfs.mucextend.UserEntity;

public class IQHandlerUtils {
	private static final String MODULE_NAME = "muc extend";  
	private static final String NAMESPACE_PREFIX = "com:nfs:mucextend:";
	
	public static IQProcessor buildProcessor(String namespace, IQProcess process) {
		// 字段拼接namespace
		return new IQProcessor(MODULE_NAME, NAMESPACE_PREFIX+namespace, process);
	}
	
	public static List<IQHandler> buildHandlers() {
		List<IQHandler> handlers = new ArrayList<IQHandler>();
		// 处理用户房间列表
		handlers.add(buildProcessor("room:list", new IQProcess() {
			@Override
			public IQ process(IQ packet) {
				Element query = packet.getChildElement();
				String xmlns = query.getNamespaceURI();
				
				IQ reply = IQ.createResultIQ(packet);
				String jid = packet.getFrom().toBareJID();
				List<RoomEntity> roomList = MUCDao.getRoomList(jid);
				
				Element element = reply.getElement();
				Element queryElement = element.addElement("query", xmlns);
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
		}));
		handlers.add(buildProcessor("user:list", new IQProcess() {
			@Override
			public IQ process(IQ packet) {
				Element query = packet.getChildElement();
				String xmlns = query.getNamespaceURI();
				
				IQ reply = IQ.createResultIQ(packet);
				String roomName = query.attributeValue("roomName");
				List<UserEntity> userList = MUCDao.getUserList(roomName);
				
				Element element = reply.getElement();
				Element queryElement = element.addElement("query", xmlns);
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
		}));
		handlers.add(buildProcessor("user:iq", new IQProcess() {
			@Override
			public IQ process(IQ packet) {
				Element query = packet.getChildElement();
				String xmlns = query.getNamespaceURI();
				
				IQ reply = IQ.createResultIQ(packet);
				if (packet.getType() == IQ.Type.get) {
					String jid = packet.getFrom().toBareJID();
					String roomName = query.attributeValue("roomName");
					UserEntity user = MUCDao.getUser(roomName, jid);
					if (user != null) {
						// 返回查询的用户信息
						Element element = reply.getElement();
						Element queryElement = element.addElement("query", xmlns);
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
					// 返回设置成功后的用户信息
					Element element = reply.getElement();
					Element queryElement = element.addElement("query", xmlns);
					Element userElement = queryElement.addElement("user");
					userElement.addAttribute("jid", jid);
					userElement.addAttribute("nickname", nickname);
				}
				
				return reply;
			}
		}));
		return handlers;
	}
}

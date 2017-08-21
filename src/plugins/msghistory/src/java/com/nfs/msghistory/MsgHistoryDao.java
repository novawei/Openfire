package com.nfs.msghistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class MsgHistoryDao {
	private static final Logger Log = LoggerFactory.getLogger(MsgHistoryDao.class);

	private static final String ADD_CHAT_MSG = "INSERT INTO ofMsgHistoryChat(sender, receiver, createdDate, body) values(?, ?, ?, ?)";
	private static final String ADD_GROUPCHAT_MSG = "INSERT INTO ofMsgHistoryGroupChat(sender, roomID, roomName, createdDate, body, nickname) values(?, ?, ?, ?, ?, ?)";
	private static final String QUERY_CHAT_MSG = "SELECT messageID, sender, receiver, createdDate, body FROM ofMsgHistoryChat "
			+ "WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?) "
			+ "ORDER BY createdDate DESC LIMIT ?,?";
	private static final String QUERY_GROUPCHAT_MSG = "SELECT messageID, sender, roomID, roomName, createdDate, body, nickname FROM ofMsgHistoryGroupChat "
			+ "WHERE roomName=? "
			+ "ORDER BY createdDate DESC LIMIT ?,?";
	
	public static  void saveChatMsgToDB(Message msg) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_CHAT_MSG);
            int i = 1;
            pstmt.setString(i++, msg.getFrom().getNode());
            pstmt.setString(i++, msg.getTo().getNode());
            pstmt.setString(i++, StringUtils.dateToMillis(new Date()));
            pstmt.setString(i++, msg.getBody());
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error saving chat msg", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static void saveGroupChatMsgToDB(MUCRoom room, Message msg, JID senderJID) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_GROUPCHAT_MSG);
            int i = 1;
            pstmt.setString(i++, senderJID.getNode()); //用户的真实username
            pstmt.setLong(i++, room.getID());
            pstmt.setString(i++, room.getName());
            pstmt.setString(i++, StringUtils.dateToMillis(new Date()));
            pstmt.setString(i++, msg.getBody());
            pstmt.setString(i++, msg.getFrom().getResource()); //用户在这个房间的昵称
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error saving groupchat msg", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static List<ChatMsgEntity> queryChatMsg(String user, String peer, long pageNum, long pageSize) {
		Connection con = null;
        PreparedStatement pstmt = null;
        ChatMsgEntity msg = null;
        List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(QUERY_CHAT_MSG);
            int i = 1;
            pstmt.setString(i++, user);
            pstmt.setString(i++, peer);
            pstmt.setString(i++, peer);
            pstmt.setString(i++, user);
            pstmt.setLong(i++, (pageNum-1)*pageSize);
            pstmt.setLong(i++, pageSize);
            ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				msg = new ChatMsgEntity();
				i = 1;
				msg.setMessageID(rs.getLong(i++));
				//sender, receiver, createdDate, body
				msg.setSender(rs.getString(i++));
				msg.setReceiver(rs.getString(i++));
				msg.setCreatedDate(new Date(rs.getLong(i++)));
				msg.setBody(rs.getString(i++));;
				msgList.add(msg);
			}
        }
        catch (SQLException sqle) {
            Log.error("Error query groupchat msg", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return msgList;
	}
	
	public static List<GroupChatMsgEntity> queryGroupChatMsg(String roomName, long pageNum, long pageSize) {
		Connection con = null;
        PreparedStatement pstmt = null;
        GroupChatMsgEntity msg = null;
        List<GroupChatMsgEntity> msgList = new ArrayList<GroupChatMsgEntity>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(QUERY_GROUPCHAT_MSG);
            int i = 1;
            pstmt.setString(i++, roomName);
            pstmt.setLong(i++, (pageNum-1)*pageSize);
            pstmt.setLong(i++, pageSize);
            ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				msg = new GroupChatMsgEntity();
				i = 1;
				msg.setMessageID(rs.getLong(i++));
				//sender, roomID, roomName, createdDate, body, nickname
				msg.setSender(rs.getString(i++));
				msg.setRoomID(rs.getLong(i++));
				msg.setRoomName(rs.getString(i++));
				msg.setCreatedDate(new Date(rs.getLong(i++)));
				msg.setBody(rs.getString(i++));
				msg.setNickname(rs.getString(i++));
				msgList.add(msg);
			}
        }
        catch (SQLException sqle) {
            Log.error("Error query groupchat msg", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return msgList;
	}
}

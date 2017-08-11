package com.nfs.msghistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

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
	
	public static  void saveChatMsgToDB(Message msg) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_CHAT_MSG);
            pstmt.setString(1, msg.getFrom().getNode());
            pstmt.setString(2, msg.getTo().getNode());
            pstmt.setString(3, StringUtils.dateToMillis(new Date()));
            pstmt.setString(4, msg.getBody());
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
            pstmt.setString(1, senderJID.getNode()); //用户的真实username
            pstmt.setLong(2, room.getID());
            pstmt.setString(3, room.getName());
            pstmt.setString(4, StringUtils.dateToMillis(new Date()));
            pstmt.setString(5, msg.getBody());
            pstmt.setString(6, msg.getFrom().getResource()); //用户在这个房间的昵称
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error saving groupchat msg", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
}

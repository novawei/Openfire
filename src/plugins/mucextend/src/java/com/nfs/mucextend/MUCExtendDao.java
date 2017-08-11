package com.nfs.mucextend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MUCExtendDao {
	private static final Logger Log = LoggerFactory.getLogger(MUCExtendDao.class);

	private static final String CHECK_AFFILIATION_EXIST = "SELECT COUNT(*) FROM ofMucAffiliation WHERE roomID=? AND jid=?";
	private static final String CHECK_MEMBER_EXIST = "SELECT COUNT(*) FROM ofMucMember WHERE roomID=? AND jid=?";
	private static final String QUERY_ROOM_LIST = "SELECT DISTINCT r.roomID, r.name, r.naturalName, r.description FROM ofMucRoom AS r "
			+ "INNER JOIN ofMucAffiliation AS a ON r.roomID=a.roomID "
			+ "INNER JOIN ofMucMember AS m ON r.roomID=m.roomID "
			+ "WHERE a.jid=? OR m.jid=?";
	
	public static boolean isMemberExist(long roomID, String jid) {
		boolean exist = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(CHECK_MEMBER_EXIST);
			pstmt.setLong(1, roomID);
			pstmt.setString(2, jid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				exist = rs.getBoolean(1);
			}
		} catch (SQLException sqle) {
			Log.error(sqle.getMessage());
		}
		return exist;
	}
	
	public static boolean isAffiliationExist(long roomID, String jid) {
		boolean exist = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(CHECK_AFFILIATION_EXIST);
			pstmt.setLong(1, roomID);
			pstmt.setString(2, jid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				exist = rs.getBoolean(1);
			}
		} catch (SQLException sqle) {
			Log.error(sqle.getMessage());
		}
		return exist;
	}
	
	public static List<MUCExtendRoom> getRoomList(String jid) {
		List<MUCExtendRoom> roomList = new ArrayList<MUCExtendRoom>();
		Connection con = null;
		PreparedStatement pstmt = null;
		MUCExtendRoom room = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(QUERY_ROOM_LIST);
			pstmt.setString(1, jid);
			pstmt.setString(2, jid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				room = new MUCExtendRoom();
				room.setRoomID(rs.getLong(1));
				room.setName(rs.getString(2));
				room.setNaturalName(rs.getString(3));
				room.setDescription(rs.getString(4));
				roomList.add(room);
			}
		} catch (SQLException sqle) {
			Log.error(sqle.getMessage());
		}
		return roomList;
	}
}

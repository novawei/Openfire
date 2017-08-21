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

public class MUCDao {
	private static final Logger Log = LoggerFactory.getLogger(MUCDao.class);

	private static final String QUERY_USER = "SELECT u.roomID, u.roomName, u.jid, u.nickname FROM ofMucUser AS u "
			+ "WHERE u.roomName=? AND u.jid=?";
	private static final String ADD_USER = "INSERT INTO ofMucUser(roomID, roomName, jid, nickname) VALUES(?, ?, ?)";
	private static final String UPDATE_USER = "UPDATE ofMucUser SET nickname=? WHERE roomName=? AND jid=?";
	private static final String DELETE_USER = "DELETE FROM ofMucUser WHERE roomName=? AND jid=?";
	private static final String DELETE_ROOM = "DELETE FROM ofMucUser WHERE roomName=?";
	
	private static final String QUERY_ROOM_LIST = "SELECT DISTINCT r.roomID, r.name, r.naturalName, r.description FROM ofMucRoom AS r "
			+ "WHERE u.jid=?";
	private static final String QUERY_USER_LIST = "SELECT u.roomID, u.roomName, u.jid, u.nickname, a.affiliation FROM ofMucUser AS u "
			+ "LEFT JOIN ofmucaffiliation AS a ON a.roomID=u.roomID AND a.jid=u.jid "
			+ "WHERE u.roomName=?";
	
	public static UserEntity getUser(String roomName, String jid) {
		Connection con = null;
		PreparedStatement pstmt = null;
		UserEntity user = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(QUERY_USER);
			pstmt.setString(1, roomName);
			pstmt.setString(2, jid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = new UserEntity();
				int i = 3;
				user.setJid(rs.getString(i++));
				user.setNickname(rs.getString(i++));
			}
		} 
		catch (SQLException sqle) {
			Log.error("Error get user", sqle);
		}
		finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
		return user;
	}
	
	public static void saveUserToDB(long roomID, String roomName, String jid, String nickname) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_USER);
            int i = 1;
            pstmt.setLong(i++, roomID);
            pstmt.setString(i++, roomName);
            pstmt.setString(i++, jid);
            pstmt.setString(i++, nickname);
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error saving room user", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static void deleteUserFromDB(String roomName, String jid) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_USER);
            int i = 1;
            pstmt.setString(i++, roomName);
            pstmt.setString(i++, jid);
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error delete user", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static void deleteRoomFromDB(String roomName) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_ROOM);
            int i = 1;
            pstmt.setString(i++, roomName);
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error delete room", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static void updateUserToDB(String roomName, String jid, String nickname) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(UPDATE_USER);
            int i = 1;
            pstmt.setString(i++, nickname);
            pstmt.setString(i++, roomName);
            pstmt.setString(i++, jid);
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            Log.error("Error saving room user", sqle);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public static List<UserEntity> getUserList(String roomName) {
		List<UserEntity> userList = new ArrayList<UserEntity>();
		Connection con = null;
		PreparedStatement pstmt = null;
		UserEntity user = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(QUERY_USER_LIST);
			pstmt.setString(1, roomName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				user = new UserEntity();
				int i = 3;
				user.setJid(rs.getString(i++));
				user.setNickname(rs.getString(i++));
				user.setAffiliation(rs.getInt(i++));
				userList.add(user);
			}
		} 
		catch (SQLException sqle) {
			Log.error("Error get user list", sqle);
		}
		finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
		return userList;
	}
	
	public static List<RoomEntity> getRoomList(String jid) {
		List<RoomEntity> roomList = new ArrayList<RoomEntity>();
		Connection con = null;
		PreparedStatement pstmt = null;
		RoomEntity room = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(QUERY_ROOM_LIST);
			pstmt.setString(1, jid);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				room = new RoomEntity();
				int i = 1;
				room.setRoomID(rs.getLong(i++));
				room.setName(rs.getString(i++));
				room.setNaturalName(rs.getString(i++));
				room.setDescription(rs.getString(i++));
				roomList.add(room);
			}
		} 
		catch (SQLException sqle) {
			Log.error("Error get room list", sqle);
		}
		finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
		return roomList;
	}
}

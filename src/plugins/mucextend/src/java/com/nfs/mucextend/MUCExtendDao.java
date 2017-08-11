package com.nfs.mucextend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MUCExtendDao {
	private static final Logger Log = LoggerFactory.getLogger(MUCExtendDao.class);

	private static final String CHECK_AFFILIATION_COUNT = "SELECT COUNT(*) FROM ofMucAffiliation WHERE roomID=? AND jid=?";
	private static final String CHECK_MEMBER_EXIST = "SELECT COUNT(*) FROM ofMucMember WHERE roomID=? AND jid=?";
	
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
			pstmt = con.prepareStatement(CHECK_AFFILIATION_COUNT);
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
}

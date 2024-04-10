package Models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Databases.ConnectionPool;
import Models.Basic.BasicImpl;
import Models.Objects.UserObject;

public class UserImpl extends BasicImpl implements User {

	public UserImpl(ConnectionPool cp) {
		super(cp, "User");
	}

	@Override
	public ConnectionPool getCP() {
		return this.getCP();
	}

	@Override
	public void releaseConnection() {
		this.releaseConnection();
	}

	@Override
	public boolean addUser(UserObject item) {

		if (this.isExisting(item)) {
			return false;
		}
		/**
		 * user_id, user_name, user_password, user_email, user_phone, user_role,
		 * user_logined
		 */
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO tbluser(");
		sql.append("user_name, user_password, user_fullname, user_email, user_phone, user_role, user_logined");
		sql.append(") VALUE(?,md5(?),?,?,?,?,?)");
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setString(1, item.getUser_name());
			pre.setString(2, item.getUser_password());
			pre.setString(3, item.getUser_fullname());
			pre.setString(4, item.getUser_email());
			pre.setString(5, item.getUser_phone());
			pre.setInt(6, item.getUser_role());
			pre.setInt(7, item.getUser_logined());
			return this.add(pre);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	private boolean isExisting(UserObject item) {
		boolean flag = false;
		String sql = "SELECT user_id FROM tbluser WHERE user_name='" + item.getUser_name() + "'";
		ResultSet rs = this.gets(sql);
		if (rs != null) {
			try {
				if (rs.next()) {
					flag = true;
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public boolean editUser(UserObject item) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE tbluser SET ");
		sql.append("user_fullname=?, user_email=?, user_phone=? ");
		sql.append("WHERE user_id=? ");
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setString(1, item.getUser_fullname());
			pre.setString(2, item.getUser_email());
			pre.setString(3, item.getUser_phone());
			pre.setInt(4, item.getUser_id());
			return this.edit(pre);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean delUser(UserObject item) {
		String sql = "DELETE FROM tbluser WHERE user_id=? ";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, item.getUser_id());
			return this.del(pre);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				this.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public ResultSet getUser(int id) {
		String sql = "SELECT * FROM tbluser WHERE (user_id=?);";
		return this.get(sql, id);
	}

	@Override
	public ResultSet getUser(String username, String userpass) {
		ArrayList<String> sql = new ArrayList<>();
		String sqlSelect = "SELECT * FROM tbluser WHERE (user_name = ?) AND (user_pass = md5(?));";
		String sqlUpdate = "UPDATE tbluser SET user_logined = user_logined + 1 WHERE (user_name=?) AND (user_pass = md5(?));";
		sql.add(sqlSelect);
		sql.add(sqlUpdate);
		return this.get(sql, username, userpass);
	}
	
	public String createConditions(UserObject similar) {
		StringBuilder conds = new StringBuilder();
		if(similar != null) {
			String key = similar.getUser_name();
			if(key != null && !key.equalsIgnoreCase("")) {
				conds.append(" (user_name LIKE '%"+key+"%') OR ");
				conds.append(" (user_fullname LIKE '%"+key+"%') OR ");
				conds.append(" (user_email LIKE '%"+key+"%') ");
			}
		}
		
		if(!conds.toString().equalsIgnoreCase("")) {
			conds.insert(0, " WHERE ");
		}
		
		return conds.toString();
	}

	@Override
	public ResultSet getUsers(UserObject similar) {
		String sql = "SELECT * FROM tbluser ";
		sql += this.createConditions(similar);
		sql += "ORDER BY usr_id DESC;";
		return this.gets(sql);
	}

}

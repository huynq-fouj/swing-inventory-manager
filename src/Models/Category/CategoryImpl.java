package Models.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Databases.ConnectionPool;
import Models.Basic.BasicImpl;
import Models.Objects.CategoryObject;
import Utilities.Utilities;
import Utilities.Utilities_date;

public class CategoryImpl extends BasicImpl implements Category {

	public CategoryImpl(ConnectionPool cp) {
		super(cp, "Category");
	}

	@Override
	public ConnectionPool getCP() {
		return super.getCP();
	}

	@Override
	public void releaseConnection() {
		super.releaseConnection();
	}

	@Override
	public boolean addCategory(CategoryObject item) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO tblcategory(")
		.append("category_name, category_notes, category_author_id,")
		.append("category_created_date, category_modified_date")
		.append(") VALUES(?,?,?,?,?);");
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setString(1, Utilities.encode(item.getCategory_name()));
			pre.setString(2, Utilities.encode(item.getCategory_notes()));
			pre.setInt(3, item.getAuthor_id());
			pre.setString(4, Utilities_date.getDate());
			pre.setString(5, Utilities_date.getDate());
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

	@Override
	public boolean editCategory(CategoryObject item) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE tblcategory SET ")
		.append("category_name=?, category_notes=?, category_modified_date=? ")
		.append("WHERE category_id=?");
		try {
			PreparedStatement pre = this.con.prepareStatement(sql.toString());
			pre.setString(1, Utilities.encode(item.getCategory_name()));
			pre.setString(2, Utilities.encode(item.getCategory_notes()));
			pre.setString(3, Utilities_date.getDate());
			pre.setInt(4, item.getCategory_id());
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
	public boolean delCategory(CategoryObject item) {
		String sql = "DELETE FROM tblcategory WHERE category_id=?;";
		try {
			PreparedStatement pre = this.con.prepareStatement(sql);
			pre.setInt(1, item.getCategory_id());
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
	public ResultSet getCategory(int id) {
		String sql = "SELECT * FROM tblcategory WHERE category_id=?";
		return this.get(sql, id);
	}

	@Override
	public ResultSet getCategories(CategoryObject similar, CategorySortType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM tblcategory ")
		.append(this.createConditions(similar));
		switch(type) {
		case ID_ASC:
			sql.append("ORDER BY category_id ASC");
			break;
		case NAME_ASC:
			sql.append("ORDER BY category_name ASC");
			break;
		case NOTES_ASC:
			sql.append("ORDER BY category_notes ASC");
			break;
		case NAME_DESC:
			sql.append("ORDER BY category_name DESC");
			break;
		case NOTES_DESC:
			sql.append("ORDER BY category_notes DESC");
			break;
		default:
			sql.append("ORDER BY category_id DESC");
		}
		sql.append(";");
		return this.gets(sql.toString());
	}
	
	@Override
	public ResultSet countCategory(CategoryObject similar) {
		String sql = "SELECT COUNT(*) AS total FROM tblcategory ";
		sql += this.createConditions(similar);
		return this.gets(sql);
	}

	private String createConditions(CategoryObject similar) {
		StringBuilder conds = new StringBuilder();
		if(similar != null) {
			if(similar.getAuthor_id() > 0) {
				int id = similar.getAuthor_id();
				conds.append(" category_author_id=").append(id);
			}
			
			String name = similar.getCategory_name();
			if(name != null && !name.equalsIgnoreCase("")) {
				if(!conds.toString().equalsIgnoreCase("")) {
					conds.append(" AND ");
				}
				String key = Utilities.encode(name);
				conds.append(" ((category_name LIKE '%"+key+"%') OR ");
				conds.append(" (category_notes LIKE '%"+key+"%')) ");
			}
		}
		
		if(!conds.toString().equalsIgnoreCase("")) {
			conds.insert(0, " WHERE ");
		}
		
		conds.append(" ");
		
		return conds.toString();
	}

}

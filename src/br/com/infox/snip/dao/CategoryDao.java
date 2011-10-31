package br.com.infox.snip.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import br.com.infox.snip.models.Category;
import br.com.infox.snip.models.Snippet;
import br.com.infox.utils.ConnectionFactory;

public class CategoryDao implements Dao<Category> {

	@Override
	public void insert(Category c) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "INSERT INTO tb_category (nm_category) VALUES (?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, c.getName());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public List<Category> list() throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "SELECT * FROM tb_category ORDER BY nm_category";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		List<Category> categories = new LinkedList<Category>();
		while (rs.next()) {
			Category c = new Category();
			c.setIdCategory(rs.getLong("id_category"));
			c.setName(rs.getString("nm_category"));
			categories.add(c);
		}
		rs.close();
		ps.close();
		con.close();
		return categories;
	}

	@Override
	public void remove(Category c) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "DELETE FROM tb_category WHERE id_category = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, c.getIdCategory());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public void update(Category c) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "UPDATE tb_category SET nm_category = ? WHERE id_category = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, c.getName());
		ps.setLong(2, c.getIdCategory());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public Category find(Object id) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "SELECT * FROM tb_snippet WHERE id_snippet = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, (Long) id);
		ResultSet rs = ps.executeQuery();
		rs.next();
		Category category = new Category();
		category.setIdCategory(rs.getLong("id_category"));
		category.setName(rs.getString("nm_category"));
		rs.close();
		ps.close();
		con.close();
		return category;
	}
	
	public List<Snippet> findSnippets(Category c) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "SELECT * FROM tb_snippet WHERE id_category = ? ORDER BY nm_snippet";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, c.getIdCategory());
		ResultSet rs = ps.executeQuery();
		List<Snippet> snippets = new LinkedList<Snippet>();
		while (rs.next()) {
			Snippet snippet = new Snippet();
			snippet.setName(rs.getString("nm_snippet"));
			snippet.setSnippet(rs.getString("ds_snippet"));
			snippet.setCategory(c);
			snippet.setIdSnippet(rs.getLong("id_snippet"));
			snippets.add(snippet);
		}
		rs.close();
		ps.close();
		con.close();
		return snippets;
	}
}

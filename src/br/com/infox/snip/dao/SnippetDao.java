package br.com.infox.snip.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import br.com.infox.snip.models.Snippet;
import br.com.infox.utils.ConnectionFactory;

public class SnippetDao implements Dao<Snippet> {

	@Override
	public void insert(Snippet s) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "INSERT INTO tb_snippet (nm_snippet, ds_snippet, id_category) " +
				"VALUES (?, ?, ?)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, s.getName());
		ps.setString(2, s.getSnippet());
		ps.setLong(3, s.getCategory().getIdCategory());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public List<Snippet> list() throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "SELECT * FROM tb_snippet ORDER BY nm_snippet";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		List<Snippet> snippets = new LinkedList<Snippet>();
		while (rs.next()) {
			Snippet s = new Snippet();
			s.setIdSnippet(rs.getLong("id_snippet"));
			s.setName(rs.getString("nm_snippet"));
			s.setSnippet(rs.getString("ds_snippet"));
			s.setCategory(new CategoryDao().find(rs.getLong("id_category")));
			snippets.add(s);
		}
		rs.close();
		ps.close();
		con.close();
		return snippets;
	}

	@Override
	public void remove(Snippet s) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "DELETE FROM tb_snippet WHERE id_snippet = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, s.getIdSnippet());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public void update(Snippet s) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "UPDATE tb_snippet SET nm_snippet = ?," +
				"ds_snippet = ?, id_category = ? WHERE id_snippet = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, s.getName());
		ps.setString(2, s.getSnippet());
		ps.setLong(3, s.getCategory().getIdCategory());
		ps.setLong(4, s.getIdSnippet());
		ps.executeUpdate();
		ps.close();
		con.close();
	}

	@Override
	public Snippet find(Object id) throws SQLException {
		Connection con = ConnectionFactory.getConnection();
		String sql = "SELECT * FROM tb_snippet WHERE id_snippet = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, (Long) id);
		ResultSet rs = ps.executeQuery();
		rs.next();
		Snippet snippet = new Snippet();
		snippet.setCategory(new CategoryDao().find(rs.getLong("id_category")));
		snippet.setIdSnippet(rs.getLong("id_snippet"));
		snippet.setName(rs.getString("nm_snippet"));
		snippet.setSnippet(rs.getString("ds_snippet"));
		rs.close();
		ps.close();
		con.close();
		return snippet;
	}
}

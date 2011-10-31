package br.com.infox.snip.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

	void insert(T o) throws SQLException;
	void update(T o) throws SQLException;
	void remove(T o) throws SQLException;
	List<T> list() throws SQLException;
	T find(Object id) throws SQLException;
}

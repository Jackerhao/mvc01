package com.home.mvcapp.test;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

import com.home.mvcapp.db.JdbcUtils;

public class JdbcUtilsTest {

	@Test
	public void testGetConnection() throws SQLException {
		Connection connection = JdbcUtils.getConnection();
		System.out.println(connection);
		
	}

}

package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
//数据库连接类
public class DbConnection {
	
	//初始化
	Connection con = null;
	Statement statement = null;
	ResultSet result = null;
	
	//JDBC连接方法
	public Connection getConn() {
		try {
			String driver = "com.mysql.jdbc.Driver";//连接驱动
		    String url = "jdbc:mysql://127.0.0.1:3306/fileUpload";//连接地址
			String user = "root";//用户
			String password = "root";//密码
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return this.con;
	}

	public DbConnection() {
		this.con = this.getConn();
	}
	//查询
	public ResultSet query(String sql) {
		this.con = this.getConn();
		try {
			statement = con.createStatement();
			result = statement.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	//更新
	public void update(String sql) {
		this.con = this.getConn();
		try {
			statement = con.createStatement();
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//关闭
	public void close() {
		try {
			if (result != null)
				result.close();
			if (statement != null)
				statement.close();
			if (con != null)
				con.close();
		} catch (Exception ex) {	
			ex.printStackTrace();
		}
	}
}

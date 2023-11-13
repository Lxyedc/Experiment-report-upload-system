package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
//���ݿ�������
public class DbConnection {
	
	//��ʼ��
	Connection con = null;
	Statement statement = null;
	ResultSet result = null;
	
	//JDBC���ӷ���
	public Connection getConn() {
		try {
			String driver = "com.mysql.jdbc.Driver";//��������
		    String url = "jdbc:mysql://127.0.0.1:3306/fileUpload";//���ӵ�ַ
			String user = "root";//�û�
			String password = "root";//����
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
	//��ѯ
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
	//����
	public void update(String sql) {
		this.con = this.getConn();
		try {
			statement = con.createStatement();
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//�ر�
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

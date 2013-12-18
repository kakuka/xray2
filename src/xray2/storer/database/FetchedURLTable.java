package xray2.storer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import xray2.entity.URLEntity;

public class FetchedURLTable{
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/xray2?useUnicode=true&characterEncoding=UTF-8";
	private String user = "root";
	private String password = "kakuka";
	protected Connection conn;
	
	public FetchedURLTable(){
		try {
			Class.forName(driver);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void connectJdbc(){
		try {
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void closeConnection(){
		try {
			if(conn != null){
				conn.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void addUrl(URLEntity urlEntity) {
		this.connectJdbc();
		//System.out.println("fetch->addUrl");

		String sql = "insert into fetched_url_table("
				+ "url,"
				+ "host,"
				+ "anchor_text,"
				+ "title,"
				+ "content,"
				+ "cash,"
				+ "priority,"
				+ "last_fetch_time,"
				+ "last_modify_time,"
				+ "last_fetch_updated,"
				+ "info"
				+ ") "
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, urlEntity.getUrl());
			preparedStatement.setString(2, urlEntity.getHost());
			preparedStatement.setString(3, urlEntity.getAnchorText());
			preparedStatement.setString(4, urlEntity.getTitle());
			preparedStatement.setString(5, urlEntity.getContent());
			preparedStatement.setDouble(6, urlEntity.getCash());
			preparedStatement.setInt(7, urlEntity.getPriority());
			preparedStatement.setString(8, urlEntity.getLastFetchTime());
			preparedStatement.setString(9, urlEntity.getLastModifyTime());
			preparedStatement.setInt(10, urlEntity.getLastFetcheUpdated());
			preparedStatement.setString(11, urlEntity.getInfo());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeConnection();
		}
	}
	
	public synchronized boolean checkExist(String url) {
		this.connectJdbc();
		//System.out.println("fetched->checkExist");

		String sql = "select id from fetched_url_table where url = ?";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, url);
			ResultSet rS = preparedStatement.executeQuery();
			boolean rt = false;
			if(rS.next() && rS.getString("id") != null){
				rt = true;
			}
			preparedStatement.close();
			return rt;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeConnection();
		}
		return false;
	}
}

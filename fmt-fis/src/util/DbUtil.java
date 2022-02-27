package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

public class DbUtil {

//	private static JdbcConnectionSource connectionSource = null;
	private static JdbcConnectionSource connectionSource = null;
	
	public static JdbcConnectionSource getConnSource(){
		try {
			
			Class.forName("org.sqlite.JDBC");
			
			
			String url = System.getProperty("user.home")+"/fmt-fis/fmt-fis.db";			
			
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + url);
			Statement statement = conn.createStatement();
			
			statement.executeUpdate("create table if not exists config("
					+ "id integer primary key autoincrement,"
					+ "lpfrUrl text,"
					+ "pin text,"
					+ "printReceipt integer default 1,"
					+ "printerName text,"
					+ "paperWidth integer default 80,"
					+ "numOfPrintCopies integer default 1"					
					+ ");");
			
			ResultSet rss = statement.executeQuery("select * from config;");
			
			if(!rss.next()){
				statement.executeUpdate("insert into config values("
						+ "1, "
						+ "'http://devesdc.sandbox.suf.purs.gov.rs:8888/7711811d-3006-411e-82c8-c45b4943fad7/api/v3/;"
						+ "http://devesdc.sandbox.suf.purs.gov.rs:8888/d26bfbe0-49d1-431a-ba26-525fc28889c3/api/v3/;"
						+ "http://devesdc.sandbox.suf.purs.gov.rs:8888/11373f9c-6526-408c-9dc1-d52ad97b3905/api/v3/',"
						+ "'7173;5657;3685',"
						+ "1,"
						+ "'POS-80',"
						+ "80,"
						+ "1)");
				
			}	
			
			statement.executeUpdate("create table if not exists request("
					+ "id integer primary key,"
					+ "requestId text,"
					+ "request text,"
					+ "response text,"
					+ "requestDateAndTime text"				
					+ ");");
			
//			ResultSet rs = statement.executeQuery("select * from config;");
//			while(rs.next()){
//				System.out.println(rs.getString(2));
//				System.out.println(rs.getString(3));
//			}
			
			connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + url);
						
			return connectionSource;
			
//			Properties prop = new Properties();
//			String path = System.getProperty("user.home") + "\\fmt-fis\\config.properties";
//			InputStream inputStream = new FileInputStream(path);
//            prop.load(inputStream);
//            
//            String url = prop.getProperty("url");
//            String dbUsername = prop.getProperty("dbUsername");
//            String dbPassword = prop.getProperty("dbPassword"); 
//                   
//         
//			connectionSource  = new JdbcConnectionSource(url, dbUsername, dbPassword);
//			return connectionSource;
			
			
			
		} catch (Exception e) {
			System.out.println("neka greska u sql-u");
			return null;
		}
	}
	
	public void setupDatabase(){
		
	}
	
	
	public DbUtil() {
		// TODO Auto-generated constructor stub
	}
	
	

}

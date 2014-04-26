package com.erigo.server;
import java.sql.*;

public class DatabaseConn {

	String database = "Erigo"; // ODBC database name
	Connection con = null; 
	Statement stmt = null; 

	public DatabaseConn()
	{
		
		try 
		{ 
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); 
			con = DriverManager.getConnection("jdbc:odbc:" + database); 
			stmt = con.createStatement(); 
		} 
		catch (Exception ex) 
		{ 
			// if not successful, quit 
			System.out.println("Cannot open database -- make sure ODBC is configured properly."); 
			System.exit(1); 
		}
	}

	public String authenticateUser(String username,String password)
	{
		String userID =null;
		String sql = "SELECT userID from FROM Erigo.Users where username = \"" + username +
				"\" and password = \"" +password + "\"";

		ResultSet rs = null; 
		try 
		{ 
			rs = stmt.executeQuery(sql); 
			while (rs.next()) 
			{ 
				userID = rs.getString(1); // read 1st column as text
			} 
		} 
		catch (Exception ex) 
		{ 
			// error executing SQL statement 
			System.out.println("Error: " + ex); 
		}
		return userID;
	} 
}


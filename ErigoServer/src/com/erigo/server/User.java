package com.erigo.server;

//import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

//main actor. allows multiple users from same endpoint
public class User {
	//unique ID used for communication
	private long id;
	public String name;
	//public Timestamp lastSeen; 
	public ClientEndPoint endpoint;

	private String password;
	//public boolean isActive
	public final AtomicInteger currentReqID;

	
	public User(String username, String password, ClientEndPoint endpoint){
		this.endpoint = endpoint;
		this.name= username;
		this.password  = password;
		this.id = Server.clientID.incrementAndGet();
		//this.isActive = true;
		this.currentReqID = new AtomicInteger();
	}
	

	// Returns true if password passed in matches the user's password
	public boolean isValidPassword(String password) {
		return this.password.equals(password);
	}

	
	//id to string
	public String getID()
	{
		return ""+id;
	}
	
	
}

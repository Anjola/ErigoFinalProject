package com.networks.erigo.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Messages {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<Message> messages= new ArrayList<Message>();
	
	
	public static AtomicLong idCreator = new AtomicLong();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, Message> message_map= new HashMap<String, Message>();

	static {
		// Add 3 sample items.
		addItem(new Message("Item 1","",""));
		addItem(new Message("Item 2","",""));
		addItem(new Message("Item 3","",""));
	}

	private static void addItem(Message message) {
		messages.add(message);
		message_map.put(message.id, message);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class Message{
		public String id;
		public String songUrl; 
		public String imageUrl;
		public String message; 
		public boolean encouragingPost;
		public List<String> Categories = new ArrayList<String>();
		
		public Message(String message, String imageUrl, String songUrl){
			this.id =  ""+idCreator.getAndIncrement();
			this.message  = message;
			this.imageUrl = imageUrl;
			this.songUrl = songUrl;
		}
		
	  public boolean isEmpty()
	  {
		return message.isEmpty()&&imageUrl.isEmpty()&&songUrl.isEmpty();
	  }


		@Override
		public String toString() {
			return this.message;
		}
	}
}

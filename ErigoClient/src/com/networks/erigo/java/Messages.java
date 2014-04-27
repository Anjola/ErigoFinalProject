package com.networks.erigo.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

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

	public static Map<String, Message> message_map= new HashMap<String, Message>();

	private static void addItem(Message message) {
		messages.add(message);
		message_map.put(message.id, message);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class Message{
		@JsonProperty("id")
		public String id = ""+idCreator.getAndIncrement();
		@JsonProperty("songUrl")
		public String songUrl; 
		@JsonProperty("imageUrl")
		public String imageUrl;
		@JsonProperty("message")
		public String message; 
		@JsonProperty("encouragingPost")
		public boolean encouragingPost;
		@JsonProperty("Specific")
		public boolean Specific;
		@JsonProperty("Categories")
		public List<String> Categories = new ArrayList<String>();
		
		@JsonIgnore
		public Message(String message, String imageUrl, String songUrl){
			this.message  = message;
			this.imageUrl = imageUrl;
			this.songUrl = songUrl;
		}
		
		public Message(){
			
		}
		
		@JsonIgnore
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

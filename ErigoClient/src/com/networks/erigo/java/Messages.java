package com.networks.erigo.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

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

	public static void addItem(Message message) {
		messages.add(message);
		message_map.put(message.id, message);
	}

	//Message class representing a full message.
	public static class Message implements Parcelable{
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

		/**Ctor from Parcel, reads back fields IN THE ORDER they were written */

		public Message(Parcel pc){
			id = pc.readString();
			imageUrl = pc.readString();
			songUrl = pc.readString();
			message = pc.readString();
			encouragingPost = (pc.readInt() == 1);
			Specific = (pc.readInt() == 1);
			pc.readStringList(Categories);
		}

		public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
			@Override
			public Message createFromParcel(Parcel pc) {
				// TODO Auto-generated method stub
				return new Message(pc);
			}

			//for batch crreation
			public Message[] newArray(int size) {
				return new Message[size];
			}


		};
		@JsonIgnore
		public boolean isEmpty()
		{
			return message.isEmpty()&&imageUrl.isEmpty()&&songUrl.isEmpty();
		}


		@Override
		@JsonIgnore
		public String toString() {
			return this.message;
		}

		@Override
		@JsonIgnore
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		@JsonIgnore
		public void writeToParcel(Parcel pc, int flags) {
			// TODO Auto-generated method stub

			pc.writeString(id);
			pc.writeString(imageUrl);
			pc.writeString(songUrl);
			pc.writeString(message);
			pc.writeInt( encouragingPost ? 1 :0 );
			pc.writeInt(Specific?1: 0);
			pc.writeStringList(Categories);

		}
	}
}

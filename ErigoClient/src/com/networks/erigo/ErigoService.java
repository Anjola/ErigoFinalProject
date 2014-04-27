package com.networks.erigo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ErigoService extends Service {

	public static final int SERVERPORT= 30000;
	public static final String SERVERADDRESS = "54.186.253.58";
	public static final int MAXPACKETSIZE = 2048;
	//stores all messages that have not been acknowledged. maps an ackId to the Scheduled sender 
	protected static final Map<String, ScheduledExecutorService> pendingAck = 
			Collections.synchronizedMap(new HashMap<String, ScheduledExecutorService>());

	//for unique id generation for ackID
	protected static final AtomicLong  acKID = new AtomicLong();



	private String clientID = null; 
	private final IBinder mBinder = new MyBinder();

	public DatagramSocket socket;
	public InetSocketAddress serverSocketAddress;
	private String group = null;
	public Messenger myMessenger;
	private AtomicBoolean isRegistered = new AtomicBoolean(); 
	private ScheduledExecutorService registerExecutor;
	//binder to return
	public class MyBinder extends Binder {
		ErigoService getService() {
			return ErigoService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		//start the listeners
		Log.i("MyService", "Bound");
		init();
		return mBinder;
	}

	//messenger from mainActivity
	public void setMessenger(Messenger messenger)
	{
		myMessenger = messenger;
	}


	//starts service thread 
	public void init()
	{
		new Thread(){

			@Override
			public void run()
			{
				try {

					socket = new DatagramSocket();
					serverSocketAddress =  new InetSocketAddress(SERVERADDRESS,SERVERPORT);

					while(true)
					{
						byte[] buf = new byte[MAXPACKETSIZE];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						// call receive (this will poulate the packet with the received
						// data, and the other endpoint's info)
						socket.receive(packet);
						// start up a worker thread to process the packet (and pass it
						// the socket, too, in case the
						// worker thread wants to respond)
						WorkerThread t = new WorkerThread(packet);
						t.start();
					}

				} catch (SocketException e) {
					// TODO Auto-generated catch block
					Log.i("Exception",e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("Exception",e.getMessage());
					e.printStackTrace();
				}




			}
		}.start();
	}
	public void registerClient(String username,String password) {
		final String sendPayload = "REGISTER,"+username+","+password;
		registerExecutor = Executors
				.newSingleThreadScheduledExecutor();
		Runnable sendTask = new Runnable() {
			public void run() {

				try {
					DatagramPacket txPacket = new DatagramPacket(
							sendPayload.getBytes(), sendPayload.length(), serverSocketAddress);
					socket.send(txPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();	

				}
			}
		};
		//try to connect every 25 seconds
		registerExecutor.scheduleAtFixedRate(sendTask, 0, 25,
				SECONDS);
	
	}	


	private class WorkerThread extends Thread {
		private DatagramPacket rxPacket;

		public WorkerThread(DatagramPacket packet){
			this.rxPacket = packet;
		}

		@Override
		public void run(){
			// convert the rxPacket's payload to a string
			String payload = new String(rxPacket.getData(), 0, rxPacket.getLength())
			.trim();
			// dispatch request handler functions based on the payload's prefix
			String[] params = payload.split(",");
			acknowlege(params[0]);
			String command = params[0];
			if(params.length>1){
				command = params[1];
			}
			Log.i("Verbose",payload);
			if(payload.contains("REGISTERED")){
				onRegistered(payload);
				return;
			}

			if (command.startsWith("+ERROR")) {
				onErrorReceived(payload);
				return;
			}

			if (command.startsWith("+SUCCESS")) {
				onSuccessReceived(payload);
				return;
			}
			if(command.startsWith("GENERALMESSAGE")){
				onGeneralMessage(payload.replace(params[0]+","+command+",",""));
				return;

			}	
			if(command.startsWith("SPECIFICMESSAGE")){
				onSpecificMessage(payload.replace(params[0]+","+command+",",""));
				return;
			}
			if(command.startsWith("PROBLEM")){
				onProblemMessage(payload.replace(params[0]+","+command+",",""));
				return;
			}
			if(payload.startsWith("ACK")){
				onACKReceived(params[1]);
				return;
			}

			//reach here bad request 
			onBadRequest(payload);
		}

		private void onProblemMessage(String payload) {
			// TODO Auto-generated method stub
			//alert mainactivity of data availability
			Message message = Message.obtain(null, ErigoFrameActivity.PROBLEMMESSAGE);
			message.obj = payload;
			try {
				myMessenger.send(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private void onSpecificMessage(String payload) {
			// TODO Auto-generated method stub
			
			//alert mainactivity of data availability
			Message message = Message.obtain(null, ErigoFrameActivity.SPECIFICMESSAGE);
			message.obj = payload;
			try {
				myMessenger.send(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// take care of notification 
			Intent intent = new Intent(ErigoService.this, ErigoFrameActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(ErigoService.this,
			    0,intent,0);

			// build notification
			NotificationCompat.Builder builder = new NotificationCompat.Builder(ErigoService.this)
			        .setContentTitle("New Encouragement!")
			        .setContentText("Cheer up. You have reiceived a new encouragement :)")
			        .setAutoCancel(true)
			        .setContentIntent(pendingIntent);
			
			
			NotificationManager manager = 
			        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and sends it 
			manager.notify(0, builder.build());
		}

		private void onGeneralMessage(String payload) {
			// TODO Auto-generated method stub
			// let mainactivity know data is available
			Message message = Message.obtain(null, ErigoFrameActivity.GENERALMESSAGE);
			message.obj = payload;
			try {
				myMessenger.send(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	public void acknowlege(final String ackid) {
		// TODO Auto-generated method stub
		Log.i("MyService", "Ack,"+ ackid);
		final String payload = "ACK," + ackid;
		new Thread(){

			@Override
			public void run(){
				byte[] buf = payload.getBytes();

				try {
					//send ack only once. server handles invalid id's
					DatagramPacket packet= new DatagramPacket(buf, buf.length, serverSocketAddress);
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		}.start();

	}

	public void onRegistered(String payload) {
		// TODO Auto-generated method stub
		//registration successful
		String [] params = payload.split(",");
		registerExecutor.shutdown();
		if(payload.contains("SUCCESS"))		
		{
			clientID = params[0].split(":")[0];
			
			isRegistered.set(true);
			// let login activity know result 
			Message message = Message.obtain(null, LoginActivity.STATUS);
			message.obj = "SUCCESS";
			try {
				myMessenger.send(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else
		{
			// let login activity know result 
			Message message = Message.obtain(null, LoginActivity.STATUS);
			message.obj = "FAILURE";
			try {
				myMessenger.send(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}



	
	private void onACKReceived(String ackid) {
		Log.i("MyService", "Ack Received:"+ ackid);
		ScheduledExecutorService executor = pendingAck.get(ackid);
		if (executor != null) 
		{	
			executor.shutdown();
			pendingAck.remove(ackid);
			Log.i("OnAckRecieved","ExecutorShutdown");
		} 

	}

	private void onSuccessReceived(String payload) {
		Log.i("MyService", "Success");
		// TODO Auto-generated method stub

	}

	private void onErrorReceived(String payload) {
		Log.i("MyService", "Some Error Occured");
		// TODO Auto-generated method stub
		//some error handling? 

	}

	private void onBadRequest(String payload) {
		Log.i("MyService", "Bad Request");
		Log.i("BadRequest",payload);
	}


	private void sendMSG(String message){
		String payload = "MSG,"+clientID + "," + group +","+message;
		send(payload);

	}
	public void send(String payload) {

		final String ackid  =""+ acKID.getAndIncrement();
		final String sendPayload = ackid+","+ payload;
		final ScheduledExecutorService executor = Executors
				.newSingleThreadScheduledExecutor();
		Runnable sendTask = new Runnable() {
			public void run() {

				try {
					DatagramPacket txPacket = new DatagramPacket(
							sendPayload.getBytes(), sendPayload.length(), serverSocketAddress);
					socket.send(txPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//throw new RuntimeException();	
				}


			}
		};

		//try to send message every 15 seconds
		executor.scheduleAtFixedRate(sendTask, 0, 15,
				SECONDS);
		pendingAck.put(ackid, executor);
	}

	public void sendPoll() {
		send("POLL,"+clientID);
		Toast.makeText(getApplicationContext(), "Polling Server...",
				Toast.LENGTH_SHORT).show();

	}

	public void sendWithID(String string) {
		// TODO Auto-generated method stub
		//include clientID
		send(string.replaceFirst(",",","+clientID +","));
	}	
}




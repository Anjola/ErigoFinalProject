package com.networks.erigo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.networks.erigo.java.ErigoUtils;
import com.networks.erigo.java.Messages;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ErigoFrameActivity extends ActionBarActivity implements
		ActionBar.TabListener,Encourage.OnFragmentInteractionListener,
		Encourager.OnFragmentInteractionListener,PostsFragment.OnFragmentInteractionListener{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ActionBar mActionBar;
	Tab tab;
	ErigoService mService;
	public static final int GENERALMESSAGE = 0;
	public static final int SPECIFICMESSAGE = 1;
	public static final int PROBLEMMESSAGE = 2;
	
	private static class MainHandler extends Handler {
		WeakReference<ErigoFrameActivity> wr;

		MainHandler(ErigoFrameActivity activity){
			wr = new WeakReference<ErigoFrameActivity> (activity);
		}

		@Override
		public void handleMessage (Message message){
			ErigoFrameActivity activity = wr.get();
			if (activity != null) {
				switch(message.what) {
				case GENERALMESSAGE: 
					activity.parseGeneralMessage((String)message.obj);
					break;
				case SPECIFICMESSAGE:
					activity.parseSpecificMessage((String)message.obj);
					break;
				case PROBLEMMESSAGE:
					activity.parseProblemMessage((String)message.obj);
				default:
					super.handleMessage(message);
				}

			}
			else{
				Log.i("MainActivity","activity returned null");
			}
		}
	}


	public Messenger messenger = new Messenger(new MainHandler(this));

	protected boolean isBound;
	public ServiceConnection serviceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			Log.i("MainActivity","service connected");
			ErigoService.MyBinder b = (ErigoService.MyBinder)binder;
			mService = b.getService();
			mService.setMessenger(messenger);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			isBound = false;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.erigo_frame);

		//build filter list
		AssetManager am = getAssets();
		InputStream is;
		try {
			is = am.open("badwords.txt");
			ErigoUtils.populateBadWords(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}
	}
	
	public void parseProblemMessage(String obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("ErigoFrameActivity","onStart");
		super.onStart();
		Intent intent = new Intent(this, ErigoService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i("ErigoFrameActivity","onStop");
		super.onStop();
		if (isBound) {
			unbindService(serviceConnection);
		}
	}
	public void parseSpecificMessage(String obj) {
		// TODO Auto-generated method stub
		
	}

	public void parseGeneralMessage(String obj) {
		// TODO Auto-generated method stub
		
		//extract messageID 
		String messageID = obj.split(",")[0];
		Messages.Message mMessage = null;
		;
		ObjectMapper mapper  = new ObjectMapper();
		try {
			Log.i("Verbose","Json:" + obj.replaceFirst(messageID+",", ""));
			mMessage = mapper.readValue(obj.replaceFirst(messageID+",", ""),Messages.Message.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("Success",mMessage.message);
		Log.i("Success",mMessage.songUrl);
		Log.i("Success",mMessage.imageUrl);
		Log.i("Success",mMessage.Categories.get(0));
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.erigo_frame, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public int getIcon(int position) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
				return R.drawable.ic_action_overflow;
	        case 1:
	           return R.drawable.sadicon;

	        case 2:
	           return R.drawable.happyheart;
	
	        case 3:
	            return  R.drawable.postsicon;
	   
	        case 4:
	        	return R.drawable.ic_action_select_all;
        }
		return 0;
			
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
				case 0:
					return new MenuFragment();
		        case 1:
		           return new Encourage();
	
		        case 2:
		           return new Encourager();
		
		        case 3:
		            return new PostsFragment();
		   
		        case 4:
		        	return new MessageListFragment();
	        }
	        return null;
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return "Menu";
				case 1:
					return "Encourage";
				case 2:
					return "Encourage someone";
				case 3:
					return "View Posts";
				case 4:
					return "Categories";
			}
			return null;
		}
	}


	@Override
	public void onFragmentInteraction(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

}

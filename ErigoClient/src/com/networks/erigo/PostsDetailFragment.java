package com.networks.erigo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.networks.erigo.java.Messages;
import com.networks.erigo.java.Messages.Message;

/**
 * A fragment representing a single Message detail screen. This fragment is
 * either contained in a {@link MessageListActivity} in two-pane mode (on
 * tablets) or a {@link PostsDetailActivity} on handsets.
 */
public class PostsDetailFragment extends Fragment {
	//Message containing arguments for detail view
	private Message mMessage;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PostsDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMessage = getArguments().getParcelable("Messages.Message");
		Log.i("verbose","mMessage parsed succesfully");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_posts_detail,
				container, false);
		
		
		//Parse view
		if(mMessage == null)
			Log.i("verbose","message detail is null");
		if (mMessage!= null) {
			((TextView) rootView.findViewById(R.id.message_detail))
			.setText(mMessage.message);
			if(mMessage.imageUrl !=  null){
				new LoadImagefromUrl(getActivity()).execute( (ImageView)rootView.findViewById(R.id.detailImage), 
						mMessage.imageUrl);
			}
			if(mMessage.songUrl != null){
				
			}
			String tags = "";
			tags+=((mMessage.encouragingPost == true)?"Encouragement":"Problem");
			if(!mMessage.Categories.isEmpty())
			{
				
				for( String category:mMessage.Categories)
				{
					tags += "," + category;
				}
				
			}
			((TextView) rootView.findViewById(R.id.Tags)).setText(tags);
		}
		
		return rootView;
	}


	//loading image Task
	private class LoadImagefromUrl extends AsyncTask< Object, Void, Bitmap > {
		ImageView ivPreview = null;
		//Activity mActivity = null;
		
		   public  LoadImagefromUrl(Activity activity)
		    {
		    // this.mActivity = activity;
		    }
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			this.ivPreview = (ImageView) params[0];
			String url = (String) params[1];
			System.out.println(url);
			return loadBitmap( url );
		}

		@Override
		protected void onPostExecute( Bitmap result ) {
			super.onPostExecute( result );
			ivPreview.setImageBitmap( result );
		}
	}

	public Bitmap loadBitmap( String url ) {
		URL newurl = null;
		Bitmap bitmap = null;

		try {

			newurl = new URL( url );
			bitmap = BitmapFactory.decodeStream( newurl.openConnection().getInputStream() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}
}

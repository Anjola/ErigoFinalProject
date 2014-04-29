package com.networks.erigo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.networks.erigo.java.Messages;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PostsFragment extends Fragment implements
AbsListView.OnItemClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;

	// TODO: Rename and change types of parameters
	public static PostsFragment newInstance(String param1, String param2) {
		PostsFragment fragment = new PostsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PostsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}

		// TODO: Change Adapter to display your content
		mAdapter = new ArrayAdapter<Messages.Message>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				Messages.messages);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_posts, container, false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
//		inputSearch = (EditText) findViewById(R.id.inputSearch);
//
//		// Adding items to listview
//		inputSearch.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//				// When user changed the Text
//				MainActivity.this.adapter.getFilter().filter(cs);
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
//
//			@Override
//			public void afterTextChanged(Editable arg0) {}
//		});

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}


	public void onViewCreated(View view, Bundle savedInstanceState) {
		//initial message population
		((ErigoFrameActivity)getActivity()).mService.getMessages();
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Messages.Message message = (Messages.Message)parent.getItemAtPosition(position);
		Intent intent = new Intent(view.getContext(), PostsDetailActivity.class);
		//send message to details activity 
		Log.i("verbose","Sucess binding");
		intent.putExtra("Messages.Message", message);
		startActivity(intent);
	}
	


	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. 
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public void updateListView(Messages.Message mMessage) {
		// TODO Auto-generated method stub
		Messages.addItem(mMessage);
	}



}

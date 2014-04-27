package com.networks.erigo;

import java.io.IOException;
import java.util.Arrays;

import com.networks.erigo.java.Messages.Message;
import com.networks.erigo.java.ErigoUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link Encourage.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link Encourage#newInstance} factory method to
 * create an instance of this fragment.
 * 
 */
public class Encourage extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	MultiAutoCompleteTextView atv;
	ArrayAdapter<String> adapter;
	String[] categories;
	private OnFragmentInteractionListener mListener;
	EditText et;
	Message mMessage = new Message("","","");



	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment Encourage.
	 */
	// TODO: Rename and change types and number of parameters
	public static Encourage newInstance(String param1, String param2) {
		Encourage fragment = new Encourage();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public Encourage() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_encourage, container, false);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		categories = getResources().getStringArray(R.array.problem_array);
		atv = (MultiAutoCompleteTextView)view.findViewById(R.id.problem);
		adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,categories);
		atv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		atv.setAdapter(adapter);
		atv.setThreshold(0);
		atv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
;					atv.showDropDown();
				}
		});
		
		et=(EditText)view.findViewById(R.id.needtext);


		view.findViewById(R.id.quickbutton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
		view.findViewById(R.id.postButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						if(et!= null && !et.getText().equals("")){
							mMessage.message=et.getText().toString();
							Log.i("verbose",mMessage.message);
						}
						String messageCategories = null;
						if(atv!=null && !atv.getText().equals(""))
						{
							messageCategories = atv.getText().toString();
							Log.i("verbose",messageCategories);
						}
						if(messageCategories == null ||messageCategories.isEmpty() || messageCategories.equals(","))
						{
							mMessage.Categories.add("Unspecified");
						}
						else
						{
							mMessage.Categories.addAll(Arrays.asList(messageCategories.split(",")));
						}
						mMessage.encouragingPost = false;
						Toast.makeText(getActivity().getApplicationContext(), "You're awesome! Thanks :)",
								Toast.LENGTH_LONG).show();	


						//only proceed if non vulgar and non empty
						if(!mMessage.isEmpty() && !ErigoUtils.isProfanePost(mMessage.message)){
							Log.i("Verbose",mMessage.message +  " is not profone");
							ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
							String json;
							try {
								json = ow.writeValueAsString(mMessage);
								((ErigoFrameActivity)getActivity()).mService.sendWithID("ENCOURAGEE,"+json);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						else
						{
							Log.i("Verbose",mMessage.message +  " is profone");
						}
						Log.i("EncouragerFragment","Message sent");
					}

				});
	
	
	

		view.findViewById(R.id.encouragesong).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

						alert.setTitle("Enter Song Url");
						alert.setMessage("Message");

						// Set an EditText view to get user input 
						final EditText input = new EditText(getActivity());
						alert.setView(input);

						alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK button
								if(input!= null && !input.getText().equals(""))
									mMessage.songUrl = input.getText().toString();
								Toast.makeText(getActivity().getApplicationContext(), "Song added!",
										Toast.LENGTH_SHORT).show();	
							}
						});
						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
						alert.show();

					}
				});

		view.findViewById(R.id.encourageimg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

						alert.setTitle("Enter Imagr Url");
						alert.setMessage("Message");

						// Set an EditText view to get user input 
						final EditText input = new EditText(getActivity());
						alert.setView(input);

						alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK button
								if(input!= null && !input.getText().equals(""))
									mMessage.imageUrl = input.getText().toString();
								Toast.makeText(getActivity().getApplicationContext(), "Image added!",
										Toast.LENGTH_LONG).show();
							}
						});
						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
						alert.show();

					}
				});

		view.findViewById(R.id.encouragerecord).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Toast.makeText(getActivity().getApplicationContext(), "Coming soon!",
								Toast.LENGTH_LONG).show();

					}
				});

		view.findViewById(R.id.encourageposts).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Toast.makeText(getActivity().getApplicationContext(), "Coming soon!",
								Toast.LENGTH_LONG).show();

					}
				});



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
	public void onFragmentInteraction(Uri uri);
}

}

package se.gsc.stenmark.gscenduro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A placeholder fragment containing a simple view.
 */
public class CompMangementFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CompMangementFragment newInstance(int sectionNumber) {
		CompMangementFragment fragment = new CompMangementFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public CompMangementFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_comp_management, container,false);
//		View rootView = inflater.inflate(R.layout.fragment_main, container,false);
					
		
		return rootView;
	} 	
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CompMangementFragment getInstance(int sectionNumber) {
		CompMangementFragment fragment = null;
//		if(instance == null){
			fragment = new CompMangementFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
//			instance = fragment;
//		}
		return fragment;
	}
	
   

	

}

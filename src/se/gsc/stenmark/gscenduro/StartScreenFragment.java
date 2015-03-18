package se.gsc.stenmark.gscenduro;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartScreenFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	static StartScreenFragment mStartScreenFragment;
	MainActivity mMainActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMainActivity = ((MainActivity) getActivity());      
    }
		
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static StartScreenFragment getInstance(int sectionNumber) {
		mStartScreenFragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		mStartScreenFragment.setArguments(args);
		return mStartScreenFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
					
		updateConnectText();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
        
		TextView connectButton = (TextView) rootView.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mMainActivity.connectToSiMaster();
					updateConnectText();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});
		return rootView;
	}
	
	public void updateConnectText(){
		try {
			TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);	
			statusTextView.setText(mMainActivity.getConnectionStatus());
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}
}

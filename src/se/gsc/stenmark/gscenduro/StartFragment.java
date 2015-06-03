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
public class StartFragment extends Fragment {
	
	private MainActivity mMainActivity;
	private boolean inView = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMainActivity = ((MainActivity) getActivity());      
    }

	@Override
	public void onResume() {
		super.onResume();
		inView = true;
					
		updateConnectText();
		updateCompetitionStatus();
	}
	
	public void onPause(){
		super.onPause();
		inView = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.start_fragment, container,	false);
        
		TextView connectButton = (TextView) rootView.findViewById(R.id.connect_button);
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
		if(inView){
			try {
				TextView statusTextView = (TextView) getView().findViewById(R.id.status_text);	
				statusTextView.setText(mMainActivity.getConnectionStatus());
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show(getFragmentManager(), "popUp");
			}
		}
	}
	
	public void updateCompetitionStatus(){
		if(inView){
			try {						
				TextView statusTextView;				
				
				statusTextView = (TextView) getView().findViewById(R.id.competition_competition_date);	
				statusTextView.setText(mMainActivity.competition.getCompetitionDate());
				
				statusTextView = (TextView) getView().findViewById(R.id.competition_type);				
				if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.SVARTVITT_TYPE) {
					statusTextView.setText("SvartVitt");	
				} else {
					statusTextView.setText("Enduro Sweden Series");
				}
				
				statusTextView = (TextView) getView().findViewById(R.id.competition_name);	
				statusTextView.setText(mMainActivity.competition.getCompetitionName());
				
				statusTextView = (TextView) getView().findViewById(R.id.track_status);	
				statusTextView.setText(mMainActivity.competition.getTrackAsString());
				
				statusTextView = (TextView) getView().findViewById(R.id.competitor_status);	
				if (mMainActivity.competition.getCompetitionType() == mMainActivity.competition.SVARTVITT_TYPE) {
					statusTextView.setText("Total: " + mMainActivity.competition.getNumbeofCompetitors());
				} else {					
					String numberOfCompetitors = "";
					for (String competitorClass : mMainActivity.competition.getCompetitorClasses()) {
						
						if (numberOfCompetitors.length() != 0) {
							numberOfCompetitors += "\n";
						}
						
						numberOfCompetitors += competitorClass + ": " + mMainActivity.competition.getNumberOfCompetitors(competitorClass);
					}
					
					statusTextView.setText("Total: " + mMainActivity.competition.getNumbeofCompetitors() + "\n" + numberOfCompetitors);					
				}
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
				dialog.show(getFragmentManager(), "popUp");
			}
		}
	}
}

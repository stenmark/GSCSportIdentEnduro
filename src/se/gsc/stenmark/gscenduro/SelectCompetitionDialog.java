package se.gsc.stenmark.gscenduro;

import java.util.List;

import se.gsc.stenmark.gscenduro.StartScreenFragment.CompetitionOnClickListener;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectCompetitionDialog extends DialogFragment {
	private List<String> competitions;
	private CompetitionOnClickListener competitionOnClickListener;
	private MainActivity mMainActivity;
	private StartScreenFragment startScreenFragment;
	CompetitionOnClickListener radioButtonListener;
	
	public SelectCompetitionDialog( List<String> competitions, 
									CompetitionOnClickListener competitionOnClickListener,
									MainActivity mMainActivity,
									StartScreenFragment startScreenFragment,
									CompetitionOnClickListener radioButtonListener) {
		this.competitions = competitions;
		this.competitionOnClickListener = competitionOnClickListener;
		this.mMainActivity = mMainActivity;
		this.startScreenFragment = startScreenFragment;
		this.radioButtonListener = radioButtonListener;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setTitle("Select competition");
        CharSequence[] items = new String[competitions.size()];
        int i = 0;
        for( String competition : competitions){
        	items[i] = competition;
        	i++;
        }
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					String selectedItem = competitions.get(radioButtonListener.which);
					mMainActivity.competition = Competition.loadSessionData(selectedItem);
					mMainActivity.competition.calculateResults();
					mMainActivity.updateFragments();						
					startScreenFragment.updateCompName();
				} catch (Exception e) {
					PopupMessage dialog2 = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog2.show(getFragmentManager(), "popUp");
				}
			}
		});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
    	builder.setSingleChoiceItems(items,0, competitionOnClickListener );
        return builder.create();        
        
    }
    

}

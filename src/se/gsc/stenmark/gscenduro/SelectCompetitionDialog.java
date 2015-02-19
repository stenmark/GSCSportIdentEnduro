package se.gsc.stenmark.gscenduro;

import java.util.List;

import se.gsc.stenmark.gscenduro.StartScreenFragment.CompetitionOnClickListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectCompetitionDialog extends DialogFragment {
	private List<String> competitions;
	CompetitionOnClickListener competitionOnClickListener;
	
	public SelectCompetitionDialog( List<String> competitions, CompetitionOnClickListener competitionOnClickListener ) {
		this.competitions = competitions;
		this.competitionOnClickListener = competitionOnClickListener;
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
    	builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	builder.setSingleChoiceItems(items,0, competitionOnClickListener );
        return builder.create();        
        
    }
    

}

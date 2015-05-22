package se.gsc.stenmark.gscenduro;

import java.util.Collections;
import java.util.Comparator;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PunchListActivity extends ListActivity {

	private ListPunchAdapter mPunchAdapter;
	private Card mUpdatedCard = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		
		setContentView(R.layout.punch_list);
        getListView().setEmptyView(findViewById(R.id.empty));
		
        Button addButton = (Button) findViewById(R.id.punch_list_add);

        addButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPunchAlert();
			}
		});
        
		try{				
			mUpdatedCard = (Card) getIntent().getExtras().getSerializable("Card");
			
			if (mUpdatedCard != null)
			{
				mUpdatedCard.setNumberOfPunches(mUpdatedCard.getPunches().size());							
			}			
			
			sortData();
			mPunchAdapter = new ListPunchAdapter(this, mUpdatedCard.getPunches());
			setListAdapter(mPunchAdapter);			
		}
		catch( Exception e){
			Log.d("PunchListActivity", "Error = " + e);
		}
	}	
	
	public void addPunchAlert(){
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.punch_add, null);
	
		final EditText controlInput = (EditText) promptsView.findViewById(R.id.control_input);		
		final EditText timeInput = (EditText) promptsView.findViewById(R.id.time_input);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Long time = Long.valueOf(timeInput.getText().toString());
								Long control = Long.valueOf(controlInput.getText().toString());
								
								addPunch(time, control);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
	
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();						
	}	
	
	@Override
    public void onBackPressed() {
		backButtonHandler();
        return;
    }   
	
	public void sendCard(){
		//Need to reset the doublePunch marker and let the main program re-evaluate after someone manually edited the card
		for(Punch punch : mUpdatedCard.getPunches()){
			punch.setMarkAsDoublePunch(false);
		}
		Intent intent = new Intent(this, MainActivity.class);		
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("updateCard", mUpdatedCard);
		intent.putExtras(bundle);		                    	
		 
        setResult(2, intent);  		
	}
	
	public void sendEmptyCard(){
		Intent intent = new Intent(this, MainActivity.class);		                    	
		
		Card noCard = new Card();		
		Bundle bundle = new Bundle();
		bundle.putSerializable("updateCard", noCard);
		intent.putExtras(bundle);	
		
        setResult(2, intent);  		
	}
	
    public void backButtonHandler() {              
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.punch_leave, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {							
		                    	sendCard();
		                    	finish();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								sendEmptyCard();
								finish();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();        
    }
	
	public void removePunch(int Position) {
		mUpdatedCard.getPunches().remove(Position);		
		sortData();	
		reloadData();
	}
	
	public void addPunch(long Time, long Control) {
		Punch newPunch = new Punch(Time, Control);
		mUpdatedCard.getPunches().add(newPunch);		
		sortData();
		reloadData();
	}
	
	public void updatePunch(int Position, long Control, long Time) {
		mUpdatedCard.getPunches().get(Position).setControl(Control);
		mUpdatedCard.getPunches().get(Position).setTime(Time);		
		sortData();
		reloadData();
	}
	
	public void sortData() {
	    Collections.sort(mUpdatedCard.getPunches(), new Comparator<Punch>() {
	        @Override
	        public int compare(Punch s1, Punch s2) {
	            return new Long(s1.getTime()).compareTo(s2.getTime());
	        }
	    });	
	}
	
	public void reloadData() {
		mPunchAdapter.notifyDataSetChanged();
	}
}
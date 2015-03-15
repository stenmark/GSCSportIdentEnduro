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

	protected ListPunchAdapter mPunchAdapter;
	protected Card mUpdatedCard = null;
	
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
				mUpdatedCard.numberOfPunches = mUpdatedCard.punches.size();							
			}			
			
			SortData();
			mPunchAdapter = new ListPunchAdapter(this, mUpdatedCard.punches);
			setListAdapter(mPunchAdapter);			
		}
		catch( Exception e){
			Log.d("PunchListActivity", "Error = " + e);
		}
	}	
	
	public void addPunchAlert(){
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_punch, null);
	
		final EditText ControlInput = (EditText) promptsView
				.findViewById(R.id.editTextControlInput);
		
		final EditText TimeInput = (EditText) promptsView
				.findViewById(R.id.editTextTimeInput);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Long time = Long.valueOf(TimeInput.getText().toString());
								Long control = Long.valueOf(ControlInput.getText().toString());
								
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
		for( Punch punch : mUpdatedCard.punches){
			punch.markAsDoublePunch = false;
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
		View promptsView = li.inflate(R.layout.leave_punch, null);

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
		mUpdatedCard.punches.remove(Position);		
		SortData();	
		ReloadData();
	}
	
	public void addPunch(long Time, long Control) {
		Punch newPunch = new Punch(Time, Control);
		mUpdatedCard.punches.add(newPunch);		
		SortData();
		ReloadData();
	}
	
	public void updatePunch(int Position, long Control, long Time) {
		mUpdatedCard.punches.get(Position).control = Control;
		mUpdatedCard.punches.get(Position).time = Time;		
		SortData();
		ReloadData();
	}
	
	public void SortData() {
	    Collections.sort(mUpdatedCard.punches, new Comparator<Punch>() {
	        @Override
	        public int compare(Punch s1, Punch s2) {
	            return s1.getTime().compareTo(s2.getTime());
	        }
	    });	
	}
	
	public void ReloadData() {
		mPunchAdapter.notifyDataSetChanged();
	}
}
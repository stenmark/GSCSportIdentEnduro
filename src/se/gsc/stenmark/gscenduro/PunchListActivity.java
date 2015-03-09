package se.gsc.stenmark.gscenduro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import android.app.Activity;
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
	protected ArrayList<Punch> mAllPunches = new ArrayList<Punch>();
	protected Card mUpdatedCard = new Card();
	protected Activity mMainActivity;
	
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
			mMainActivity = this.getParent(); 
				
			mUpdatedCard = (Card) getIntent().getExtras().getSerializable("Card");
			
			if (mUpdatedCard != null)
			{
				mUpdatedCard.numberOfPunches = mUpdatedCard.punches.size();
				
			    Collections.sort(mUpdatedCard.punches, new Comparator<Punch>() {
			        @Override
			        public int compare(Punch s1, Punch s2) {
			            return s1.getTime().compareTo(s2.getTime());
			        }
			    });
	
			}
			
			fetchItems();
		}
		catch( Exception e){
			PopupMessage2 dialog = new PopupMessage2(MainActivity.generateErrorMessage(e));
			dialog.show( mMainActivity.getFragmentManager(), "popUp");
	
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
	
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		
		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								Long time = Long.valueOf(TimeInput.getText().toString());
								Long control = Long.valueOf(ControlInput.getText().toString());
								
								addPunch(time, control);
								fetchItems();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
	
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
	
		// show it
		alertDialog.show();						
	}	
	
	@Override
    public void onBackPressed() {
		backButtonHandler();
        return;
    }   
	
	public void sendCard(){
		//Need to reset the doublePunch marker and let the main prgoram re-evaluate after someone manually edited the card
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

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {							
		                    	sendCard();
		                    	finish();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								sendEmptyCard();
								finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();        
    }
    
	protected void fetchItems() {					
		mAllPunches.clear();
		
		if (mUpdatedCard != null)
		{			
			for(Punch punch : mUpdatedCard.punches){					
				mAllPunches.add(punch);
			}
																    
			fillList();
		}			
	}

	protected void fillList() {
		populateList();
		
		if (mPunchAdapter == null) {
			mPunchAdapter = new ListPunchAdapter(this, mUpdatedCard.punches);
			setListAdapter(mPunchAdapter);
		} else {
			mPunchAdapter.notifyDataSetChanged();
		}					
	}

	protected void populateList() {		
    	mUpdatedCard.punches.clear();    		
		
		for (int i = 0; i < mAllPunches.size(); i++) {
			mUpdatedCard.punches.add(mAllPunches.get(i));
		}		
		
	    Collections.sort(mUpdatedCard.punches, new Comparator<Punch>() {
	        @Override
	        public int compare(Punch s1, Punch s2) {
	            return s1.getTime().compareTo(s2.getTime());
	        }
	    });
		
		mUpdatedCard.numberOfPunches = mUpdatedCard.punches.size();
	}

	
	public void removePunch(int Position) {
		mUpdatedCard.punches.remove(Position);
	}
	
	public void addPunch(long Time, long Control) {
		Punch newPunch = new Punch(Time, Control);
		mUpdatedCard.punches.add(newPunch);
	}
	
	public void updatePunch(int Position, long Control, long Time) {
		mUpdatedCard.punches.get(Position).control = Control;
		mUpdatedCard.punches.get(Position).time = Time;	
	}
}
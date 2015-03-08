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
import android.view.LayoutInflater;
import android.view.View;

public class PunchListActivity extends ListActivity {

	protected ListPunchAdapter mPunchAdapter;
	protected ArrayList<Punch> mAllPunches = new ArrayList<Punch>();
	protected ArrayList<Punch> mPunches = new ArrayList<Punch>();
	protected Card mUpdatedCard = new Card();
	protected Activity mMainActivity;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		try{
			mMainActivity = this.getParent(); 
				
			mUpdatedCard = (Card) getIntent().getExtras().getSerializable("Card");
			
			if (mUpdatedCard != null)
			{
				for(Punch punch : mUpdatedCard.doublePunches){
					mUpdatedCard.punches.add(punch);				
				}
				mUpdatedCard.doublePunches.clear();
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
	
	@Override
    public void onBackPressed() {
        backButtonHandler();
        return;
    }   
	
	public void sendCard(){
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
			mPunchAdapter = new ListPunchAdapter(this, mPunches);
			setListAdapter(mPunchAdapter);
		} else {
			mPunchAdapter.notifyDataSetChanged();
		}					
	}

	protected void populateList() {		
		mPunches.clear();		        	
    	mUpdatedCard.punches.clear();    		
		
		for (int i = 0; i < mAllPunches.size(); i++) {
			mPunches.add(mAllPunches.get(i));
			mUpdatedCard.punches.add(mAllPunches.get(i));
		}		
		
	    Collections.sort(mUpdatedCard.punches, new Comparator<Punch>() {
	        @Override
	        public int compare(Punch s1, Punch s2) {
	            return s1.getTime().compareTo(s2.getTime());
	        }
	    });
		
		mUpdatedCard.numberOfPunches = mPunches.size();
	}

	
	public void removePunch(int Position) {
		mUpdatedCard.punches.remove(Position);
	}
	
	public void updatePunch(int Position, long Control, long Time) {
		mPunches.get(Position).control = Control;
		mPunches.get(Position).time = Time;	
	}
}
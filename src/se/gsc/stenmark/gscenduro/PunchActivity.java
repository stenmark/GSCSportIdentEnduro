package se.gsc.stenmark.gscenduro;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.gsc.stenmark.gscenduro.R;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.Punch;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PunchActivity extends ListActivity {

	private PunchAdapter mPunchAdapter;
	private Card mUpdatedCard = null;
	private Competition competition;
	private List<Integer> mControls;

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

		try {				
			mUpdatedCard = (Card) getIntent().getExtras().getSerializable("Card");
			competition = (Competition) getIntent().getExtras().getSerializable("Competition");

			mControls = competition.getControls();

			if (mUpdatedCard != null) {
				mUpdatedCard.setNumberOfPunches(mUpdatedCard.getPunches().size());							
			}			

			sortData();
			mPunchAdapter = new PunchAdapter(this, mUpdatedCard.getPunches());
			setListAdapter(mPunchAdapter);			
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}	

	public List<Integer> getControls() {
		return mControls;		
	}	

	public void addPunchAlert(){
		try{
			LayoutInflater li = LayoutInflater.from(this);
			View promptsView = li.inflate(R.layout.punch_add, null);

			final Spinner spinner = (Spinner) promptsView.findViewById(R.id.add_punch_controls_spinner);	
			ArrayAdapter<Integer> LTRadapter = new ArrayAdapter<Integer>(PunchActivity.this, android.R.layout.simple_spinner_item, mControls);
			LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);			
			spinner.setAdapter(LTRadapter);			
			spinner.setSelection(0);				

			final EditText timeInput = (EditText) promptsView.findViewById(R.id.time_input);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("Add punch");
			alertDialogBuilder.setView(promptsView);
			alertDialogBuilder.setPositiveButton("Add", null);
			alertDialogBuilder.setNegativeButton("Cancel", null);		

			final AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (timeInput.length() == 0) {
						Toast.makeText(PunchActivity.this, "No time was supplied", Toast.LENGTH_LONG).show();
						return;            		 
					}

					Long time = Long.valueOf(timeInput.getText().toString());
					int control = Integer.parseInt(spinner.getSelectedItem().toString());

					addPunch(time, control);

					alertDialog.dismiss();
				}
			});	
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
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

	public void sendEmptyCard() {
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

	public void addPunch(long Time, int Control) {
		Punch newPunch = new Punch(Time, Control);
		mUpdatedCard.getPunches().add(newPunch);		
		sortData();
		reloadData();
	}

	public void updatePunch(int Position, int Control, long Time) {
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
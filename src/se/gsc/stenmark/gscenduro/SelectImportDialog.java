package se.gsc.stenmark.gscenduro;

import se.gsc.stenmark.gscenduro.MainActivity.ImportOnClickListener;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionParser;
import se.gsc.stenmark.gscenduro.compmanagement.PunchParser;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitorParser;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SelectImportDialog {
	
	private ImportOnClickListener mImportOnClickListener;
	private MainActivity mMainActivity;
	private CharSequence[] items = {"Competitors", "Punches", "Competition"};
	ImportOnClickListener mRadioButtonListener;
	
	public SelectImportDialog(ImportOnClickListener importOnClickListener,
							  MainActivity MainActivity,
							  ImportOnClickListener radioButtonListener) {
		mImportOnClickListener = importOnClickListener;
		mMainActivity = MainActivity;
		mRadioButtonListener = radioButtonListener;
	}
	
    public void createImportDialog() {   
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle("What do you want to import?");	            
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
				switch(mRadioButtonListener.which)
				{				
				case 0:
					try {
						LayoutInflater li = LayoutInflater.from(mMainActivity);
						View promptsView = li.inflate(R.layout.import_competitors, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

						alertDialogBuilder.setView(promptsView);

						final EditText importCompetitorsInput = (EditText) promptsView.findViewById(R.id.import_competitors_input);

						alertDialogBuilder
								.setCancelable(false)
								.setPositiveButton("Add",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,	int id) {		
												try {													
													CompetitorParser competitorParser = new CompetitorParser();
													
													if (((MainActivity)mMainActivity).competition.getCompetitionType() == ((MainActivity)mMainActivity).competition.ESS_TYPE)
													{
														competitorParser.parseEssCompetitors(importCompetitorsInput.getText().toString());
														for (Competitor competitorObject : competitorParser.getCompetitors()) {		
															((MainActivity)mMainActivity).competition.addCompetitor(competitorObject.getName(), competitorObject.getCardNumber(), competitorObject.getTeam(), 
																	competitorObject.getCompetitorClass(), competitorObject.getStartNumber(), competitorObject.getStartGroup());
														}
													}
													else
													{													
														competitorParser.parseCompetitors(importCompetitorsInput.getText().toString());
														for (Competitor competitorObject : competitorParser.getCompetitors()) {		
															((MainActivity)mMainActivity).competition.addCompetitor(competitorObject.getName(), competitorObject.getCardNumber(), "", "", 0, 0);
														}
													}
																																																		
													String statusMsg = competitorParser.getStatus();
													((MainActivity)mMainActivity).updateFragments();													
													
													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Import competitor status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });
											 
											        AlertDialog alert = builder.create();
											        alert.show();																					
												} catch (Exception e) {
													String statusMsg = MainActivity.generateErrorMessage(e);

													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Error importing competitors").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });		
											        AlertDialog alert = builder.create();
											        alert.show();	
												}																					
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();															
					} catch (Exception e) {
						Log.d("action_import_competitors", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 1:
					try {
						LayoutInflater li = LayoutInflater.from(mMainActivity);
						View promptsView = li.inflate(R.layout.import_punches, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

						alertDialogBuilder.setView(promptsView);

						final EditText importPunchesInput = (EditText) promptsView.findViewById(R.id.import_punches_input);

						alertDialogBuilder
								.setCancelable(false)
								.setPositiveButton("Add",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,	int id) {		
												try {
													PunchParser punchParser = new PunchParser();
													punchParser.parsePunches(importPunchesInput.getText().toString(), ((MainActivity)mMainActivity).competition.getCompetitors());
																										
													for (Card cardObject : punchParser.getCards()) {		
														if (cardObject.getPunches().size() > 0)
														{
															if (cardObject.getCardNumber() != 0) {
																((MainActivity)mMainActivity).competition.processNewCard(cardObject);
															}
														}
													}												
													
													String statusMsg = punchParser.getStatus();
													((MainActivity)mMainActivity).updateFragments();
													
													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Import punches status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });
											 
											        AlertDialog alert = builder.create();
											        alert.show();													        											      
												} catch (Exception e) {
													String statusMsg = MainActivity.generateErrorMessage(e);

													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Error importing punches").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });			
											        AlertDialog alert = builder.create();
											        alert.show();												        
												}																					
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();													
					} catch (Exception e) {
						Log.d("action_import_punches", "Error = " + Log.getStackTraceString(e));
					}
					break;
					
				case 2:
					try {
						LayoutInflater li = LayoutInflater.from(mMainActivity);
						View promptsView = li.inflate(R.layout.import_competition, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);

						alertDialogBuilder.setView(promptsView);

						final EditText importCompetitorsInput = (EditText) promptsView.findViewById(R.id.import_competition_input);

						alertDialogBuilder
								.setCancelable(false)
								.setPositiveButton("Add",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,	int id) {		
												try {
													CompetitionParser competitionParser = new CompetitionParser();
													competitionParser.parseCompetition(importCompetitorsInput.getText().toString());												
													
													((MainActivity)mMainActivity).competition.getCompetitors().clear();
													((MainActivity)mMainActivity).competition = new Competition();
													
													((MainActivity)mMainActivity).competition.setCompetitionName(competitionParser.getCompetitionName());
													((MainActivity)mMainActivity).competition.addNewTrack(competitionParser.getTrack());
													
													for (Competitor competitorObject : competitionParser.getCompetitors()) {													
														((MainActivity)mMainActivity).competition.addCompetitor(competitorObject.getName(), competitorObject.getCardNumber(), "", "", 0, 0);
													}
													
													for (Card cardObject : competitionParser.getCards()) {		
														if (cardObject.getPunches().size() > 0)
														{
															if (cardObject.getCardNumber() != 0) {
																((MainActivity)mMainActivity).competition.processNewCard(cardObject);
															}
														}
													}
													
													String statusMsg = competitionParser.getStatus();
													((MainActivity)mMainActivity).updateFragments();
													
													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Import competition status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });
											 
											        AlertDialog alert = builder.create();
											        alert.show();																					
												} catch (Exception e) {
													String statusMsg = MainActivity.generateErrorMessage(e);

													AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
											        builder.setIcon(android.R.drawable.ic_dialog_alert);
											        builder.setMessage(statusMsg).setTitle("Error importing Competition").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener()
											        {
											            public void onClick(DialogInterface dialog, int which) {}
											        });		
											        AlertDialog alert = builder.create();
											        alert.show();	
												}																								
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();														
					} catch (Exception e) {
						Log.d("action_export_competition", "Error = " + Log.getStackTraceString(e));
					}
					break;						
				}		    					
		    }});
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item)
		    {
		    	dialog.dismiss();
		    }});	            
    	builder.setSingleChoiceItems(items,0, mImportOnClickListener);    	
		AlertDialog loadDialog = builder.create();
        loadDialog.show();        
    }
}

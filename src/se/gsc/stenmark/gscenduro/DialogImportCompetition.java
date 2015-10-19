package se.gsc.stenmark.gscenduro;

import java.io.BufferedReader;
import java.io.StringReader;

import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogImportCompetition {

	private MainActivity mMainActivity;

	public DialogImportCompetition(MainActivity MainActivity) {
		mMainActivity = MainActivity;
	}

	public void createImportCompetitionDialog() {
		LayoutInflater li = LayoutInflater.from(mMainActivity);
		View promptsView = li.inflate(R.layout.competition_import, null);

		final EditText importCompetitionInput = (EditText) promptsView.findViewById(R.id.import_competition_input);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMainActivity);
		alertDialogBuilder.setTitle("Import competition");
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setPositiveButton("Import", null);
		alertDialogBuilder.setNegativeButton("Cancel", null);

		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (importCompetitionInput.length() == 0) {
					Toast.makeText(mMainActivity, "No competitors was supplied", Toast.LENGTH_LONG).show();
					return;
				}

				try {
					Boolean nameAdded = false;
					Boolean dateAdded = false;
					Boolean typeAdded = false;
					Boolean stageAdded = false;
					Boolean competitorsAdded = false;
					Boolean punchesAdded = false;
					int type = 0;
					String importType = "";
					String importData = "";
					String errorText = "";					

					BufferedReader bufReader = new BufferedReader(new StringReader(importCompetitionInput.getText().toString()));
					String line = null;
					while ((line = bufReader.readLine()) != null) {

						if (line.equals("[/Name]")) {
							importType = "";
							if (importData.length() > 0) {
								nameAdded = true;
							}
						} else if (line.equals("[/Date]")) {
							importType = "";
							if (importData.length() > 0) {
								dateAdded = true;
							}
						} else if (line.equals("[/Type]")) {
							importType = "";
							if (importData.length() > 0) {
								if (importData.matches("\\d+")) {
									type = Integer.parseInt(importData);
									if ((type == 0) || (type == 1)) {
										typeAdded = true;
									}
								}
							}
						} else if (line.equals("[/Stages]")) {
							importType = "";
							errorText += mMainActivity.competition.getStages().checkStagesData(importData, type);
							stageAdded = true;
						} else if (line.equals("[/Competitors]")) {
							importType = "";
							errorText += mMainActivity.competition.getCompetitors().checkImportCompetitors(importData, false, type);
							competitorsAdded = true;
						} else if (line.equals("[/Punches]")) {
							importType = "";
							// errorText += mMainActivity.competition.getCompetitors().checkimportPunches(importData);														
							punchesAdded = true;
						} else if (importType.length() > 0) {
							importData += line;
							if ((importType.equals("[Competitors]")) || (importType.equals("[Punches]"))) {
								importData += "\n";
							}
						} else if ((line.equals("[Name]"))
								|| (line.equals("[Date]"))
								|| (line.equals("[Type]"))
								|| (line.equals("[Stages]"))
								|| (line.equals("[Competitors]"))
								|| (line.equals("[Punches]"))) {
							importType = line;
							importData = "";
						}
					}

					if (!nameAdded) {
						errorText += "No name\n";
					}

					if (!dateAdded) {
						errorText += "No date\n";
					}

					if (!typeAdded) {
						errorText += "No type\n";
					}

					if (!stageAdded) {
						errorText += "No stage\n";
					}
					
					if (!competitorsAdded && punchesAdded) {
						errorText += "Can't add punches without adding competitors\n";
					}
					
					if (errorText.length() != 0) {
						Toast.makeText(mMainActivity, errorText, Toast.LENGTH_LONG).show();
						return;
					}
				} catch (Exception e) {
					String errorText = MainActivity.generateErrorMessage(e);

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setMessage(errorText).setTitle("Error importing Competition").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}

				try {
					String importType = "";
					String importData = "";
					mMainActivity.competition = new Competition();

					BufferedReader bufReader = new BufferedReader(new StringReader(importCompetitionInput.getText().toString()));
					String line = null;
					while ((line = bufReader.readLine()) != null) {

						if (line.equals("[/Name]")) {
							importType = "";
							mMainActivity.competition.setCompetitionName(importData);
						} else if (line.equals("[/Date]")) {
							importType = "";
							mMainActivity.competition.setCompetitionDate(importData);
						} else if (line.equals("[/Type]")) {
							importType = "";
							mMainActivity.competition.setCompetitionType(Integer.parseInt(importData));
						} else if (line.equals("[/Stages]")) {
							importType = "";
							mMainActivity.competition.getStages().importStages(importData);
						} else if (line.equals("[/Competitors]")) {
							importType = "";
							mMainActivity.competition.getCompetitors().importCompetitors(importData, false, mMainActivity.competition.getCompetitionType());
						} else if (line.equals("[/Punches]")) {
							importType = "";
							mMainActivity.competition.getCompetitors().importPunches(importData, mMainActivity.competition.getStages(), mMainActivity.competition.getCompetitionType());
						} else if (importType.length() > 0) {
							importData += line;
							if ((importType.equals("[Competitors]")) || (importType.equals("[Punches]"))) {
								importData += "\n";
							}
						} else if ((line.equals("[Name]"))
								|| (line.equals("[Date]"))
								|| (line.equals("[Type]"))
								|| (line.equals("[Stages]"))
								|| (line.equals("[Competitors]"))
								|| (line.equals("[Punches]"))) {
							importType = line;
							importData = "";
						}
					}

					mMainActivity.competition.calculateResults();
					mMainActivity.updateFragments();

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setMessage("Competition added").setTitle("Import competition status").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});

					AlertDialog alert = builder.create();
					alert.show();
				} catch (Exception e) {
					String errorText = MainActivity.generateErrorMessage(e);

					AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setMessage(errorText).setTitle("Error importing Competition").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}

				alertDialog.dismiss();
			}
		});
	}
}

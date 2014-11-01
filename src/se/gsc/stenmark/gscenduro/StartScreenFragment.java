package se.gsc.stenmark.gscenduro;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.SiMessage;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartScreenFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private MainActivity mainActivity;
	OnNewCardListener newCardCallback;
	public SiDriver siDriver = null;

	public static long lastCalltime;
	public static int disconnectCounter;
	public static boolean disconected;

	// Container Activity must implement this interface
	public interface OnNewCardListener {
		public void onNewCard(Card card);
	}
	
	public void setActivity( MainActivity mainActivity){
		this.mainActivity = mainActivity;
		MainApplication.startScreenFragment = this;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.startScreenFragment = null;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			newCardCallback = (OnNewCardListener) activity;
		} catch (ClassCastException e) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
			throw new ClassCastException(activity.toString()
					+ " must implement OnNewCardListener");
		}
	}

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static StartScreenFragment getInstance(int sectionNumber,
			MainActivity mainActivity) {
		StartScreenFragment fragment = null;
		fragment = new StartScreenFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		fragment.mainActivity = mainActivity;
		return fragment;

	}

	public StartScreenFragment() {
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTrackText();
		EditText nameOFCompEdit = (EditText) getView().findViewById(R.id.editSaveLoadComp);
		nameOFCompEdit.setText(mainActivity.competition.competitionName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		lastCalltime = System.currentTimeMillis();
		disconnectCounter = 0;
		disconected = true;

		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

		Button connectButton = (Button) rootView.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);
					String connectMsg = connectToSiMaster();
					statusTextView.setText(connectMsg);
					disconected = false;
					disconnectCounter = 0;
					statusTextView = null;
					new SiCardListener().execute(siDriver);
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button addTrackButton = (Button) rootView.findViewById(R.id.addTrackButton);
		addTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText newTrack = (EditText) getView().findViewById(R.id.editTrackDefinition);
					 mainActivity.competition.addNewTrack( (newTrack.getText().toString()) );
					updateTrackText();
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity	.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button addCompetitorButton = (Button) rootView.findViewById(R.id.addCompetitorButton);
		addCompetitorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText competitorName = (EditText) getView().findViewById(R.id.editCompetitorName);
					EditText cardNumber = (EditText) getView().findViewById( R.id.editCardNumber);
					mainActivity.competition.addCompetitor(competitorName.getText().toString(), cardNumber.getText().toString() );
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity
							.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}

			}
		});

		Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText nameOFCompToSave = (EditText) getView().findViewById(R.id.editSaveLoadComp);
					String compName = nameOFCompToSave.getText().toString();
					if (compName.isEmpty()) {
						PopupMessage dialog = new PopupMessage("No competition name was supplied");
						dialog.show(getFragmentManager(), "popUp");
						return;
					}
					mainActivity.competition.competitionName = compName;
					mainActivity.competition.saveSessionData(compName);
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button loadButton = (Button) rootView.findViewById(R.id.loadButton);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					EditText nameOFCompToLoad = (EditText) getView().findViewById(R.id.editSaveLoadComp);
					mainActivity.competition = Competition.loadSessionData( nameOFCompToLoad.getText().toString() );
					updateTrackText();
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button listButton = (Button) rootView.findViewById(R.id.listLoadedButton);
		listButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					TextView statusText = (TextView) getView().findViewById( R.id.cardInfoTextView);
					statusText.setText("Existing competitions \n");
					statusText.append( CompetitionHelper.getSavedCompetitions() );

				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}
		});

		Button newButton = (Button) rootView.findViewById(R.id.newCompButton);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mainActivity.competition = new Competition();
					updateTrackText();
					if (mainActivity.getResultListFragment() != null) {
						mainActivity.getResultListFragment().updateResultList();
					}
				} catch (Exception e) {
					PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
					dialog.show(getFragmentManager(), "popUp");
				}
			}

		});

		Button exportResultButton = (Button) rootView.findViewById(R.id.exportResultButton);
		exportResultButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					exportResult();
			}
		});

		return rootView;
	}

	private void exportResult() {
		try {
			TextView status = (TextView) getView().findViewById(R.id.cardInfoTextView);
			String exportResult = mainActivity.competition.exportResultAsCsv(this);
			status.setText(exportResult);	
						
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage( MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}
	}



	public void updateTrackText() {
		try {
			TextView trackInfoTextView = (TextView) getView().findViewById( R.id.trackInfoTextView);
			trackInfoTextView.setText("Current loaded Track: " + mainActivity.competition.getTrackAsString() );
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
		}

	}

	/** Called when the user clicks the Send button */
	public String connectToSiMaster() {
		try {
			String msg = "";
			siDriver = new SiDriver();
			if (siDriver.connectDriver((UsbManager) mainActivity.getSystemService(Context.USB_SERVICE))) {
				if (siDriver.connectToSiMaster()) {
					msg = "SiMain " + siDriver.stationId + " connected";
				} else {
					msg = "Failed ot connect SI master";
				}
			}
			if (siDriver.mode != SiMessage.STATION_MODE_READ_CARD) {
				msg = "SiMain is not configured as Reas Si";
				PopupMessage dialog = new PopupMessage(	msg + " Is configured as: "	+ SiMessage.getStationMode(siDriver.mode) );
				dialog.show(getFragmentManager(), "popUp");
			}

			return msg;
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");
			return "Fail";
		}
	}

	public void writeCard(Card card) {
		try {
			TextView cardText = (TextView) getView().findViewById(R.id.cardInfoTextView);
			if (card.cardNumber != 0) {
				cardText.setText(card.toString());
				cardText.append("\n" + card.errorMsg + "\n");
				newCardCallback.onNewCard(card);
			} else {
				cardText.append("\n" + card.errorMsg);
			}
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			} else {
				TextView statusTextView = (TextView) getView().findViewById(R.id.statusText);
				statusTextView.setText("Disconnected");
			}
		} catch (Exception e) {
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			}
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getFragmentManager(), "popUp");

		}
	}

	private class SiCardListener extends AsyncTask<SiDriver, Void, Card> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected Card doInBackground(SiDriver... siDriver) {
			try {
				Card cardData = new Card();
				while (true) {
					byte[] readSiMessage = siDriver[0].readSiMessage(100,
							50000, false);

					if (readSiMessage.length >= 1
							&& readSiMessage[0] == SiMessage.STX) {
						if (readSiMessage.length >= 2
								&& (readSiMessage[1] & 0xFF) == 0x66) {
							siDriver[0]
									.sendSiMessage(SiMessage.request_si_card6.sequence());
							cardData = siDriver[0].getCard6Data();

							siDriver[0].sendSiMessage(SiMessage.ack_sequence.sequence());

							return cardData;
						} else if (readSiMessage.length >= 2
								&& (readSiMessage[1] & 0xFF) == 0x46) {
							if (readSiMessage.length >= 3
									&& (readSiMessage[2] & 0xFF) == 0x4f) {
								cardData.errorMsg += "Card pulled out";
								return cardData;
							}

							siDriver[0].sendSiMessage(SiMessage.request_si_card5.sequence());
							cardData = siDriver[0].getCard5Data( mainActivity.competition );
							if (cardData == null) {
								cardData = new Card();
							}

							siDriver[0].sendSiMessage(SiMessage.ack_sequence.sequence());
							return cardData;
						} else {
							cardData.errorMsg += "not card6";
							return cardData;
						}

					} else {
						// Use this to check if we have been disconnected. If we
						// have many faulty read outs from the Driver, assume
						// disconenction.
						if (System.currentTimeMillis()
								- StartScreenFragment.lastCalltime < 1000) {
							disconnectCounter++;
						} else {
							disconnectCounter = 0;
						}
						if (disconnectCounter > 10) {
							disconected = true;
							siDriver[0].closeDriver();
						}
						StartScreenFragment.lastCalltime = System
								.currentTimeMillis();
						cardData.errorMsg += "not STX or timeout";
						return cardData;
					}
				}
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getFragmentManager(), "popUp");
				return new Card();
			}
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(Card newCard) {
			writeCard(newCard);
		}
	}

}

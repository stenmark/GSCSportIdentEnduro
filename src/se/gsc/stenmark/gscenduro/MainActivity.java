package se.gsc.stenmark.gscenduro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.util.List;
import java.util.Locale;

import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.SiMessage;
import se.gsc.stenmark.gscenduro.compmanagement.CompetitionHelper;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import se.gsc.stenmark.gscenduro.compmanagement.Stages;
import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Android Main class. This class is the creator of the GUI fragments via the SectionPageer and also implements OnCompetitionChanged
 * Interface used by the fragments to pass data between the Activity and fragments.
 * It does also handle the Application lifecycle by killing and creating the fragments when onPause and onResume are called. This is not working very well though...
 * And finally this class holds a separate thread for listening on the USB interface for new events once the SI main unit has been connected.
 * @author Andreas
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	public static final String PREF_NAME = "GSC_ENDURO_PREFERENCES";
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public Competition competition = null;
	public SiDriver siDriver = null;
	public static long lastCalltime;
	public static int disconnectCounter;
	public static boolean disconected;
	private String connectionStatus = "";


	public static String driverLayerErrorMsg;
		
	public String getConnectionStatus() {
		return connectionStatus;
	}	
	
	public void listPunches(int cardNumber) {		
		try{
			Intent punchListIntent = new Intent();		
			punchListIntent.setClass(this, PunchActivity.class);		
			
			if (competition.getCompetitors().getByCardNumber(cardNumber).getCard() == null) {
				competition.getCompetitors().getByCardNumber(cardNumber).processCard(new Card(), this.competition.getStages(), this.competition.getCompetitionType());				
			}			

			Stages stages = new Stages( competition.getStages() );
			
			Bundle bundle = new Bundle();
			bundle.putSerializable("Card", competition.getCompetitors().getByCardNumber(cardNumber).getCard());
			bundle.putSerializable("Stages", stages);
			punchListIntent.putExtras(bundle);
			
			startActivityForResult(punchListIntent, 2);		
		}
		catch( Exception e) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e));
			dialog.show( getSupportFragmentManager(), "popUp");
	
		}
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// check if the request code is same as what is passed here it is 2
		if (requestCode == 2) {		
			Card updatedCard = new Card();
	    	updatedCard = (Card) data.getExtras().getSerializable("updateCard");
	    		    	
	    	if (updatedCard.getCardNumber() != 0) {	    		
	    		this.competition.processNewCard(updatedCard, true);
	    		Toast.makeText(this, "Card updated", Toast.LENGTH_LONG).show();
	    	}   		    
		}

		updateFragments();
	}
	
    public void updateFragments() {
     	SectionsPagerAdapter mAdapter = ((SectionsPagerAdapter)mViewPager.getAdapter());
     	
		ResultsFragment resultsFragment = (ResultsFragment)mAdapter.getRegisteredFragment(1);
		if (resultsFragment != null) {
			if (resultsFragment.getResultAdapter() != null) {
				resultsFragment.getResultAdapter().updateResult();				
			}
			if (resultsFragment.getResultLandscapeAdapter() != null) {				
				resultsFragment.getResultLandscapeAdapter().updateResultLandscape();
			}
		}

		CompetitorsFragment competitorsFragment = (CompetitorsFragment)mAdapter.getRegisteredFragment(2);
		if (competitorsFragment != null){
			if (competitorsFragment.getListCompetitorAdapter() != null) {
				competitorsFragment.getListCompetitorAdapter().updateCompetitors();
			}
		}     	

		StatusFragment statusFragment = (StatusFragment)mAdapter.getRegisteredFragment(0);
		if (statusFragment != null) {
			statusFragment.updateCompetitionStatus();
		}
	}	

    /**
	 * Disconnect the SI main unit and save all competition session data to disc.
	 */
	@Override
	protected void onPause() {
		try {
			super.onPause();
			disconected = true;
			if( competition != null){
				AndroidHelper.saveSessionData(null,competition);
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();

	}
	/**
	 * A lot of template code to get the sectorPager going. Created automagically by eclipse.
	 * Creates an empty competion object, later to be populated by onResume()
	 * Initiates the local variables.
	 * Also set handles to the mainActivity for all the fragments. This is not working very well and is a temporary solution. (just like onResume)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			
			lastCalltime = System.currentTimeMillis();
			disconnectCounter = 0;
			disconected = true;
			connectionStatus = "Disconnected";
			
			setContentView(R.layout.main_activity);

			try {
				competition = AndroidHelper.loadSessionData(null);				
			} 
			//Version missmatch, dont warn the user. Just create a new empty Competition and continue.
			catch( InvalidClassException e1){
				competition = new Competition();
			}
			catch (Exception e2) {
				competition = new Competition();
				PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e2));
				dialog.show(getSupportFragmentManager(), "popUp");
			}		
						
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Create the adapter that will return a fragment for each of the
			// three primary sections of the activity.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					actionBar.setSelectedNavigationItem(position);
				}
			});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title
				// defined by
				// the adapter. Also specify this Activity object, which
				// implements
				// the TabListener interface, as the callback (listener) for
				// when this tab is selected.
				actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
			}				
				
			Uri data = getIntent().getData();
			if(data!=null) {
				String importResult = "";
				if(ContentResolver.SCHEME_CONTENT.equals(data.getScheme())) {

					ContentResolver resolver = getContentResolver();
				    InputStream input = resolver.openInputStream(data);
				    if(input != null){
				        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				        String line = "";
				        String inputData = "";
				        while ((line = reader.readLine()) != null) {	
				        	inputData += line +"\n";
				        }
				        importResult = competition.getCompetitors().importCompetitors(inputData, true, competition.getCompetitionType(), false);
				        competition.calculateResults();
				    }
				}
				updateFragments();
				
				if( importResult.isEmpty() ){
					PopupMessage dialog = new PopupMessage("Imported competitors from .gsc file succesfully!");
					dialog.show(getSupportFragmentManager(), "popUp");
				}
				else{
					PopupMessage dialog = new PopupMessage("Importing competitors from .gsc file failed\n" + importResult );
					dialog.show(getSupportFragmentManager(), "popUp");
				}
				
			}
				
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	/** Called when the user clicks the Connect button 
	 * Connects to the SI main Unit over USB with the SiDriver USB/Serial interface.
	 * Checks that connection was successful and initiates the supervision counters and connectionStatus.
	 * Also checks that the SiDriver is in Read mode (to avoid mistakenly clear SI pins)
	 * Finally it starts the SiCardListener, a separate thread for SI events. The thread listens to events from the SI Main unit and activates callbacks when SI cards are read.
	 * 
	 * */
	public void connectToSiMaster() {
		try {
			driverLayerErrorMsg = "";
			siDriver = new SiDriver();
			UsbManager usbSystemService = (UsbManager) getSystemService(Context.USB_SERVICE);
			if( usbSystemService == null ){
				disconected = true;
				connectionStatus = "Could not get the USB system service from the Android system";
				PopupMessage dialog = new PopupMessage( connectionStatus + driverLayerErrorMsg );
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			}
			
			if (siDriver.connectDriver( usbSystemService ) ) {
				if (siDriver.connectToSiMaster()) {
					connectionStatus = "SiMain " + siDriver.stationId + " connected";
					disconected = false;
					disconnectCounter = 0;
					new SiCardListener().execute(siDriver);
				} else {
					connectionStatus = "Failed ot connect SI master";
					PopupMessage dialog = new PopupMessage( connectionStatus + driverLayerErrorMsg );
					dialog.show(getSupportFragmentManager(), "popUp");
					disconected = true;
					return;
				}
			}
			else{			
				connectionStatus = "Could not connect to the USB service";
				PopupMessage dialog = new PopupMessage( connectionStatus + driverLayerErrorMsg );
				dialog.show(getSupportFragmentManager(), "popUp");
				disconected = true;
				return;
			}
			
			if (siDriver.mode != SiMessage.STATION_MODE_READ_CARD) {
				connectionStatus = "SiMain is not configured as Reas Si";
				PopupMessage dialog = new PopupMessage(	connectionStatus + " Is configured as: " + siDriver.mode + "  :  "	+ SiMessage.getStationMode(siDriver.mode) );
				dialog.show(getSupportFragmentManager(), "popUp");
				disconected = true;
			}
		} catch (Exception e) {
			connectionStatus = "Unknown connection problem";
			PopupMessage dialog = new PopupMessage(driverLayerErrorMsg + "\n" + MainActivity.generateErrorMessage(e) );
			dialog.show(getSupportFragmentManager(), "popUp");
			disconected = true;
		}
	}

	/**
	 * Called by SiListener Callback when SI cards are read by the SI main unit.
	 * It takes a Card object and updates the GUI in the different SectionAdapter fragments.
	 * Some uncertainty on how well this works, is it always working to update fragments if they are not null?
	 * @param card
	 */
	public void writeCard(Card card) {
		try {
			if (card.getCardNumber() != 0) {
				String cardStatus = competition.processNewCard(card, true);			
				Toast.makeText(this, cardStatus, Toast.LENGTH_LONG).show();
				competition.lastReadCards.add(cardStatus);		
			} 
			//The Listener dies once it has received one message, so kick it again to restart it
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			} else {
				connectionStatus = "Disconnected";				
		     	SectionsPagerAdapter mAdapter = ((SectionsPagerAdapter)mViewPager.getAdapter());		     	
		     	StatusFragment statusFragment = (StatusFragment)mAdapter.getRegisteredFragment(0);
				if (statusFragment != null) {
					statusFragment.updateConnectText();
				}								
			}
		} catch (Exception e) {
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			}
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
		updateFragments();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
				
		switch(id) {
		case R.id.action_new:			
			DialogNewCompetition newCompetitionDialog = new DialogNewCompetition(this);
			newCompetitionDialog.createNewCompetitionDialog();			
			return true;
			
		case R.id.action_load:			
			List<String> savedCompetitions = AndroidHelper.getSavedCompetitionsAsList();
			CompetitionOnClickListener competitionOnClickListener = new CompetitionOnClickListener(savedCompetitions);
			DialogSelectCompetition selectCompetitionDialog = new DialogSelectCompetition(savedCompetitions, competitionOnClickListener, this, competitionOnClickListener);
			selectCompetitionDialog.createSelectCompetitionDialog();
			return true;			    
							
		case R.id.action_save:
			DialogSaveCompetition saveCompetitionDialog = new DialogSaveCompetition(this);
			saveCompetitionDialog.createSaveCompetitionDialog();				
			return true;			
			
		case R.id.action_add_competitor:
			DialogAddCompetitor addCompetitorDialog = new DialogAddCompetitor(this);
			addCompetitorDialog.createAddCompetitorDialog();	
			return true;
			
		case R.id.action_settings:
	        Intent settingsIntent = new Intent();
	        settingsIntent.setClass(this, SettingsActivity.class);
	        startActivity(settingsIntent);			
			return true;
			
		case R.id.action_import:
			ImportOnClickListener importOnClickListener = new ImportOnClickListener();
			DialogSelectImport selectImportDialog = new DialogSelectImport(importOnClickListener, this, importOnClickListener);
			selectImportDialog.createImportDialog();	
			return true;	
			
		case R.id.action_export_as_csv:
			ExportOnClickListener exportOnClickListener = new ExportOnClickListener();
			DialogSelectExport selectExportDialog = new DialogSelectExport(exportOnClickListener, this, exportOnClickListener);
			selectExportDialog.createExportDialog();							
			return true;			

		case R.id.action_export_as_html:
			String resultList = CompetitionHelper.getResultsAsHtmlString(this.competition.getCompetitionName(), this.competition.getCompetitionDate(), this.competition.getStages(), this.competition.getCompetitors(), this.competition.getCompetitionType(), competition);
			try {
				AndroidHelper.exportString(this, resultList, "results", this.competition.getCompetitionName(), "htm");
			} catch (IOException e) {
				Log.d("action_export_as_html", "Error = " + Log.getStackTraceString(e));
			}						
			return true;				
			
		case R.id.action_export_as_image:			
			try{				
		     	SectionsPagerAdapter mAdapter = ((SectionsPagerAdapter)mViewPager.getAdapter());
		     	
				ResultsFragment resultListFragment = (ResultsFragment)mAdapter.getRegisteredFragment(1);
				if (resultListFragment != null){
					Bitmap resultBitmap = AndroidHelper.getWholeListViewItemsToBitmap(resultListFragment.getListView());
					File imageFile = AndroidHelper.writeImageToFile(competition.getCompetitionName() + "_results.png", resultBitmap);
					
					Intent mailIntent = new Intent(Intent.ACTION_SEND);
					mailIntent.setType("text/plain");
					mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
					mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enduro result image for " + competition.getCompetitionName());
					mailIntent.putExtra(Intent.EXTRA_TEXT   , imageFile.getName() + " result in attached file");
					Uri uri = Uri.fromFile( imageFile );
					mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(mailIntent, "Send mail"));
				}				
			} catch( Exception e) {
				PopupMessage dialog = new PopupMessage(	"You must put the Android unit in the \"RESULTS\" view and in Landscape orientation before you can you image export");
				dialog.show(getSupportFragmentManager(), "popUp");
			}
			
			return true;						
			
		default:
			return super.onOptionsItemSelected(item);
		}		
	}	
	
    public class CompetitionOnClickListener implements android.content.DialogInterface.OnClickListener {
    	public int which = 0;
    	
    	public CompetitionOnClickListener( List<String> savedCompetitions ) {
		}
    	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.which = which;
		}
    }		
    
    public class ImportOnClickListener implements android.content.DialogInterface.OnClickListener {
    	public int which = 0;
    	
    	public ImportOnClickListener() {
		}
    	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.which = which;
		}
    }	    
    
    public class ExportOnClickListener implements android.content.DialogInterface.OnClickListener {
    	public int which = 0;
    	
    	public ExportOnClickListener() {
		}
    	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.which = which;
		}
    }	    
    
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 * Sketchy code based on Autogenerated code. Creates all the GUI fragments and sends a handle to  the MainActivity to all the fragments.
	 * Is there a better way to setup the Fragments with a handle to the MainActivity?
	 * The fragments seem to have different lifecycle than the Activity. 
	 * Especially for layout changes i.e. flipping the screen. then fragments survive but activity is killed. Causes all sorts of problems.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
		
		public SectionsPagerAdapter(FragmentManager fm,
				MainActivity mainActivity) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null; 
			switch (position) {
			case 0:
				fragment = new StatusFragment();
				break;
			case 1:
				fragment = new ResultsFragment();
				break;
			case 2:
				fragment = new CompetitorsFragment();
				break;				
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment) super.instantiateItem(container,	position);
			registeredFragments.put(position, fragment);
			return fragment;
		}		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			registeredFragments.remove(position);
		}		
		
		public Fragment getRegisteredFragment(int position) {
			return registeredFragments.get(position);
		}		
	}

	public static String generateErrorMessage(Exception e) {
		String errorMessage = e.getMessage() + "\n";
		for (StackTraceElement element : e.getStackTrace()) {
			errorMessage += element.toString() + "\n";
		}
		return errorMessage;
	}
	
	/**
	 * Internal class to handle the SI Main unit events over USB/Serial.
	 * Uses Android AsyncTask to let Android launch this as a background task.
	 * Once an SI MainUnit event (i.e. read an SI card) has occured and been processed the Task is killed and has to be spawned again by the MainActivity.
	 * @author Andreas
	 *
	 */
	private class SiCardListener extends AsyncTask<SiDriver, Void, Card> {
		private static final boolean VERBOSE_LOGGING = true;

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 * The creator of the task supplies a handle to the siDriver.
		 * Use the siDriver to continuously listen for new events.
		 * When a new event is detected, check which event it was (i.e. read SiCard of type6)
		 * Validate the message and Create a new Card object and return it.
		 */
		protected Card doInBackground(SiDriver... siDriver) {
			try {
				while (true) {
					byte[] readSiMessage = siDriver[0].readSiMessage(100, 2000, new StringBuffer());

					if(disconected){
						//Log.d("SiCardListener", "Was disconnected");
						siDriver[0].closeDriver();
						return new Card();
					}
					
					//We got something and it was the STX (Start Transmission) symbol.
					if (readSiMessage.length >= 1 && readSiMessage[0] == SiMessage.STX) {
						if( VERBOSE_LOGGING ){
							String message = "";
							message += "Debug data for inserted card\n";
							for(int i = 0; i < readSiMessage.length; i++){
								message += "=0x" + SiDriver.byteToHex(readSiMessage[i]) + ", ";
							}
							message += "\n";
							LogFileWriter.writeLog("cardInserted", message);
						}
						
						//Check if the Magic byte 0x66 was received -> SiCard6 was read
						if (readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0x66) {
							LogFileWriter.writeLog("debugLog", "Detected SiCard6 with 0X66 byte\n");
							return siDriver[0].getCard6Data( VERBOSE_LOGGING );
						}
						//Backup detection for SiCard6. Seems like sometimes byte 1 is 0xE8, just like SIAC. But byte5 is 0x02 for Sicard6 (0x0F for SIAC)
						else if(readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0xE8 && (readSiMessage[5] & 0xFF) == 0x02){
							LogFileWriter.writeLog("debugLog", "Detected SiCard6 with 0XE8 byte and 0x02 byte on position 5\n");
							return siDriver[0].getCard6Data( VERBOSE_LOGGING );
						}
						//Check if the Magic byte 0xE8 was received -> SiCard9 (SIAC) was read
						else if(readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0xE8) {
							try{
								LogFileWriter.writeLog("debugLog", "Detected SIAC card with byte 0xE8\n");
								return siDriver[0].getSiacCardData( VERBOSE_LOGGING );
							}
							//Some special case handling. 
							//Seems like sometimes (maybe due to updated SI-MASTER) the SI-MASTER responds with 0xE8 also for SICARD6.
							//The SIAC readout will throw an exception if that is the case and then we blindly try a CARD6 decode instead
							catch( RuntimeException e){
								LogFileWriter.writeLog("debugLog", "Detected SiCard6 with unexpected exception from SIAC decode\n");
								return siDriver[0].getCard6Data( VERBOSE_LOGGING );
							}
						
						//Check if the Magic byte 0x46 was received -> SiCard5 was read, seems to be 0x46 also for card pulled out event
						} else if (readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0x46) {
							
							//If the next bytes are 0xFF and =x4F it seems like this is magic bytes for card pulled out event, return an Empty Card
							if (readSiMessage.length >= 3 && (readSiMessage[2] & 0xFF) == 0x4f) {
								//Log.d("SiCardListener", "Card pulled out");
								return new Card();
							}

							//If it was not card pulled out it was an SiCard5
							siDriver[0].sendSiMessage(SiMessage.request_si_card5.sequence());
							Card cardData = siDriver[0].getCard5Data( competition, VERBOSE_LOGGING );
							if (cardData == null) {
								cardData = new Card();
							}

							siDriver[0].sendSiMessage(SiMessage.ack_sequence.sequence());
							return cardData;
						} else {
							if( VERBOSE_LOGGING ){
								String message = "";
								message += "Unkown Card detected\n";
								for(int i = 0; i < readSiMessage.length; i++){
									message += "=0x" + SiDriver.byteToHex(readSiMessage[i]) + ", ";
								}
								message += "\n";
								LogFileWriter.writeLog("unknownCard", message);
								if (readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) != 0xE7) {
									LogFileWriter.writeLog("debugLog", "Unknown card that did not contain 0XE7\n"+message);
								}
							}
					    	
							return new Card();
						}

					} else {
						// Use this to check if we have been disconnected. If we
						// have many faulty read outs from the Driver in a short time span, 
						// assume disconnection.
						if (System.currentTimeMillis() - MainActivity.lastCalltime < 1000) {
							disconnectCounter++;
						} else {
							disconnectCounter = 0;
						}
						if (disconnectCounter > 10) {
							disconected = true;
							siDriver[0].closeDriver();
						}
						MainActivity.lastCalltime = System.currentTimeMillis();
						//Log.d("SiCardListener", "not STX or timeout");
						return new Card();
					}
				}
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
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

package se.gsc.stenmark.gscenduro;

import java.io.FileNotFoundException;
import java.util.Locale;

import se.gsc.stenmark.gscenduro.StartScreenFragment.OnCompetitionChanged;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.SiMessage;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Android Main class. This class is the creator of the GUI fragments via the SectionPageer and also implements OnCompetitionChanged
 * Interface used by the fragments to pass data between the Activity and fragments.
 * It does also handle the Application lifecycle by killing and creating the fragments when onPause and onResume are called. This is not working very well though...
 * And finally this class holds a separate thread for listening on the USB interface for new events once the SI main unit has been connected.
 * @author Andreas
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener, OnCompetitionChanged {
	public static final String PREF_NAME = "GSC_ENDURO_PREFERNCES";
	
	public String msg = "";
	public Competition competition = null;
	public SiDriver siDriver = null;
	public static long lastCalltime;
	public static int disconnectCounter;
	public static boolean disconected;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	public void updateFragments() {
		if (mSectionsPagerAdapter.compMangementFragment != null
				&& mSectionsPagerAdapter.compMangementFragment instanceof CompMangementFragment) {
			mSectionsPagerAdapter.compMangementFragment.FetchItems();
		}
		if (mSectionsPagerAdapter.resultListFragment != null
				&& mSectionsPagerAdapter.resultListFragment instanceof ResultListFragment) {
			mSectionsPagerAdapter.resultListFragment.FetchItems();
		}
		
		try {
			competition.saveSessionData( null );
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}		
	}
	
	public void onNewCard(Card card) {
		try {
			if (mSectionsPagerAdapter.resultListFragment != null) {
				mSectionsPagerAdapter.resultListFragment.displayNewCard(card);
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	@Override
	public void onCompetitionChanged() {
	}

	/**
	 * Disconnect the SI main unit and save all competition session data to disc.
	 */
	@Override
	protected void onPause() {
		try {
			super.onPause();
			disconected = true;
		    SharedPreferences settings = getSharedPreferences(MainActivity.PREF_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString("connectionStatus", "Disconnected");
		    editor.commit();
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
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
			
			setContentView(R.layout.activity_main);

			try {
				competition = Competition.loadSessionData(null);
				
			} catch (FileNotFoundException e) {
				competition = new Competition();
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			}		
			
			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Create the adapter that will return a fragment for each of the
			// three
			// primary sections of the activity.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			// When swiping between different sections, select the corresponding
			// tab. We can also use ActionBar.Tab#select() to do this if we have
			// a reference to the Tab.
			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
				// when
				// this tab is selected.
				actionBar.addTab(actionBar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this));
			}			
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
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
	public String connectToSiMaster() {
		try {
			String msg = "";
			siDriver = new SiDriver();
			if (siDriver.connectDriver((UsbManager) getSystemService(Context.USB_SERVICE))) {
				if (siDriver.connectToSiMaster()) {
					msg = "SiMain " + siDriver.stationId + " connected";
					disconected = false;
					disconnectCounter = 0;
					new SiCardListener().execute(siDriver);
				} else {
					msg = "Failed ot connect SI master";
					disconected = true;
				}
			}
			if (siDriver.mode != SiMessage.STATION_MODE_READ_CARD) {
				msg = "SiMain is not configured as Reas Si";
				PopupMessage dialog = new PopupMessage(	msg + " Is configured as: "	+ SiMessage.getStationMode(siDriver.mode) );
				dialog.show(getSupportFragmentManager(), "popUp");
				disconected = true;
			}

			return msg;
		} catch (Exception e) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getSupportFragmentManager(), "popUp");
			disconected = true;
			return "Fail";
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
			//TODO: Move this to something smarter
//			TextView cardText = (TextView) findViewById(R.id.cardInfoTextView);
			//Update the result fragment with the new card.
			//TODO: Investigate how well this works, can you always call GUI updating methods from here?
			//Sometimes the fragment seems to be null, for unknown reasons.
			if (card.cardNumber != 0) {
//				cardText.setText(card.toString());
//				cardText.append("\n" + card.errorMsg + "\n");
				if (mSectionsPagerAdapter.resultListFragment != null) {
					mSectionsPagerAdapter.resultListFragment.displayNewCard(card);
				}
			} 
//			else {
//				cardText.append("\n" + card.errorMsg);
//			}
			//The Listener dies once it has received once message, so kick it again to restart it
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			} else {
				mSectionsPagerAdapter.startScreenFragment.connectionStatus = "Disconnected";
				mSectionsPagerAdapter.startScreenFragment.updateConnectText();
			}
		} catch (Exception e) {
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			}
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getSupportFragmentManager(), "popUp");

		}
	}
	
	/**
	 * Autocrated template code
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Autocrated template code
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Autocrated template code
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	/**
	 * Autocrated template code
	 */
	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * Autocrated template code
	 */
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
		public StartScreenFragment startScreenFragment = null;
		public ResultListFragment resultListFragment = null;
		public CompMangementFragment compMangementFragment = null;

		public SectionsPagerAdapter(FragmentManager fm,
				MainActivity mainActivity) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				startScreenFragment = StartScreenFragment.getInstance(0);
				return startScreenFragment;
			case 1:
				resultListFragment = ResultListFragment.getInstance(1);
				return resultListFragment;
			case 2:
				compMangementFragment = CompMangementFragment.getInstance(2);
				return compMangementFragment;
			}

			return null;
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
				Card cardData = new Card();
				while (true) {
					byte[] readSiMessage = siDriver[0].readSiMessage(100,
							50000, false);

					//We got something and it was the STX (Start Transmistion) symbol.
					if (readSiMessage.length >= 1 && readSiMessage[0] == SiMessage.STX) {
						
						//Check if the Magic byte 0x66 was received -> SiCard6 was read
						if (readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0x66) {
							siDriver[0].sendSiMessage(SiMessage.request_si_card6.sequence());
							cardData = siDriver[0].getCard6Data();
							siDriver[0].sendSiMessage(SiMessage.ack_sequence.sequence());
							return cardData;
							
						//Check if the Magic byte 0x46 was received -> SiCard5 was read, seems to be 0x46 also for card pulled out event
						} else if (readSiMessage.length >= 2 && (readSiMessage[1] & 0xFF) == 0x46) {
							
							//If the next bytes are 0xFF and =x4F it seems like this is magic bytes for card pulled out event, return an Empty Card
							if (readSiMessage.length >= 3 && (readSiMessage[2] & 0xFF) == 0x4f) {
								cardData.errorMsg += "Card pulled out";
								return cardData;
							}

							//If it was not card pulled out it was an SiCard5
							siDriver[0].sendSiMessage(SiMessage.request_si_card5.sequence());
							cardData = siDriver[0].getCard5Data( competition );
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
						cardData.errorMsg += "not STX or timeout";
						return cardData;
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

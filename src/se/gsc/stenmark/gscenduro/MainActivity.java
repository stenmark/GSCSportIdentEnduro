package se.gsc.stenmark.gscenduro;

import java.io.FileNotFoundException;
import java.util.Locale;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.SporIdent.SiDriver;
import se.gsc.stenmark.gscenduro.SporIdent.SiMessage;
import se.gsc.stenmark.gscenduro.compmanagement.Competition;
import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
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

	public ResultListFragment getResultListFragment(){
		return mSectionsPagerAdapter.resultListFragment;
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
	protected void onPause() {
		try {
			super.onPause();
			competition.saveSessionData( null );
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	@Override
	protected void onStop() {
		try {
			super.onStop();
			competition.saveSessionData( null );
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}
		
	
	@Override
	protected void onResume() {
		try {
			super.onResume();
			try {
				competition = Competition.loadSessionData(null);
				
			} catch (FileNotFoundException e) {
				competition = new Competition();
			} catch (Exception e) {
				PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			}
			if( MainApplication.resultListFragment != null ){
				MainApplication.resultListFragment.setActivity(this);
			}
			if( MainApplication.startScreenFragment != null ){
				MainApplication.startScreenFragment.setActivity(this);
			}
			if( MainApplication.compMangementFragment != null ){
				MainApplication.compMangementFragment.setActivity(this);
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			
			lastCalltime = System.currentTimeMillis();
			disconnectCounter = 0;
			disconected = true;
			
			setContentView(R.layout.activity_main);

			competition = new Competition();

			// Set up the action bar.
			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Create the adapter that will return a fragment for each of the
			// three
			// primary sections of the activity.
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
			// startScreenFragment = mSectionsPagerAdapter.startScreenFragment;
			// resultListFragment = mSectionsPagerAdapter.resultListFragment;

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
			if( MainApplication.resultListFragment != null ){
				MainApplication.resultListFragment.setActivity(this);
			}
			if( MainApplication.startScreenFragment != null ){
				MainApplication.startScreenFragment.setActivity(this);
			}
			if( MainApplication.compMangementFragment != null ){
				MainApplication.compMangementFragment.setActivity(this);
			}
			
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	/** Called when the user clicks the Send button */
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

	public void writeCard(Card card) {
		try {
			TextView cardText = (TextView) findViewById(R.id.cardInfoTextView);
			if (card.cardNumber != 0) {
				cardText.setText(card.toString());
				cardText.append("\n" + card.errorMsg + "\n");
				if (mSectionsPagerAdapter.resultListFragment != null) {
					mSectionsPagerAdapter.resultListFragment.displayNewCard(card);
				}
			} else {
				cardText.append("\n" + card.errorMsg);
			}
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			} else {
				TextView statusTextView = (TextView) findViewById(R.id.statusText);
				statusTextView.setText("Disconnected");
			}
		} catch (Exception e) {
			if (!disconected) {
				new SiCardListener().execute(siDriver);
			}
			PopupMessage dialog = new PopupMessage(	MainActivity.generateErrorMessage(e));
			dialog.show(getSupportFragmentManager(), "popUp");

		}
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public StartScreenFragment startScreenFragment = null;
		public ResultListFragment resultListFragment = null;
		public CompMangementFragment compMangementFragment = null;
		private MainActivity mainActivity = null;

		public SectionsPagerAdapter(FragmentManager fm,
				MainActivity mainActivity) {
			super(fm);
			this.mainActivity = mainActivity;
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				startScreenFragment = StartScreenFragment.getInstance(
						position + 1, mainActivity);
				return startScreenFragment;
			case 1:
				resultListFragment = ResultListFragment.getInstance(
						position + 1, mainActivity);
				return resultListFragment;
			case 2:
				compMangementFragment = CompMangementFragment.getInstance(
						position + 1, mainActivity);
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

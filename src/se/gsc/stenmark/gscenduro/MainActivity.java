package se.gsc.stenmark.gscenduro;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.gsc.stenmark.gscenduro.StartScreenFragment.OnNewCardListener;
import se.gsc.stenmark.gscenduro.SporIdent.Card;
import se.gsc.stenmark.gscenduro.compmanagement.Competitor;
import se.gsc.stenmark.gscenduro.compmanagement.TrackMarker;

import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, OnNewCardListener {
	public String msg = "";
	public static List<TrackMarker> track = null;
	public ArrayList<Competitor> competitors = null;

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

	public void onNewCard(Card card) {
		try {
			if (mSectionsPagerAdapter.resultListFragment != null) {
				mSectionsPagerAdapter.resultListFragment.processNewCard(card);
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	public void saveSessionData(String competionName) {
		try {
			FileOutputStream fileOutputComp;
			FileOutputStream fileOutputTrack;
			try {
				if (competionName == null || competionName.isEmpty()) {
					fileOutputComp = MainApplication
							.getAppContext()
							.openFileOutput(
									StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE,
									Context.MODE_PRIVATE);
				} else {
					fileOutputComp = MainApplication.getAppContext()
							.openFileOutput(competionName + "_list",
									Context.MODE_PRIVATE);
				}

				ObjectOutputStream objStreamOutComp = new ObjectOutputStream(
						fileOutputComp);
				objStreamOutComp.writeObject(competitors);
				objStreamOutComp.close();

				if (track == null) {
					track = new ArrayList<TrackMarker>();
				}
				if (competionName == null || competionName.isEmpty()) {
					fileOutputTrack = MainApplication.getAppContext()
							.openFileOutput(
									StartScreenFragment.CURRENT_TRACK_FILE,
									Context.MODE_PRIVATE);
				} else {
					fileOutputTrack = MainApplication.getAppContext()
							.openFileOutput(competionName + "_track",
									Context.MODE_PRIVATE);
				}
				ObjectOutputStream objStreamOutTrack = new ObjectOutputStream(
						fileOutputTrack);
				objStreamOutTrack.writeObject(track);
				objStreamOutTrack.close();

			} catch (FileNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			} catch (IOException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			}
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}

	}

	public void loadSessionData(String competionName, boolean readFile) {
		try {
			TextView cardText = (TextView) findViewById(R.id.cardInfoTextView);
			cardText.setText("");
			if (readFile) {
				FileInputStream fileInputTrack = null;
				FileInputStream fileInputComp = null;
				try {
					if (competionName == null || competionName.isEmpty()) {
						fileInputComp = MainApplication
								.getAppContext()
								.openFileInput(
										StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE);
					} else {
						fileInputComp = MainApplication.getAppContext()
								.openFileInput(competionName + "_list");
					}
					ObjectInputStream objStreamInComp = new ObjectInputStream(
							fileInputComp);
					competitors = (ArrayList<Competitor>) objStreamInComp
							.readObject();
					objStreamInComp.close();

					if (competionName == null || competionName.isEmpty()) {
						fileInputTrack = MainApplication.getAppContext()
								.openFileInput(
										StartScreenFragment.CURRENT_TRACK_FILE);
					} else {
						fileInputTrack = MainApplication.getAppContext()
								.openFileInput(competionName + "_track");
					}
					ObjectInputStream objStreamInTrack = new ObjectInputStream(
							fileInputTrack);
					track = (List<TrackMarker>) objStreamInTrack.readObject();
					objStreamInTrack.close();
				} catch (FileNotFoundException e) {
					cardText.append("File not found: " + e.getMessage());
					PopupMessage dialog = new PopupMessage(
							MainActivity.generateErrorMessage(e));
					dialog.show(getSupportFragmentManager(), "popUp");
					return;
				} catch (IOException e) {
					cardText.append("IOException: " + e.getMessage());
					PopupMessage dialog = new PopupMessage(
							MainActivity.generateErrorMessage(e));
					dialog.show(getSupportFragmentManager(), "popUp");
					return;
				} catch (ClassNotFoundException e) {
					PopupMessage dialog = new PopupMessage(
							MainActivity.generateErrorMessage(e));
					dialog.show(getSupportFragmentManager(), "popUp");
					cardText.append("ClassNotFoundException: " + e.getMessage());
					return;
				}
			}

			// TODO: some fuck up with this global instances, need to get rid of
			// them
			// StartScreenFragment.instance.updateTrackText();
			TextView trackInfoTextView = (TextView) findViewById(R.id.trackInfoTextView);
			trackInfoTextView.setText("Current loaded Track: ");
			int i = 0;
			for (TrackMarker trackMarker : track) {
				i++;
				trackInfoTextView.append(", SS" + i + " Start: "
						+ trackMarker.start + " Finish: " + trackMarker.finish);
			}
			if (mSectionsPagerAdapter.resultListFragment != null) {
				mSectionsPagerAdapter.resultListFragment.updateResultList();
			}
			cardText.append("Loaded: ");

			for (Competitor comp : competitors) {
				cardText.append("Name: " + comp.name + " cardnum"
						+ comp.cardNumber + comp.card + "\n");
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
			saveSessionData(null);
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}

	@Override
	protected void onStop() {
		try {
			super.onStop();
			saveSessionData(null);
		} catch (Exception e1) {
			PopupMessage dialog = new PopupMessage(
					MainActivity.generateErrorMessage(e1));
			dialog.show(getSupportFragmentManager(), "popUp");
		}
	}
	
	@Override
	protected void onDestroy () {
		super.onDestroy();
//		if (mSectionsPagerAdapter.resultListFragment != null) {
//			FragmentManager fragmentManager = getSupportFragmentManager();
//			android.support.v4.app.FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
//			beginTransaction.remove(mSectionsPagerAdapter.resultListFragment).commit(); 
//			beginTransaction.remove(mSectionsPagerAdapter.startScreenFragment).commit(); 
//			beginTransaction.remove(mSectionsPagerAdapter.compMangementFragment).commit(); 
//		}
	}

	
	
	@Override
	protected void onResume() {
		try {
			super.onResume();
			try {
				FileInputStream fileInputComp = MainApplication
						.getAppContext()
						.openFileInput(
								StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE);
				ObjectInputStream objStreamInComp = new ObjectInputStream(
						fileInputComp);
				competitors = (ArrayList<Competitor>) objStreamInComp
						.readObject();
				objStreamInComp.close();

				FileInputStream fileInputTrack = MainApplication
						.getAppContext().openFileInput(
								StartScreenFragment.CURRENT_TRACK_FILE);
				ObjectInputStream objStreamInTrack = new ObjectInputStream(
						fileInputTrack);
				track = (List<TrackMarker>) objStreamInTrack.readObject();
				objStreamInTrack.close();
			} catch (FileNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			} catch (IOException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
				dialog.show(getSupportFragmentManager(), "popUp");
				return;
			} catch (ClassNotFoundException e) {
				PopupMessage dialog = new PopupMessage(
						MainActivity.generateErrorMessage(e));
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
			setContentView(R.layout.activity_main);

			competitors = new ArrayList<Competitor>();

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

}

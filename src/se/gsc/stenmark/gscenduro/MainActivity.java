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

import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import	android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, OnNewCardListener {
	public static SiDriver siDriver = null;
	public static UsbManager usbManager; 
	public String msg = "";
	public static List<TrackMarker> track= null;
	public static ArrayList<Competitor> competitors = null;
	public static MainActivity instance = null;
	
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

	public void onNewCard(Card card){
//		ResultListFragment fragment = (ResultListFragment) getSupportFragmentManager().findFragmentById(R.id.pager);
		if(mSectionsPagerAdapter.resultListFragment != null ){
			mSectionsPagerAdapter.resultListFragment.processNewCard(card);
		}
	}
	
	public void saveSessionData( String competionName){
    	FileOutputStream fileOutputComp;
    	FileOutputStream fileOutputTrack;
		try {
			if( competionName == null || competionName.isEmpty() ){
				fileOutputComp = MainApplication.getAppContext().openFileOutput(StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE, Context.MODE_PRIVATE);
			}
			else{
				fileOutputComp = MainApplication.getAppContext().openFileOutput(competionName+"_list", Context.MODE_PRIVATE);
			}
			
			ObjectOutputStream objStreamOutComp = new ObjectOutputStream(fileOutputComp);  
			objStreamOutComp.writeObject(competitors);
			objStreamOutComp.close();	 
			
			if(track == null){
				track = new ArrayList<TrackMarker>();
			}
			if( competionName == null || competionName.isEmpty() ){
				fileOutputTrack = MainApplication.getAppContext().openFileOutput(StartScreenFragment.CURRENT_TRACK_FILE, Context.MODE_PRIVATE);
			}
			else{
				fileOutputTrack = MainApplication.getAppContext().openFileOutput(competionName+"_track", Context.MODE_PRIVATE);
			}
			ObjectOutputStream objStreamOutTrack = new ObjectOutputStream(fileOutputTrack);  
			objStreamOutTrack.writeObject(track);
			objStreamOutTrack.close();	
			
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	 
	}
	
	public void loadSessionData(String competionName, boolean readFile){
	try{
	   	 TextView cardText = (TextView) findViewById(R.id.cardInfoTextView);
	   	 cardText.setText("" );
	   	if( readFile ){
	   	FileInputStream fileInputTrack = null;
	   	FileInputStream fileInputComp = null;
	   	 try {
			 	if( competionName == null || competionName.isEmpty() ){
			 		fileInputComp = MainApplication.getAppContext().openFileInput(StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE);
			 	}
			 	else{
			 		fileInputComp = MainApplication.getAppContext().openFileInput(competionName+"_list");
			 	}
				ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
				competitors = (ArrayList<Competitor>) objStreamInComp.readObject();
				objStreamInComp.close();
				
				if( competionName == null || competionName.isEmpty() ){
					fileInputTrack = MainApplication.getAppContext().openFileInput(StartScreenFragment.CURRENT_TRACK_FILE);
				}
				else{
					fileInputTrack = MainApplication.getAppContext().openFileInput(competionName+"_track");
				}
				ObjectInputStream objStreamInTrack = new ObjectInputStream(fileInputTrack);
				track = (List<TrackMarker>) objStreamInTrack.readObject();
				objStreamInTrack.close();
			} catch (FileNotFoundException e) {
				 cardText.append("File not found: " +e.getMessage() );
				 return;
			} catch (IOException e) {
				cardText.append("IOException: " +e.getMessage() );
				return;
			} catch (ClassNotFoundException e) {
				cardText.append("ClassNotFoundException: " +e.getMessage() );
				return;
			}
		}
	   	 
	   	 //TODO: some fuck up with this global instances, need to get rid of them
//	   	 StartScreenFragment.instance.updateTrackText();
		 TextView trackInfoTextView = (TextView) findViewById(R.id.trackInfoTextView);
		 trackInfoTextView.setText("Current loaded Track: " );
		 int i = 0;
		 for( TrackMarker trackMarker : MainActivity.track){
			 i++;
			 trackInfoTextView.append( ", SS" + i + " Start: " + trackMarker.start + " Finish: " + trackMarker.finish );
		 }
		 if(mSectionsPagerAdapter.resultListFragment != null ){
			 mSectionsPagerAdapter.resultListFragment.updateResultList();
		 }
	   	 cardText.append("Loaded: " );
	
	   	 for( Competitor comp : MainActivity.competitors ){
	   		 cardText.append("Name: " + comp.name + " cardnum" + comp.cardNumber  +
	   				 		comp.card + "\n");
	   	 }
		 
		}
		catch( Exception e1){
			TextView cardText = (TextView) findViewById(R.id.cardInfoTextView);
			cardText.append("Caught super exception " + e1.getMessage() + "\n" );
			for( StackTraceElement elem :  e1.getStackTrace()){
				cardText.append(elem.toString() + "\n");
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveSessionData(null);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		saveSessionData(null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			FileInputStream fileInputComp = MainApplication.getAppContext().openFileInput(StartScreenFragment.CURRENT_COMPETITIOR_LIST_FILE);
			ObjectInputStream objStreamInComp = new ObjectInputStream(fileInputComp);
			competitors = (ArrayList<Competitor>) objStreamInComp.readObject();
			objStreamInComp.close();

			FileInputStream fileInputTrack = MainApplication.getAppContext().openFileInput(StartScreenFragment.CURRENT_TRACK_FILE);
			ObjectInputStream objStreamInTrack = new ObjectInputStream(	fileInputTrack);
			track = (List<TrackMarker>) objStreamInTrack.readObject();
			objStreamInTrack.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (ClassNotFoundException e) {
			return;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		if(savedInstanceState == null){
			instance = this;
			usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			competitors = new ArrayList<Competitor>();
					
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//		startScreenFragment = mSectionsPagerAdapter.startScreenFragment;
//		resultListFragment = mSectionsPagerAdapter.resultListFragment;

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
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
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
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch( position){
				case 0: startScreenFragment = StartScreenFragment.getInstance(position + 1);
						return startScreenFragment;
				case 1: resultListFragment = ResultListFragment.getInstance(position + 1);
						return resultListFragment;
				case 2: return CompMangementFragment.getInstance(position + 1);		
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

}

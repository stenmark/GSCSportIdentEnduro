package se.gsc.stenmark.gscenduro;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

	private static Context context;

	public static StatusFragment statusScreenFragment = null;
	public static ResultsFragment resultListFragment = null;
	public static CompetitorsFragment compMangementFragment = null;	
	
	public void onCreate() {
		super.onCreate();
		MainApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MainApplication.context;
	}
}

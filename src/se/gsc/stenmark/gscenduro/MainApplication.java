package se.gsc.stenmark.gscenduro;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

	private static Context context;

	public static StartScreenFragment startScreenFragment = null;
	public static ResultListFragment resultListFragment = null;
	public static CompMangementFragment compMangementFragment = null;	
	
	public void onCreate() {
		super.onCreate();
		MainApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MainApplication.context;
	}
}

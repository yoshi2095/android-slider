package eu.flatworld.android.slider;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GA {
	public static int SCOPE_VISITOR = 1;
	public static int SCOPE_SESSION = 2;
	public static int SCOPE_PAGE = 3;
	
	static boolean enabled;
	static GoogleAnalyticsTracker TRACKER;
	
	public static void setEnabled(boolean v) {
		enabled = v;
	}
	
	public static void start(Context context) {
		if(enabled) {
			TRACKER = GoogleAnalyticsTracker.getInstance();
			TRACKER.startNewSession("UA-264586-7", 60, context);
		}
	}
	
	public static void stop() {
		if(enabled) {
			TRACKER.stopSession();
		}
	}
	
	public static void trackPageView(String value) {
		if(enabled) {
			TRACKER.trackPageView(value);
		}
	}
	
	public static void setCustomVar(int slot, String name, String value, int scope) {
		if(enabled) {
			TRACKER.setCustomVar(slot, name, value, scope);
		}
	}
}

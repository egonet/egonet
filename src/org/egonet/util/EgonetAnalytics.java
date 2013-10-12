package org.egonet.util;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boxysystems.jgoogleanalytics.FocusPoint;
import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;
import com.boxysystems.jgoogleanalytics.LoggingAdapter;

public final class EgonetAnalytics {

	private static final Object _lock = new Object();
	private static JGoogleAnalyticsTracker _tracker;
	private static Map<String,FocusPoint> _focalPoints;
	
	final private static Logger logger = LoggerFactory.getLogger(EgonetAnalytics.class);
	
	private EgonetAnalytics() {} // no constructor
	
	public static JGoogleAnalyticsTracker getTracker() {
		synchronized(_lock) {
			if(_tracker == null) {
				
				_tracker = new JGoogleAnalyticsTracker("Egonet",getExecutableName(),"UA-44770458-1");
				_focalPoints = new ConcurrentHashMap<String, FocusPoint>();

				_tracker.setLoggingAdapter(new LoggingAdapter() {

					@Override
					public void logError(String arg0) {
						logger.error(arg0);
					}

					@Override
					public void logMessage(String arg0) {
						logger.info(arg0);
					}});
			}
		}
		
		return _tracker;
	}
	
	private static final String getExecutableName() {
		String n = "unknown";
		try {
			    String path = EgonetAnalytics.class.getResource(EgonetAnalytics.class.getSimpleName() + ".class").getFile();
			    path = ClassLoader.getSystemClassLoader().getResource(path).getFile();
			    n = new File(path.substring(0, path.lastIndexOf('!'))).getName();
		} catch (Exception ex) {} // swallow
		
		return n;
	}
	
	public static void track(String area) {
		JGoogleAnalyticsTracker tracker = getTracker();
		
		if(_focalPoints == null)
			return;
		
		if(!_focalPoints.containsKey(area))
			_focalPoints.put(area, new FocusPoint(area));
		
		try {
			FocusPoint focusPoint = _focalPoints.get(area);
			tracker.trackAsynchronously(focusPoint);
		} catch (Exception ex) {} // swallow
	}
}

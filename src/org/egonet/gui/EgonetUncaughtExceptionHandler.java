package org.egonet.gui;

import java.lang.Thread.UncaughtExceptionHandler;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgonetUncaughtExceptionHandler implements UncaughtExceptionHandler {

	public void uncaughtException(Thread t, Throwable e) {
		try {
			getLogger().error("Uncaught exception", e);
		} 
		catch (Exception t2) {
			// nothing we can do here
			t2.printStackTrace();
		}

		
		try {
			ErrorInfo ei = new ErrorInfo(
					"An error has occurred", 
					e.getMessage() != null ? e.getMessage() : e.toString(), 
					null, 
					"EgonetUncaughtExceptionHandler", 
					e, 
					java.util.logging.Level.INFO, 
					null);
			JXErrorPane.showDialog(null, ei);
		}
		catch (Throwable t2) {
			// nothing we can do here
			t2.printStackTrace();
		}
	}
	
	private Logger logger = null;
	public synchronized Logger getLogger() {
		if(logger == null)
			logger = LoggerFactory.getLogger(EgonetUncaughtExceptionHandler.class);
		return logger;
	}

}

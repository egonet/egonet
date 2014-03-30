package org.egonet.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.egonet.util.EgonetAnalytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgonetRunner {

	public static void main(String[] args) throws Exception {
		final EgonetUncaughtExceptionHandler eueh = new EgonetUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(eueh);
		
		Logger logger = LoggerFactory.getLogger(EgonetRunner.class);
		
		EgonetAnalytics.track("application startup"); // track!

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Thread.currentThread().setUncaughtExceptionHandler(eueh);
			}});
		
		
		logger.debug("Configuring L&F to default system L&F");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		
		logger.info("EgonetRunner is building EgonetFrame");
		EgonetFrame egonetFrame = new EgonetFrame();
		egonetFrame.setSize(800, 600);
		egonetFrame.setExtendedState(JFrame.NORMAL | JFrame.MAXIMIZED_BOTH);
		egonetFrame.setVisible(true);
	}

}

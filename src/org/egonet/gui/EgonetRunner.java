package org.egonet.gui;

import javax.swing.UIManager;


public class EgonetRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			EgonetFrame egonetFrame = new EgonetFrame();
			egonetFrame.setSize(800, 600);
			egonetFrame.setVisible(true);
	}

}

package org.egonet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import org.egonet.gui.author.EgoFrame;
import org.egonet.gui.author.EgoNet;
import org.egonet.gui.interview.EgoClient;
import org.egonet.mdi.*;
import org.egonet.model.Shared;
import org.egonet.util.CatchingAction;
import org.egonet.util.EgonetAnalytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgonetFrame extends JFrame  {
	private final MDIDesktopPane desktop = new MDIDesktopPane();


	final private static Logger logger = LoggerFactory.getLogger(EgonetFrame.class);

	
	// TODO: handle the initial registration in a better way
	// TODO: establish the generic file menu of options

	private JMenuBar menuBar = new JMenuBar();

	private JMenu fileMenu = new JMenu("File");
	
	private JMenu helpMenu = new JMenu("Help");

	private JMenuItem newAuthoringTool = new JMenuItem("New Authoring Tool Window");

	private JMenuItem newInterviewingTool = new JMenuItem("New Interviewing Tool Window");
	
	private JScrollPane scrollPane = new JScrollPane();

	public EgonetFrame() throws Exception {
	    KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
	    focusManager.addPropertyChangeListener(new PropertyChangeListener() {
	      public void propertyChange(PropertyChangeEvent e) {
	        String prop = e.getPropertyName();
	        if(!prop.equals("focusOwner"))
	        	return;
	        
	        focusChanged(e);
	      }
	    });

		fileMenu.add(newAuthoringTool);
		fileMenu.add(newInterviewingTool);
		
		menuBar.add(fileMenu);
		menuBar.add(new WindowMenu(desktop));
		menuBar.add(helpMenu);

		final Window me = this;

		JMenuItem visitMenu = new JMenuItem("Visit website");
		helpMenu.add(visitMenu);
		visitMenu.addActionListener(new CatchingAction("jMenuHelpVisit") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				Desktop.getDesktop().browse(new URI("http://egonet.sf.net"));
			}
		});

		JMenuItem reportMenu = new JMenuItem("Report an issue");
		helpMenu.add(reportMenu);
		reportMenu.addActionListener(new CatchingAction("jMenuHelpReport") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				Desktop.getDesktop().browse(new URI("http://github.com/egonet/egonet/issues"));
			}
		});

		JMenuItem aboutMenu = new JMenuItem("About");
		helpMenu.add(aboutMenu);
		aboutMenu.addActionListener(new CatchingAction("jMenuHelpAbout") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				Shared.displayAboutBox(me);
			}
		});
		
		
		//fileMenu.add(newStudyMenu);
		setJMenuBar(menuBar);
		setTitle("EgoNet - Egocentric Network Analysis");
		scrollPane.getViewport().add(desktop);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				logger.info("Main frame is closed, closing all MDI frames");
				JInternalFrame[] frames = desktop.getAllFrames();
				
				for(JInternalFrame frame : frames) {
					try { 
						frame.setClosed(true);
						
						if(frame instanceof EgoFrame) {
							EgoFrame f = (EgoFrame)frame;
							f.jMenuFileExit_actionPerformed(null);
						}
					} 
					catch (PropertyVetoException ex) {
						logger.warn("Vetoed close on " + frame.toString(), ex);
					}
					finally {
						frame.dispose();
					}
				}
				
				EgonetAnalytics.track("application shutdown"); // track!
				System.exit(0);
			}
		});
		
		newAuthoringTool.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.add(new EgoNet().getFrame());
			}
		});
		
		newInterviewingTool.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				desktop.add(new EgoClient().getFrame());
			}
		});
		
		desktop.add(new EgoNet().getFrame());
		desktop.add(new EgoClient().getFrame());
	}
	
	
	
	protected void focusChanged(PropertyChangeEvent e) {
		
		// loop through all MDIChildFrames hanging on this component, 
		// if one is ancestor of the old or the new
		// fire an event!
		
		if(e.getOldValue() != null && e.getOldValue() instanceof Component)
		{
			Component oldValue = (Component)e.getOldValue();
			for(JInternalFrame frame : desktop.getAllFrames())
			{
				if(!(frame instanceof MDIChildFrame))
					continue;
				
				if(!frame.isAncestorOf(oldValue))
					continue;
				
				MDIChildFrame child = (MDIChildFrame)frame;
				child.focusDeactivated();
				break;
			}
		}
		
		if(e.getNewValue() != null && e.getNewValue() instanceof Component)
		{
			Component newValue = (Component)e.getNewValue();
			for(JInternalFrame frame : desktop.getAllFrames())
			{
				if(!(frame instanceof MDIChildFrame))
					continue;
				
				if(!frame.isAncestorOf(newValue))
					continue;
				
				MDIChildFrame child = (MDIChildFrame)frame;
				child.focusActivated();
				break;
			}
		}
	}
}



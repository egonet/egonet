package com.endlessloopsoftware.elsutils;

import java.awt.*;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/*******************************************************************************
 * Endless Loop Software Utilities Copyright (c) 2003, Endless Loop Software,
 * Inc.
 * 
 * @author $Author: schoaff $ @date $Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 * @version $Id: AboutBox.java 2 2006-03-09 14:42:46Z schoaff $
 * 
 * 
 * $Log: AboutBox.java,v $
 * Revision 1.1.1.1  2005/10/23 16:21:25  schoaff
 * Checking from IntelliJ IDEA
 *
 * Revision 1.1.1.1  2005/03/23 13:22:21  schoaff
 * New CVS Repository
 * Revision 1.3 2003/11/25 19:40:24 admin Cleaning up
 * AboutBox Making ListBuilder work on OSX by removing debugging graphport
 * 
 * Revision 1.2 2003/09/16 15:26:31 admin Improved DateChooser and PhoneBean
 * 
 * Revision 1.1.1.1 2003/09/13 18:12:13 admin Endless Loop Software Utilities
 * 
 * Revision 1.4 2003/06/27 12:36:43 admin Adding headers
 *  
 */
public class AboutBox extends JDialog implements ActionListener
{
	private final JButton		button1		= new JButton("OK");
	private final JLabel		imageLabel	= new JLabel();
	private final JFrame		parent;
	private final String		title;
	private final String		program;
	private final String		version;

	public AboutBox(JFrame parent, String title, String program, String version)
	{
		super(parent);
		this.title = title;
		this.program = program;
		this.version = version;
		this.parent = parent;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try
		{
			jbInit();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void showAboutBox()
	{
		pack();
		Dimension dlgSize = this.getPreferredSize();
		this.setSize(new Dimension(350, 290));
		Dimension frmSize = parent.getSize();
		Point loc = parent.getLocation();
		this.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		this.setModal(true);
		this.validate();
		this.show();
	}

	//Component initialization
	private void jbInit() throws Exception
	{
		FormLayout paneLayout = new FormLayout("320px", "p, p");
		PanelBuilder paneBuilder = new PanelBuilder(paneLayout);
		FormLayout logoLayout = new FormLayout("320px", "80px");
		PanelBuilder builder = new PanelBuilder(logoLayout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		imageLabel.setPreferredSize(new Dimension(320, 80));
		imageLabel.setIcon(new ImageIcon(AboutBox.class.getResource("eLooplogo.png")));
		builder.add(imageLabel, cc.xywh(1, 1, 1, 1, "center, top"));
		paneBuilder.add(builder.getPanel(), cc.xy(1, 1));
		FormLayout textLayout = new FormLayout("320px", "6dlu, p, 4dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 8dlu, p, 16dlu");
		builder = new PanelBuilder(textLayout);
		builder.setDefaultDialogBorder();
		builder.addSeparator(title, cc.xy(1, 2));
		builder.addLabel(program, cc.xy(1, 4));
		builder.addLabel(version, cc.xy(1, 6));
		builder.addLabel("Copyright (c) " + ELSCalendar.now().get(Calendar.YEAR) + ", " +
						"Endless Loop Software, Inc.", cc.xy(1, 8));
      builder.addLabel("All Rights Reserved", cc.xy(1, 10));
		builder.add(button1, cc.xywh(1, 12, 1, 1, "c, b"));
		builder.addLabel("  ", cc.xy(1, 13));
		paneBuilder.add(builder.getPanel(), cc.xy(1, 2));
		this.getContentPane().add(paneBuilder.getPanel());
		this.setTitle("About");
		button1.addActionListener(this);
		setResizable(true);
	}

	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e)
	{
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			cancel();
		}
		super.processWindowEvent(e);
	}

	//Close the dialog
	void cancel()
	{
		dispose();
	}

	//Close the dialog on a button event
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == button1)
		{
			cancel();
		}
	}
}
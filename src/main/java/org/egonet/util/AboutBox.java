/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.egonet.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AboutBox extends JDialog implements ActionListener
{
	final private static Logger logger = LoggerFactory.getLogger(AboutBox.class);
	
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
		} catch (Exception ex)
		{
			logger.error(ex.toString());
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
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
package com.endlessloopsoftware.elsutils;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public abstract class ELSProgressBar extends JDialog
{
	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final JPanel        statusPanel  = new JPanel();
	private final JProgressBar  progressBar  = new JProgressBar();
	private final JLabel        messageLabel = new JLabel("Progress...");
	private final GridBagLayout statusLayout = new GridBagLayout();
	private final Timer         timer;
	private final SwingWorker   worker;
	private boolean             running;
	private String              message      = null;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public ELSProgressBar(Frame frame, String title, SwingWorker worker)
	{
		super(frame, title, false);

		this.worker = worker;
		timer = new Timer( 1000 / 60, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateProgress();
			}
		});

			jbInit();

			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.pack();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	protected abstract Object   construct();
	protected abstract void     finished();

	private void jbInit()
	{
		statusPanel.setLayout(statusLayout);

		statusPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		getContentPane().add(statusPanel);
		statusPanel.add(messageLabel,
						new GridBagConstraints(0, 1, 1, 1, 0.8, 0.3, GridBagConstraints.WEST,
											   GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		statusPanel.add(progressBar,
						new GridBagConstraints(0, 2, 1, 1, 0.8, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
											   new Insets(0, 10, 20, 10), 0, 0));
	}

	public synchronized void setMessage(String m)
	{
		message = m;

		if (!running)
		{
			messageLabel.setText(message);
		}
	}

	public void setMaximum(int max)
	{
		progressBar.setMaximum(max);

		progressBar.setVisible(max > 0);
	}

	public void hideBar()
	{
		setMaximum(0);
	}

	public synchronized void updateProgress()
	{
		progressBar.setValue(((Integer) worker.getValue()).intValue());
		messageLabel.setText(message);
	}

	public void open()
	{
		pack();
		setSize(300, 120);
		timer.start();
		worker.start();
		running = true;
		show();
	}

	public void close()
	{
		running = false;
		worker.finished();
		timer.stop();
		hide();
		dispose();
	}
}



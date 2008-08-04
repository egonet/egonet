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


/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: ELSProgressBar.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */
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

		try
		{
			jbInit();

			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.pack();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	protected abstract Object   construct();
	protected abstract void     finished();

	private void jbInit() throws Exception
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



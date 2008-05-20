package com.endlessloopsoftware.ego.client;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 * @version 1.0
 */

public class WorkingDialog extends JDialog
{
	private JPanel panel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel jLabel1 = new JLabel();

	public WorkingDialog(JFrame frame, String title, boolean modal)
	{
		super(frame, title, modal);
		try
		{
			jbInit();
			pack();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public WorkingDialog()
	{
		this(null, "", false);
	}

	private void jbInit() throws Exception
	{
		panel1.setLayout(borderLayout1);
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
		jLabel1.setToolTipText("");
		jLabel1.setText("Working...");
		getContentPane().add(panel1);
		panel1.add(jLabel1, BorderLayout.CENTER);
	}
}
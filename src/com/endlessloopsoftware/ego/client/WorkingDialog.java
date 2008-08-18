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
package com.endlessloopsoftware.ego.client;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
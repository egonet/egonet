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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.egonet.util.AlphaDocument;
import org.egonet.util.CatchingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Shared.AlterNameModel;

import java.io.IOException;



public class StartPanel extends JPanel
{
	final private static Logger logger = LoggerFactory.getLogger(StartPanel.class);
	
	private final GridBagLayout gridBagLayout1 = new GridBagLayout();
	private final JLabel titleLabel = new JLabel("What is your name?");
	private final JLabel firstNameLabel = new JLabel("First: ");
	private final JTextField firstNameField = new JTextField();
	
	private final JLabel lastNameLabel;
	
	
	private final JTextField lastNameField = new JTextField();
	private final JButton startInterviewButton = new JButton("Start Interview");
	private final AlphaDocument firstNameDocument = new AlphaDocument();
	private final AlphaDocument lastNameDocument = new AlphaDocument();
	
	private final EgoClient egoClient;

	public StartPanel(EgoClient egoClient) throws Exception
	{
		this.egoClient = egoClient;
		logger.info("Create of start panel using " + egoClient + " - " + egoClient.getStudy().getAlterNameModel());
		
		
		if(egoClient.getStudy().getAlterNameModel().equals(AlterNameModel.FIRST_LAST)) {
			firstNameField.setName("firstNameField");
			lastNameLabel = new JLabel("Last: ");
		} 
		else {
			lastNameLabel = new JLabel("Name: ");
		}
		lastNameField.setName("lastNameField");
		jbInit();
	}

	private void jbInit() throws Exception
	{
		this.setLayout(gridBagLayout1);

		titleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 16));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		firstNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		lastNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		startInterviewButton.setEnabled(false);

		this.setBorder(BorderFactory.createEtchedBorder());
		this.add(
			titleLabel,
			new GridBagConstraints(
				0,
				0,
				2,
				1,
				1.0,
				0.2,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0),
				0,
				0));
		
		logger.info(egoClient.getStudy().getAlterNameModel().toString());
		logger.info(egoClient.getStudy().getStudyName());
		if(egoClient.getStudy().getAlterNameModel().equals(AlterNameModel.FIRST_LAST)) {
			this.add(
				firstNameLabel,
				new GridBagConstraints(
					0,
					1,
					1,
					1,
					0.3,
					0.1,
					GridBagConstraints.CENTER,
					GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0),
					0,
					0));
			this.add(
				firstNameField,
				new GridBagConstraints(
					1,
					1,
					1,
					1,
					0.7,
					0.0,
					GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL,
					new Insets(10, 10, 10, 10),
					0,
					6));
		}
		this.add(
			lastNameLabel,
			new GridBagConstraints(
				0,
				2,
				1,
				1,
				0.0,
				0.1,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),
				0,
				0));
		this.add(
			lastNameField,
			new GridBagConstraints(
				1,
				2,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10),
				0,
				6));
		this.add(
			startInterviewButton,
			new GridBagConstraints(
				0,
				3,
				2,
				1,
				0.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(20, 80, 20, 80),
				0,
				0));

		startInterviewButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startInterviewButton_actionPerformed(e);
			}
		});

		firstNameField.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				firstNameField_actionPerformed(e);
			}
		});

		lastNameField.addActionListener(new CatchingAction("lastNameField")
		{
			public void safeActionPerformed(ActionEvent e) throws Exception
			{
				lastNameField_actionPerformed(e);
			}
		});

		firstNameField.setDocument(firstNameDocument);
		firstNameDocument.addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
			public void changedUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
			public void removeUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
		});

		lastNameField.setDocument(lastNameDocument);
		lastNameDocument.addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
			public void changedUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
			public void removeUpdate(DocumentEvent e)
			{
				textEvent(e);
			}
		});
	}

	void startInterviewButton_actionPerformed(ActionEvent e)
	{
		boolean success = false;

		/* Logic */
		try
		{
			egoClient.getInterview().setName(firstNameField.getText(), lastNameField.getText());

			success = egoClient.getStorage().saveInterview();
		}
		catch (IOException ex)
		{
			success = false;
		}

		/* UI */
		if (success)
		{
		    egoClient.getFrame().gotoClientQuestionPanel();
		}
		else
		{
		    egoClient.getFrame().gotoSourceSelectPanel();
		}
	}

	protected void lastNameField_actionPerformed(ActionEvent e) throws Exception
	{
		if (egoClient.getStudy().getAlterNameModel().equals(AlterNameModel.FIRST_LAST) && firstNameField.getText().length() == 0)
		{
			firstNameField.requestFocus();
		}
		else
		{
			startInterviewButton_actionPerformed(e);
		}
	}

	protected void firstNameField_actionPerformed(ActionEvent e)
	{
		if (lastNameField.getText().length() == 0)
		{
			lastNameField.requestFocus();
		}
		else
		{
			startInterviewButton_actionPerformed(e);
		}
	}

	protected void textEvent(DocumentEvent e) 
	{
		try {
		startInterviewButton.setEnabled(
			(firstNameField.getText().length() > 0 || !egoClient.getStudy().getAlterNameModel().equals(AlterNameModel.FIRST_LAST)) && (lastNameField.getText().length() > 0));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void doFocus() {
		if(egoClient.getStudy().getAlterNameModel().equals(AlterNameModel.FIRST_LAST)) {
			firstNameField.requestFocusInWindow();
		} 
		else {
			lastNameLabel.requestFocusInWindow();
		}
		
	}
}
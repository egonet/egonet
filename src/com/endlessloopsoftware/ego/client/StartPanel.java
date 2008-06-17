package com.endlessloopsoftware.ego.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.endlessloopsoftware.elsutils.documents.AlphaDocument;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.elsutils.files.FileReadException;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 * @version 1.0
 */

public class StartPanel extends JPanel
{
	private final GridBagLayout gridBagLayout1 = new GridBagLayout();
	private final JLabel titleLabel = new JLabel("What is your name?");
	private final JLabel firstNameLabel = new JLabel("First: ");
	private final JTextField firstNameField = new JTextField();
	private final JLabel lastNameLabel = new JLabel("Last: ");
	private final JTextField lastNameField = new JTextField();
	private final JButton startInterviewButton = new JButton("Start Interview");
	private final AlphaDocument firstNameDocument = new AlphaDocument();
	private final AlphaDocument lastNameDocument = new AlphaDocument();

	public StartPanel()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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

		lastNameField.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
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

	static void gotoPanel()
	{
		/* Return to first screen */
		EgoClient.frame.setVisible(false);
		EgoClient.frame.setContentPane(new StartPanel());
		EgoClient.frame.pack();
		EgoClient.frame.setSize(350, 350);
		//EgoClient.frame.setExtendedState(EgoClient.frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
   		Dimension frameSize = EgoClient.frame.getSize();
   		if (frameSize.height > screenSize.height)
   		{
   			frameSize.height = screenSize.height;
   		}
   		if (frameSize.width > screenSize.width)
   		{
   			frameSize.width = screenSize.width;
   		}
   		EgoClient.frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		EgoClient.frame.setVisible(true);
	}

	void startInterviewButton_actionPerformed(ActionEvent e)
	{
		boolean success = false;

		/* Logic */
		try
		{
			EgoClient.interview.setName(firstNameField.getText(), lastNameField.getText());

			success = EgoClient.storage.saveInterview();
		}
		catch (FileCreateException ex)
		{
			success = false;
		}
		catch (FileReadException ex)
		{
			success = false;
		}

		/* UI */
		if (success)
		{
			ClientQuestionPanel.gotoPanel();
		}
		else
		{
	      SourceSelectPanel.gotoPanel(false);
		}
	}

	protected void lastNameField_actionPerformed(ActionEvent e)
	{
		if (firstNameField.getText().length() == 0)
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
		startInterviewButton.setEnabled(
			(firstNameField.getText().length() > 0) && (lastNameField.getText().length() > 0));
	}
}
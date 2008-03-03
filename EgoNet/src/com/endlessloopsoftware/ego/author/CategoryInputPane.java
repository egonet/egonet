package com.endlessloopsoftware.ego.author;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

import com.endlessloopsoftware.ego.Question;
import org.egonet.util.listbuilder.ListBuilder;
import org.egonet.util.listbuilder.Selection;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: CategoryInputPane.java,v 1.1 2005/08/02 19:36:04 samag Exp $
 *
 */

/**
 * Dialog box for collecting categorical answer selections
 */
public class CategoryInputPane extends JDialog {
	private final JList parentList;

	private final GridBagLayout gridBagLayout1 = new GridBagLayout();

	// create list builder with preset values turned on.
	private final ListBuilder listBuilder = new ListBuilder();

	private final JButton jOKButton = new JButton("OK");

	private final JButton jCancelButton = new JButton("Cancel");

	private Box box1;

	/**
	 * Constructor for CategoryInputPane
	 * 
	 * @param list
	 *            question list from parent frame used to determine which
	 *            question we are operating on
	 */
	public CategoryInputPane(JList list) {
		parentList = list;

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes layout and fields for the dialog
	 * 
	 * @throws Exception
	 *             No idea, sorry
	 */
	private void jbInit() throws Exception {
		box1 = Box.createHorizontalBox();
		this.getContentPane().setLayout(gridBagLayout1);

		this.setModal(true);
		this.setTitle("Category Options");
		this.setSize(400, 300);

		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		this.getContentPane().add(
				listBuilder,
				new GridBagConstraints(0, 0, 4, 1, 1.0, 0.9,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(
				jCancelButton,
				new GridBagConstraints(3, 1, 1, 1, 0.2, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(10, 0, 10, 10), 0, 0));
		this.getContentPane().add(
				jOKButton,
				new GridBagConstraints(2, 1, 1, 1, 0.2, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(10, 20, 10, 0), 26, 0));
		this.getContentPane().add(
				box1,
				new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
						40, 0));

		jOKButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OKButton_actionPerformed(e);
			}
		});

		jCancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});

	}

	void OKButton_actionPerformed(ActionEvent e) {
		boolean changed = false;
		boolean compatible = true;
		// boolean abort = false;

		Question q = (Question) parentList.getSelectedValue();
		if (q != null) {
			/* count choices */

			Selection[] newSelections = listBuilder.getListSelections();

			// code added 09/05/2007 sonam
			// ask for values for each question

			/*
			 * for (int i = 0; i < newSelections.length; i++) { String
			 * inputValue = null; String extraInformation = ""; int
			 * intInputValue = -1;
			 * 
			 * do { inputValue = JOptionPane.showInputDialog(this,
			 * extraInformation + " Please input a value for " +
			 * newSelections[i].getString() + ": ", new
			 * Integer(newSelections[i].getValue()) ); if (inputValue == null) { //
			 * if user actually selected cancel
			 * JOptionPane.showMessageDialog(this, "Your question weight will
			 * default to " + Integer.toString(i) + "!"); inputValue =
			 * Integer.toString(i); } try { intInputValue =
			 * Integer.parseInt(inputValue); } catch (NumberFormatException ex) {
			 * inputValue = null; extraInformation = "Sorry! That was not a
			 * valid value for :\"" + newSelections[i].getString() + "\"";
			 * continue; } } while (inputValue == null);
			 * newSelections[i].setValue(intInputValue); }
			 */

			if (newSelections.length != q.selections.length) {
				if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame)) {
					compatible = false;
					changed = true;

					// If the number changed we know the list has changed, so
					// just copy over
					// the reference and let the loop trim the strings
					q.selections = newSelections;
				} else {
					// Don't make this change
					EgoNet.frame.fillCurrentPanel();
					this.hide();
					return;
				}
			}

			// Trim the strings, check for changes
			for (int i = 0; i < q.selections.length; i++) {
				if (!q.selections[i]
						.equals(newSelections[i].getString().trim())) {
					q.selections[i].setString(newSelections[i].getString()
							.trim());
					changed = true;
				}
				// q.selections[i].value= newSelections[i].value;
			}

			/*
			 * original code by Peter: // change all the values at once for (int
			 * i = 0; i < newWeights.length; i++) newSelections[i].value =
			 * newWeights[i];
			 */
			// End of code change
			EgoNet.study.setModified(changed);
			EgoNet.study.setCompatible(compatible);

			EgoNet.frame.fillCurrentPanel();
			this.hide();
		}

	}

	void cancelButton_actionPerformed(ActionEvent e) {
		this.hide();
	}

	void activate() {
		Question q = (Question) parentList.getSelectedValue();

		if (q != null) {
			listBuilder.setListSelections(q.selections);
		} else {
			System.err.println("Parent list had no selections");
		}

		listBuilder.setEditable(true);

		listBuilder.setElementName("Option: ");
		listBuilder.setTitle("Category Options");
		listBuilder
				.setDescription("Enter possible answers to this question below. Press Return to add the option "
						+ "to the options list. Press OK to set options or Cancel to undo changes.");
		listBuilder.setNameList(q.questionType == Question.ALTER_PROMPT);
		listBuilder.setLetUserPickValues(true);
		listBuilder
				.setPresetListsActive(q.answerType == Question.CATEGORICAL);
		
//		boolean preset = (q.answerType == Question.CATEGORICAL) ? true : false;
//		System.out.println("Is question categorical? " + preset);
//		
		listBuilder
				.setAdjacencyActive(q.questionType == Question.ALTER_PAIR_QUESTION);

		this.setSize(450, 400);
		jOKButton.setVisible(true);
		jCancelButton.setText("Cancel");

		/* Pack since we've modified the GUI elements */
		this.pack();
		this.setVisible(true);
	}
}

/**
 * $Log: CategoryInputPane.java,v $ Revision 1.1 2005/08/02 19:36:04 samag
 * Initial checkin
 * 
 * Revision 1.11 2004/04/11 00:24:48 admin Fixing headers
 * 
 */

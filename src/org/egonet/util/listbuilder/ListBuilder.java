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
package org.egonet.util.listbuilder;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;

import com.endlessloopsoftware.ego.author.CategoryInputPane;
import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.builder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Map;

public class ListBuilder extends JPanel implements Observer {
	/**
	 * Contains a list of Selection items that can be observed as the contents
	 * of the list change.
	 */
	public ObservableList<Selection> elementList;

	/**
	 * When true, a form is displayed where entries in the list may be added or
	 * deleted. When false, only the list of options is displayed.
	 */
	private boolean editable = true;

	/**
	 * When namelist is set to true, you get a "First Name" and a "Last Name"
	 * field to fill in. Normally, you just get a "Name" field.
	 */
	private boolean nameList = false;

	/**
	 * If adjacent is active, the selected item of the list may be toggled to be
	 * adjacent via a 2-state button. Toggled entries in the list, selected or
	 * not, will be highlighted with a different color. Adjacent "Selection"
	 * items will have their Adjacent field set to true IFF they have been
	 * selected by the 2-state button being toggled ON.
	 */
	private boolean adjacencyActive = false;

	/**
	 * If preset lists are active, you may choose a pre-set list of options
	 * (States, Gender, Yes/No, i.e. NON-custom). When false, custom is the only
	 * option and no list is shown.
	 */
	private boolean presetListsActive = false;

	/**
	 * When true, users will be able to directly set Selection values in the
	 * same manner they can enter string values.
	 */
	private boolean letUserPickValues = false;

	/**
	 * When max size is selected, users will not be able to add more items once
	 * the number of items has reached max size. The delete button will still be
	 * available, however, so they may delete items and then they will again be
	 * allowed to add items up to the max size of the list.
	 */
	private int maxSize = -1;

	private String elementName = "";

	private String title = "";

	private String description = "";

	private JList jList = null;;

	private JScrollPane jScrollPane = null;

	private JPanel panelTopHalf = null;

	private JPanel panelTopRight = null;

	private JPanel panelButtons = null;

	private JButton buttonAdjacency = null;

	private JButton buttonDelete = null;

	private JButton buttonAdd = null;

	private JTextArea labelDescription = null;

	private JTextField firstName, lastName, itemName, value;

	private CellConstraints constraints = new CellConstraints();

	private static final Map<String, Selection[]> presets = ListBuilderPresets
			.getPresets();

	private static final String CHOOSE_PRESET_INSTRUCTION = "Choose from preset options";

	public ListBuilder() {
		super();
		elementList = new ObservableList<Selection>();
		build();
		addListObserver(this);
	}

	public ListBuilder(boolean isPresetValuesActive) {
		super();
		elementList = new ObservableList<Selection>();
		build();
		addListObserver(this);
	}

	public ListBuilder(Selection[] selections) {
		super();
		elementList = new ObservableList<Selection>(selections);
		build();
		addListObserver(this);
	}

	public void addListObserver(Observer ob) {
		elementList.addObserver(ob);
	}

	/**
	 * item has been updated. update our JList.
	 */
	public void update(Observable o, Object arg) {
		jList.setListData(elementList.toArray());
		jList.revalidate();
		jList.repaint();
	}

	private void build() {
		System.out.println("Build start");
		// purge anything old
		removeAll();

		// System.out.println("Building List builder...");
		jList = new JList();
		jList.setCellRenderer(new SelectionListCellRenderer());
		jList.setListData(elementList.toArray());
		jScrollPane = new JScrollPane(jList);

		// is NOT editable, we only use the main panel i.e. "this"
		if (!editable) {
			FormLayout layout = new FormLayout(
					"2dlu, fill:max(pref;200dlu):grow, 2dlu",
					"2dlu, fill:pref:grow, 2dlu");
			setLayout(layout);
			add(jScrollPane, constraints.xy(2, 2));
			invalidate();
			return;
		}

		// is editable, so we start using subpanels for layouts
		FormLayout mainLayout = new FormLayout(
				"2dlu, fill:max(pref;300dlu):grow, 2dlu",
				"2dlu, fill:pref:grow, 2dlu, fill:max(pref;2dlu):grow, 2dlu");
		setLayout(mainLayout);

		// combine top and bottom panels
		add(buildTop(), constraints.xy(2, 2));
		add(buildBottom(), constraints.xy(2, 4));

		// mainLayout.invalidateLayout(this);
		// when the list selection is changed
		jList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					clearTextFields();
					return;
				}

				JList list = (JList) e.getSource();
				Selection selection = (Selection) list.getSelectedValue();
				if (selection == null)
					return;

				// if this is a name list, attempt to parse out the pieces,
				// otherwise leave them empty
				String firstStr = "";
				String lastStr = "";
				if (isNameList()) {
					String[] parts = selection.getString().split(", ");
					if (parts.length != 2) {
						// TODO: warn person
						return;
					} else {
						lastStr = parts[0];
						firstStr = parts[1];
					}
				}

				setTextFields(firstStr, lastStr, selection.getString(), ""
						+ selection.getValue());
			}
		});
		this.validate();
		System.out.println("Build complete");
	}

	private JComponent buildTop() {
		// System.out.println("Building Top....");
		// top half panel
		panelTopHalf = new JPanel();
		FormLayout topHalfLayout = new FormLayout(
				"2dlu, fill:145dlu:grow, 2dlu, fill:145dlu:grow, 2dlu",
				"2dlu, fill:min(pref;150dlu):grow, 2dlu");
		panelTopHalf.setLayout(topHalfLayout);
		panelTopHalf.add(jScrollPane, constraints.xy(2, 2));
		panelTopHalf.add(buildTopRight(), constraints.xy(4, 2));
		return panelTopHalf;
	}

	private JComponent buildTopRight() {
		// System.out.println("Building Top Right....:"+ isPresetListsActive());
		panelTopRight = new JPanel();
		FormLayout panelTopRightLayout = new FormLayout(
				"2dlu, fill:75dlu:grow, 2dlu",
				"2dlu, fill:20dlu:grow, 2dlu, 35dlu, 2dlu, pref:grow, 2dlu, fill:pref:grow, 2dlu");
		panelTopRight.setLayout(panelTopRightLayout);

		JLabel labelTitle = new JLabel(title);
		panelTopRight.add(labelTitle, constraints.xy(2, 2));

		labelDescription = new JTextArea(description);
		labelDescription.setEditable(false);
		labelDescription.setLineWrap(true);
		labelDescription.setWrapStyleWord(true);
		//labelDescription.setRows(5); labelDescription.setColumns(30);
		
		JScrollPane sp = new JScrollPane(labelDescription);
		panelTopRight.add(sp, constraints.xy(2, 4));

		if (isPresetListsActive()) {
			// System.out.println("Presets are supposed to be active");
			final JComboBox comboPresets = new JComboBox();
			comboPresets.addItem(CHOOSE_PRESET_INSTRUCTION);
			for (String presetName : presets.keySet())
				comboPresets.addItem(presetName);
			panelTopRight.add(comboPresets, constraints.xy(2, 6));
			comboPresets.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						Selection[] options = presets.get(item);
						if (options != null) {
							setListSelections(options);
							comboPresets.setSelectedIndex(0);
							clearTextFields();
						}
					}
				}
			});
		}

		panelTopRight.add(buildInputPanel(), constraints.xy(2, 8));

		return panelTopRight;
	}

	private JComponent buildInputPanel() {
		final String colspec = "2dlu, fill:pref:grow,  2dlu";

		FormLayout inputLayout = new FormLayout(colspec);
		DefaultFormBuilder formBuilder = new DefaultFormBuilder(inputLayout);

		formBuilder.append("");
		formBuilder.nextRow();
		formBuilder.setLeadingColumnOffset(1);

		firstName = new JTextField();
		lastName = new JTextField();
		itemName = new JTextField();
		value = new JTextField();
		
		firstName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER))
					lastName.grabFocus();
			}
		});

		lastName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
				buttonAdd.setEnabled(true);
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER) && isLetUserPickValues())
					value.grabFocus();
				else if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER))
					firstName.grabFocus();
			}
		});

		itemName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER) && isLetUserPickValues())
					value.grabFocus();
			}
		});

		value.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
				buttonAdd.setEnabled(true);
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
//				System.out.println("source=" + keyEvent.getSource() + " id=" + keyEvent.getID() + 
//						" when=" + keyEvent.getWhen() + " modifiers=" + keyEvent.getModifiers() +
//						" keyCode=" + keyEvent.getKeyCode() + " keyChar=" + keyEvent.getKeyChar());
				// if you typed on value, blank the selection
				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER)) {
					if (isNameList())
						firstName.grabFocus();
					else
						itemName.grabFocus();
				}
			}
		});

		// couple different configurations here
		if (nameList) {
			formBuilder.append("First Name: ", firstName, false);
			formBuilder.append("Last Name: ", lastName, true);
		} else {
			formBuilder.append("Item Name: ", itemName, false);
		}

		if (letUserPickValues)
			formBuilder.append("Value: ", value, true);

		return formBuilder.getPanel();
	}

	/**
	 * Called when the text fields have keys typed in them. This method does not
	 * handle FOCUS at all -- it only stores data. If you'd like the selected
	 * item to change, or the focus to move, upon saving data, do it outside of
	 * this method in your handler.
	 * 
	 * @param keyEvent
	 */
	private void saveDataForSelectionOfList(KeyEvent keyEvent) {
		Object selectionObject = jList.getSelectedValue();
		boolean itemSelectedFromList = selectionObject != null && selectionObject instanceof Selection;
		boolean enterPressed = (keyEvent.getKeyCode() == KeyEvent.VK_ENTER);
		boolean shouldBlank = (keyEvent.getSource() == value)
				|| (keyEvent.getSource() == lastName && isNameList() && !isLetUserPickValues())
				|| (keyEvent.getSource() == itemName && !isNameList() && !isLetUserPickValues());

		if (itemSelectedFromList) {
			Selection selection = (Selection) selectionObject;

			// OLD item
			try {
				convertTextFieldsToSelection(selection);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Could not parse your integer value", "Value problem",
						JOptionPane.ERROR_MESSAGE);
				value.setText(selection.getValue() + "");
				return;
			}

			if (enterPressed && shouldBlank) {
				// someone HAS pressed enter
				jList.clearSelection();
				clearTextFields();
			}
			jList.updateUI();
		} else if (!itemSelectedFromList) {
			// NEW item -- don't do anything until they hit enter on the LAST
			// field
			if (enterPressed && shouldBlank) {
				Selection selection = new Selection();
				try {
					convertTextFieldsToSelection(selection);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this,
							"Could not parse your integer value",
							"Value problem", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(maxSize != -1 && elementList.size() + 1 > maxSize)
				{
					JOptionPane.showMessageDialog(this,
							"You cannot add any more alters!",
							"Maximum alter limit reached", JOptionPane.ERROR_MESSAGE);
				} else {
					elementList.add(selection);
					// someone HAS pressed enter
					jList.clearSelection();
					clearTextFields();
				}
			}
		}
	}

	private void convertTextFieldsToSelection(Selection selection)
			throws NumberFormatException {
		if (isLetUserPickValues() && !value.getText().equals("")
				&& !value.getText().equals("-")) {
			int intVal = Integer.parseInt(value.getText());
			selection.setValue(intVal);
		}

		if (isNameList()) {
			// selection.setString(lastName.getText() + ", " +
			// firstName.getText());
			selection.setString(firstName.getText() + " " + lastName.getText());
		} else {
			selection.setString(itemName.getText());
		}
	}

	private void clearTextFields() {
		setTextFields("", "", "", "");
	}

	private void setTextFields(String first, String last, String item,
			String values) {
		firstName.setText(first);
		lastName.setText(last);
		itemName.setText(item);
		value.setText(values);
	}

	private JComponent buildBottom() {
		// button panel

		ButtonBarBuilder builder = new ButtonBarBuilder();

		buttonAdd = new JButton("Add to list");
		buttonAdd.setEnabled(false);
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// simulate enter key being pressed at last name
				if(isNameList()) {
						try {
							KeyEvent keyEvent = new KeyEvent(lastName, 0, 0, 0, KeyEvent.VK_ENTER, '\n');
							saveDataForSelectionOfList(keyEvent);
						} catch (Exception ex) {
							// eat failure, keypress simulation failed
						}
					}
				else { //if (itemName.getText() != null && value.getText() != null) {
						try {
							KeyEvent keyEvent = new KeyEvent(value, 0, 0, 0,
									KeyEvent.VK_ENTER, '\n');
							saveDataForSelectionOfList(keyEvent);
						} catch (Exception ex) {
							// eat failure, keypress simulation failed
						}
					}
			}
		});

		buttonDelete = new JButton("Delete selected item");
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				Object[] selections = jList.getSelectedValues();
				for (Object o : selections)
					elementList.remove(o);
				jList.clearSelection();
			}
		});
		
		builder.addGridded(buttonAdd);
		builder.addGridded(buttonDelete);

		if (isAdjacencyActive()) {
			buttonAdjacency = new JButton("Mark selected item adjacent");
			buttonAdjacency.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					Object[] selections = jList.getSelectedValues();
					for (Object o : selections) {
						if (o instanceof Selection) {
							((Selection) o).setAdjacent(!((Selection) o)
									.isAdjacent());
						}
					}
					jList.revalidate();
					jList.repaint();
				}
			});
			builder.addGridded(buttonAdjacency);
		}

		panelButtons = builder.getPanel();
		return panelButtons;
	}

	public static void main(String[] args) throws Exception {
	
		ListBuilder listBuilder = new ListBuilder();

		listBuilder.setName("name field");
		listBuilder.setTitle("title field");

		String s = "";
		for (int i = 0; i < 20; i++)
			s += "Lorem ipsum dolor. ";

		listBuilder.setDescription(s);

		listBuilder.setEditable(true);
		listBuilder.setLetUserPickValues(true);
		listBuilder.setNameList(false);
		listBuilder.setAdjacencyActive(true);

		
		CategoryInputPane frame = new CategoryInputPane(null, new JList());
		frame.pack();
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		build();
	}

	public ObservableList<Selection> getElementList() {
		return elementList;
	}

	public void setElementList(ObservableList<Selection> elementList) {
		this.elementList = elementList;
		build();
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public int getMaxListSize() {
		return maxSize;
	}

	public void setMaxListSize(int maxSize) {
		setMaxSize(maxSize);
		build();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		build();
	}

	public Selection[] getSelections() {
		
		List<Selection> selectionList = new ArrayList<Selection>();
		for(int i = 0 ; i < elementList.size() && ((maxSize != -1 && i < maxSize) || maxSize == -1); i++)
		{
			Object o = elementList.get(i);
			if (o.getClass().equals(Selection.class))
				selectionList.add((Selection)o);
		}

		int i = 0;
		Selection[] arr = new Selection[selectionList.size()];
		for (Selection sel : selectionList)
				arr[i++] = sel;

		return arr;
	}

	public void setSelections(Selection[] selections) {
		elementList.removeAll();
		elementList.addAll(selections);
		build();
	}

	public Selection[] getListSelections() {
		return getSelections();
	}

	public void setListSelections(Selection[] selections) {
		setSelections(selections);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isNameList() {
		return nameList;
	}

	public void setNameList(boolean nameList) {
		this.nameList = nameList;
		build();
	}

	public boolean isAdjacencyActive() {
		return adjacencyActive;
	}

	public void setAdjacencyActive(boolean adjacencyActive) {
		this.adjacencyActive = adjacencyActive;
		build();
	}

	public boolean isPresetListsActive() {
		return presetListsActive;
	}

	public void setPresetListsActive(boolean presetListsActive) {
		this.presetListsActive = presetListsActive;
		build();
	}

	public String[] getListStrings() {

		Selection [] listSelections = getListSelections();
		String[] listStrings = new String[listSelections.length];
		
		int i = 0;
		for (Selection selection : listSelections)
			listStrings[i++] = selection.getString();

		return listStrings;
	}

	public void setListStrings(String[] listStrings) {
		elementList.removeAll();
		for (int i = 0; i < listStrings.length; i++) {
			elementList.add(new Selection(listStrings[i], i, i, false));
		}
		build();
	}

	public boolean isLetUserPickValues() {
		return letUserPickValues;
	}

	public void setLetUserPickValues(boolean letUserPickValues) {
		this.letUserPickValues = letUserPickValues;
		build();
	}

	public void requestFocusOnFirstVisibleComponent() {
		if (this.isNameList()) 
			itemName.requestFocusInWindow();
		else
			firstName.requestFocusInWindow();
	}

}

package org.egonet.util.listbuilder;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.awt.Robot;
import java.awt.Dimension;
import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.builder.*;

import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;
import java.util.Map;

public class ListBuilder<T> extends JPanel implements Observer {
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
	private int maxSize = 10;

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

	private JButton buttonNext = null;

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
					"2dlu, fill:min(pref;200dlu):grow, 2dlu",
					"2dlu, fill:pref:grow, 2dlu");
			setLayout(layout);
			add(jScrollPane, constraints.xy(2, 2));
			invalidate();
			return;
		}

		// is editable, so we start using subpanels for layouts
		FormLayout mainLayout = new FormLayout(
				"2dlu, fill:min(pref;300dlu):grow, 2dlu",
				"2dlu, fill:pref:grow, 2dlu, fill:max(pref;2dlu), 2dlu");
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
				"2dlu, fill:20dlu:grow, 2dlu, fill:pref:grow, 2dlu, pref:grow, 2dlu, fill:pref:grow, 2dlu");
		panelTopRight.setLayout(panelTopRightLayout);

		JLabel labelTitle = new JLabel(title);
		panelTopRight.add(labelTitle, constraints.xy(2, 2));

		labelDescription = new JTextArea(description);
		labelDescription.setEditable(false);
		labelDescription.setLineWrap(true);
		labelDescription.setWrapStyleWord(true);
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
		buttonNext = new JButton("-->");
		buttonNext.setEnabled(false);
		
		firstName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
				buttonNext.setEnabled(true);
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
				if (KeyEvent.getKeyText(keyEvent.getKeyCode()).equals("Enter"))
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
				if (KeyEvent.getKeyText(keyEvent.getKeyCode()).equals("Enter")
						&& isLetUserPickValues())
					value.grabFocus();
				else if (KeyEvent.getKeyText(keyEvent.getKeyCode()).equals(
						"Enter"))
					firstName.grabFocus();
			}
		});

		itemName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent keyEvent) {
				buttonNext.setEnabled(true);
			}

			public void keyPressed(KeyEvent keyEvent) {
			}

			public void keyReleased(KeyEvent keyEvent) {
				saveDataForSelectionOfList(keyEvent);
				if (KeyEvent.getKeyText(keyEvent.getKeyCode()).equals("Enter")
						&& isLetUserPickValues())
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
				if (KeyEvent.getKeyText(keyEvent.getKeyCode()).equals("Enter")) {
					if (isNameList())
						firstName.grabFocus();
					else
						itemName.grabFocus();
				}
			}
		});

		buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// simulate "Enter" key being pressed at last name
				if (isNameList()) {
					lastName.grabFocus();
				}
				else {
					value.grabFocus();
				}
			}
		});

		// couple different configurations here
		if (nameList) {
			formBuilder.append("First Name: ", firstName, false);
			formBuilder.append(buttonNext, 1);
			formBuilder.append("Last Name: ", lastName, true);
		} else {
			formBuilder.append("Item Name: ", itemName, false);
			formBuilder.append(buttonNext, 1);
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
		boolean itemSelectedFromList = selectionObject != null
				&& selectionObject instanceof Selection;
		boolean enterPressed = KeyEvent.getKeyText(keyEvent.getKeyCode())
				.equals("Enter");
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
				elementList.add(selection);

				// someone HAS pressed enter
				jList.clearSelection();
				clearTextFields();
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
		panelButtons = new JPanel();
		buttonAdd = new JButton("Add to list");
		buttonAdd.setEnabled(false);
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// simulate "Enter" key being pressed at last name
				if (lastName.getText() != null && firstName.getText() != null) {
						try {
							KeyEvent keyEvent = new KeyEvent(lastName, 0, 0, 0,
									KeyEvent.VK_ENTER, '\n');
							saveDataForSelectionOfList(keyEvent);
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
						}
					}
				else { //if (itemName.getText() != null && value.getText() != null) {
					System.out.println("In here");
						try {
							KeyEvent keyEvent = new KeyEvent(value, 0, 0, 0,
									KeyEvent.VK_ENTER, '\n');
							saveDataForSelectionOfList(keyEvent);
						} catch (Exception ex) {
							ex.printStackTrace(System.err);
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
		panelButtons.add(buttonAdd);
		panelButtons.add(buttonDelete);

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
			panelButtons.add(buttonAdjacency);
		}

		return panelButtons;
	}

	public static void main(String[] args) {
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

		JFrame frame = new JFrame();
		frame.add(listBuilder);

		frame.setSize(400, 500);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		build();
	}

	public ObservableList getElementList() {
		return elementList;
	}

	public void setElementList(ObservableList<Selection> elementList) {
		this.elementList = elementList;
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
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		build();
	}

	public Selection[] getSelections() {
		int ct = 0;
		for (Object o : elementList.toArray())
			if (o.getClass().equals(Selection.class))
				ct++;

		Selection[] arr = new Selection[ct];
		int i = 0;
		for (Object o : elementList.toArray()) {
			if (o.getClass().equals(Selection.class)) {
				arr[i] = (Selection) o;
				i++;
			}
		}

		return arr;
	}

	public void setSelections(Selection[] selections) {
		elementList.removeAll();
		elementList.addAll(selections);
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
		System.out.println("Set presets on");
		build();
		System.out.println("Completed new build");
	}

	public String[] getListStrings() {
		int len = elementList.size();
		String[] listStrings = new String[len];
		int i = 0;
		for (Iterator iterator = elementList.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			if (!(element instanceof Selection))
				element = new Selection();

			Selection selection = (Selection) element;
			listStrings[i++] = selection.getString();
		}

		return listStrings;
	}

	public void setListStrings(String[] listStrings) {
		elementList.removeAll();
		for (int i = 0; i < listStrings.length; i++) {
			elementList.add(new Selection(listStrings[i], i, i, false));
		}
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

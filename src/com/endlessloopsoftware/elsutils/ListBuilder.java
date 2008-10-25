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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * <p>Title: ListBuilder</p>
 * <p>Description: Creates a dialog containing a list creationg interface</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 * @version 1.1
 */
public class ListBuilder
    extends JPanel
    implements Observer
{
    private static final int CUSTOM_LIST_INDEX = 0;
    private String[][]       presetLists =
                                           {
                                               {"Custom"},
                                               {"Yes/No", "Yes", "No"},
                                               {"Gender", "Male", "Female"},
                                               {
                                                   "States", "Alabama", "Alaska", "Arizona", "Arkansas", "California",
                                                   "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii",
                                                   "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
                                                   "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan",
                                                   "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska",
                                                   "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
                                                   "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
                                                   "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                                                   "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
                                                   "West Virginia", "Wisconsin", "Wyoming"
                                               },
                                           };
    private final GridBagLayout  listBuilderLayout = new GridBagLayout();
    private final JList          jList             = new JList();
    private final ObservableList<Selection> elementList       = new ObservableList<Selection>();
    private final JScrollPane    listScrollPane    = new JScrollPane(jList);
    private final JLabel         firstNameLabel    = new JLabel("First Name:");
    private final JTextField     firstNameField    = new JTextField();
    private final JLabel         nameLabel         = new JLabel("Name: ");
    private final JTextField     valueField        = new JTextField();
    private final JButton        deleteButton      = new JButton("Delete");
    private final JTextPane      descriptionLabel  = new JTextPane();

    //	private final   JScrollPane     descriptionScroll   = new JScrollPane(descriptionLabel);
    private final JLabel        titleLabel         = new JLabel();
    private final JComboBox     listMenu           = new JComboBox();
    private final JLabel        listLabel          = new JLabel();
    private final JToggleButton adjacentToggle     = new JToggleButton();
    private Selection[][]       listList           = new Selection[0][];
    private String[]            nameList           = new String[0];
    private int                 maxListSize        = Integer.MAX_VALUE;
    private boolean             firstNameActive    = false;
    private boolean             presetActive       = true;
    private boolean             adjacencyActive    = false;

    /**
     * Creates a new ListBuilder object.
     */
    public ListBuilder()
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

    /**
     *
     *
     * @throws Exception throws
     */
    private void jbInit()
                 throws Exception
    {
        /* Set up preset lists */
        for (int i = 0; i < presetLists.length; i++)
        {
            addPresetList(presetLists[i]);
        }

        this.setLayout(listBuilderLayout);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        firstNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jList.setRequestFocusEnabled(false);
        //jList.setDebugGraphicsOptions(DebugGraphics.BUFFERED_OPTION);
        jList.setToolTipText("");
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setAutoscrolls(true);

        descriptionLabel.setBackground(Color.lightGray);
        descriptionLabel.setFont(new java.awt.Font("Serif", 0, 14));
        descriptionLabel.setBorder(BorderFactory.createEtchedBorder());
        descriptionLabel.setCaretColor(UIManager.getColor("textInactiveText"));
        descriptionLabel.setEditable(false);
        descriptionLabel.setText("Text describing the list with any instructions");

        titleLabel.setFont(new java.awt.Font("SansSerif", 0, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setText("<html><b>List Title</b></html>");

        listLabel.setText("Lists: ");
        listLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        listMenu.setSelectedItem("Custom");

        adjacentToggle.setText("Adjacent");
        adjacentToggle.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    adjacentToggle_actionPerformed(e);
                }
            });

        this.add(listScrollPane,
                 new GridBagConstraints(0, 0, 2, 5, 0.3, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(10, 10, 10, 10), 10, 0));
        this.add(titleLabel,
        		new GridBagConstraints(2, 0, 2, 1, 0.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        				new Insets(10, 10, 10, 10), 0, 0));

        this.add(descriptionLabel,
        		new GridBagConstraints(2, 1, 2, 1, 0.7, 2.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        				new Insets(10, 10, 10, 10), 20, 20));

        this.add(listLabel,
        		new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
        				new Insets(10, 10, 10, 10), 0, 0));

        this.add(listMenu,
        		new GridBagConstraints(3, 2, 1, 1, 0.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        				new Insets(10, 10, 10, 10), 0, 0));

        this.add(firstNameLabel,
                 new GridBagConstraints(2, 3, 1, 1, 0.2, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 0, 0), 0, 0));
        this.add(firstNameField,
                 new GridBagConstraints(3, 3, 1, 1, 0.3, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                        new Insets(10, 10, 10, 10), 6, 6));
        this.add(nameLabel,
                 new GridBagConstraints(2, 4, 1, 1, 0.2, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 0, 0), 0, 0));
        this.add(valueField,
                 new GridBagConstraints(3, 4, 1, 1, 0.3, 0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                        new Insets(10, 10, 10, 10), 6, 6));
        this.add(deleteButton,
                 new GridBagConstraints(0, 5, 1, 1, 0.0, 0.1, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                        new Insets(10, 15, 10, 10), 0, 0));

        this.add(adjacentToggle,
                 new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 0), 0, 0));

        jList.setModel(new DefaultListModel());

        valueField.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    valueField_actionPerformed(e);
                }
            });

        firstNameField.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    firstNameField_actionPerformed(e);
                }
            });

        jList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {
                    elementListSelectionChanged(e);
                }
            });

        deleteButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    deleteButtonActionPerformed(e);
                }
            });

        listMenu.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    listMenuActionPerformed(e);
                }
            });

        this.addPropertyChangeListener(new java.beans.PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent e)
                {
                    this_propertyChange(e);
                }
            });

        /* Configure List Display */
        adjacentToggle.setVisible(adjacencyActive);
        firstNameLabel.setVisible(firstNameActive);
        firstNameField.setVisible(firstNameActive);

        if (firstNameActive)
        {
            nameLabel.setText("Last Name:");
        }

        /* Observe changes in list */
        addListObserver(this);
    }

    /**
     * Updates title description text
     * @param     s string with new title
     */
    public void setTitle(String s)
    {
        titleLabel.setText(s);
    }

    /**
     * Updates list description text
     * @param     s string with new text
     */
    public void setDescription(String s)
    {
        descriptionLabel.setText(s);
    }

    /**
     * Updates lable of new element field
     * @param     s string with new name
     */
    public void setElementName(String s)
    {
        nameLabel.setText(s);
    }

    /**
     * Updates lable of new element field
     * @param     s string with new name
     */
    public void setMaxListSize(int i)
    {
        maxListSize = i;
    }

    /**
     * sets use of firstname/lastname vs. one labeled field
     * @param     b boolean with new value
     */
    public void setNameList(boolean b)
    {
        firstNameActive = b;

        firstNameLabel.setVisible(b);
        firstNameField.setVisible(b);

        if (b)
        {
            nameLabel.setText("Last Name:");
        }
    }

    /**
     * Is list of elements modifiable?
     * @param     b modifiable or not
     */
    public void setEditable(boolean b)
    {
        nameLabel.setVisible(b);
        valueField.setVisible(b);
        listMenu.setVisible(presetActive && b);
        listLabel.setVisible(presetActive && b);
        descriptionLabel.setVisible(b);
        deleteButton.setVisible(b);
        titleLabel.setVisible(b);
        firstNameLabel.setVisible(b && firstNameActive);
        firstNameField.setVisible(b && firstNameActive);
        adjacentToggle.setVisible(b && adjacencyActive);

        firstNameField.setText("");
        valueField.setText("");
    }

    /**
     * Enables/Disables preset list menu
     * @param     b true = enable
     */
    public void setPresetListsActive(boolean b)
    {
        presetActive = b;
        listMenu.setVisible(b);
        listLabel.setVisible(b);
    }

    /**
     * Enables/Disables preset list menu
     * @param     b true = enable
     */
    public void setAdjacencyActive(boolean b)
    {
        adjacencyActive = b;
        adjacentToggle.setVisible(b);

        if (b)
        {
            jList.setCellRenderer(new SelectionListCellRenderer());
        }
        else
        {
            jList.setCellRenderer(new DefaultListCellRenderer());
        }
    }

    /**
     * Adds a preset list to the menu
     * @param    list elements of list, element 0 must be name of list
     */
    public void addPresetList(String[] list)
    {
        if (list != null)
        {
            String[]      newNames    = new String[nameList.length + 1];
            Selection[][] newListList = new Selection[nameList.length + 1][];
            Selection[]   newList     = new Selection[list.length - 1];

            for (int i = 0; i < nameList.length; i++)
            {
                newNames[i] = nameList[i];
            }

            newNames[nameList.length] = list[0];

            for (int i = 0; i < listList.length; i++)
            {
                newListList[i] = listList[i];
            }

            for (int i = 1; i < list.length; i++)
            {
                newList[i - 1] = new Selection(list[i], list.length - (i + 1), i - 1, false);
            }

            newListList[listList.length] = newList;

            nameList = newNames;
            listList = newListList;
            listMenu.addItem(list[0]);
        }
    }

    /**
     * Returns values from list
     * @return values list of current values in list
     */
    public Selection[] getListSelections()
    {
        Selection[] sels = (Selection[]) elementList.toArray(new Selection[elementList.size()]);

        /* Set values before we pass the list out */
        for (int i = 0; i < sels.length; i++)
        {
            sels[i].value = sels.length - (i + 1);
        }

        return sels;
    }

    /**
     * Returns values from list
     * @return values list of current values in list
     */
    public String[] getListStrings()
    {
        Selection[] sel = getListSelections();
        String[]    s = new String[sel.length];

        for (int i = 0; i < sel.length; i++)
        {
            s[i] = sel[i].string;
        }

        return s;
    }

    /**
     * Adds a preset list to the menu
     * @return values list of current values in list
     */
    public void setListSelections(Selection[] s)
    {
        DefaultListModel dlm = (DefaultListModel) jList.getModel();

        if (s == null)
        {
            s = new Selection[0];
        }

        dlm.removeAllElements();
        elementList.removeAll();
        listList[CUSTOM_LIST_INDEX] = s;
        listMenu.setSelectedItem("Custom");
    }

    /**
     * Adds a preset list to the menu
     * @return values list of current values in list
     */
    public void setListStrings(String[] s)
    {
        Selection[] sel = new Selection[s.length];

        for (int i = 0; i < s.length; i++)
        {
            sel[i] = new Selection(s[i], s.length - (i + 1), i, false);
        }

        setListSelections(sel);
    }

    /**
     * Adds a preset list to the menu
     * @return values list of current values in list
     */
    public void addListObserver(Observer o)
    {
        elementList.addObserver(o);
    }

    /**
     * Updates right side question fields when the selection changes
     * @param     e event generated by selection change.
     */
    protected void elementListSelectionChanged(ListSelectionEvent e)
    {
        setButtonsEnabled();
    }

    /**
     *
     */
    private void setButtonsEnabled()
    {
        boolean enabled = (jList.getSelectedIndex() != -1);

        adjacentToggle.setSelected(enabled && ((Selection) jList.getSelectedValue()).adjacent);
        deleteButton.setEnabled(enabled);
        adjacentToggle.setEnabled(enabled);
    }

    /**
     *
     *
     * @param e param
     */
    private void adjacentToggle_actionPerformed(ActionEvent e)
    {
        Selection selection = (Selection) jList.getSelectedValue();

        if (selection != null)
        {
            selection.adjacent = adjacentToggle.isSelected();
            jList.repaint();
        }
    }

    /**
     *
     *
     * @param e param
     */
    protected void addButtonActionPerformed(ActionEvent e)
    {
        String newValue;

        newValue = valueField.getText();

        if (firstNameActive)
        {
            newValue = firstNameField.getText() + " " + newValue;
        }

        if (newValue.length() > 0)
        {
            Selection        selection = new Selection(newValue, 0, 0, false);
            DefaultListModel dlm = (DefaultListModel) jList.getModel();

            if (!dlm.contains(selection))
            {
                dlm.addElement(selection);
                elementList.add(selection);
            }

            jList.setSelectedValue(dlm.getElementAt(elementList.size() - 1), true);

            valueField.setText("");
            firstNameField.setText("");

            if (firstNameActive)
            {
                firstNameField.requestFocus();
            }
            else
            {
                valueField.requestFocus();
            }
        }
    }

    /**
     *
     *
     * @param e param
     */
    protected void deleteButtonActionPerformed(ActionEvent e)
    {
        DefaultListModel dlm = (DefaultListModel) jList.getModel();

        if (jList.getSelectedIndex() != -1)
        {
            elementList.remove(jList.getSelectedIndex());
            dlm.removeElementAt(jList.getSelectedIndex());
        }
    }

    /**
     *
     *
     * @param e param
     */
    protected void listMenuActionPerformed(ActionEvent e)
    {
        int              index = listMenu.getSelectedIndex();
        DefaultListModel dlm = (DefaultListModel) jList.getModel();

        /** @todo warn removing elements */
        dlm.removeAllElements();
        elementList.removeAll();

        for (int i = 0; (i < listList[index].length) && (i < maxListSize); i++)
        {
            dlm.addElement(listList[index][i]);
            elementList.add(listList[index][i]);
        }
    }

    /**
     *
     *
     * @param e param
     */
    protected void valueField_actionPerformed(ActionEvent e)
    {
        if (firstNameActive && (firstNameField.getText().length() == 0))
        {
            firstNameField.requestFocus();
        }
        else if (valueField.getText().length() > 0)
        {
            addButtonActionPerformed(e);
        }
    }

    /**
     *
     *
     * @param e param
     */
    protected void firstNameField_actionPerformed(ActionEvent e)
    {
        if (valueField.getText().length() == 0)
        {
            valueField.requestFocus();
        }
        else if (firstNameField.getText().length() > 0)
        {
            addButtonActionPerformed(e);
        }
    }

    /**
     *
     *
     * @param e param
     */
    private void this_propertyChange(PropertyChangeEvent e)
    {
        if (e.getPropertyName().equals("Frame.active"))
        {
            setButtonsEnabled();
            valueField.setText("");
            firstNameField.setText("");
            listMenu.setSelectedIndex(0);
        }
    }

    /**
     *
     *
     * @param o param
     * @param arg param
     */
    public void update(Observable o, Object arg)
    {
        valueField.setEnabled(elementList.size() < maxListSize);
        nameLabel.setEnabled(elementList.size() < maxListSize);
        firstNameField.setEnabled(elementList.size() < maxListSize);
        firstNameLabel.setEnabled(elementList.size() < maxListSize);
        setButtonsEnabled();
    }

    //Main method
    public static void main(String[] args)
    {
        JFrame      fred = new JFrame();
        ListBuilder bob = new ListBuilder();
        /*String[]    s   =
                          {
                              "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut",
                              "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                              "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan",
                              "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
                              "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                              "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                              "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia",
                              "Wisconsin", "Wyoming"
                          };*/

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        fred.getContentPane().add(bob);
        fred.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fred.pack();

        bob.setMaxListSize(40);
        bob.setDescription("Enter names of people");
        bob.setElementName("Name: ");
        bob.setPresetListsActive(false);
        bob.setNameList(false);
        bob.setTitle("Your Acquaintances");
        bob.setAdjacencyActive(false);
        bob.setListSelections(null);

        bob.setEditable(true);

        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = fred.getSize();

        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }

        fred.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        fred.setVisible(true);
    }

}


/**
 * Implements ListCellRenderer to differentiate between base and custom questions
 */
class SelectionListCellRenderer
    implements ListCellRenderer
{
    protected final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    /**
     *
     *
     * @param list param
     * @param value param
     * @param index param
     * @param isSelected param
     * @param cellHasFocus param
     *
     * @return returns
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus)
    {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected,
                                                                                cellHasFocus);
        renderer.setForeground((((Selection) value).adjacent) ? Color.green : Color.black);

        return renderer;
    }
}



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
package com.endlessloopsoftware.elsutils.layout;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import com.endlessloopsoftware.elsutils.DateUtils;
import com.endlessloopsoftware.elsutils.ELSCalendar;

/**
 * A component that combines a text field and a popup calendar.
 * The user can select a date from the popup calendar, which appears at the
 * user's request. If you make the date chooser editable, then the date chooser
 * includes an editable field into which the user can type a value.
 *
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A combination of a text field and a popup calendar.
 */
public class DateChooser
   extends JComponent
   implements Serializable, ActionListener, ChangeListener, MouseListener, WindowFocusListener
{
   /*
    * Flag which determines wether or not the text field is editable
    *
    * @see #isEditable
    * @see #setEditable
    */
   private boolean isEditable = false;

   /*
    * The rate at which the user scrolls through the months on the popup
    * calendar when the arrow button at the top of the popup calendar are
    * held down.  Units are in months per second.
    *
    * @see #getMonthScrollRate
    * @see #setMonthScrollRate
    */
   private int monthScrollRate = 5;

   /*
    * The date which the user has selected.  Return null of the user chose
    * "NONE".
    *
    * @see #getDate
    * @see #setDate
    */
   private ELSCalendar selectedDate;

   /*
    * The format in which the selected date will appear in the text field.
    *
    * @see #getDateFormat
    * @see #setDateFormat
    * @see #getDateFormatPattern
    * @see #setDateFormatPattern
    */
   private String returnDateFormat = "MMMM dd, yyyy";

   /*
    * Other non-accesored variables
    */
   private JTextField componentTextField;
   private BasicArrowButton componentButton;
   private int componentMargin = 2;
   private JDialog popupCalendar;
   private JButton todayButton;
   private JButton noneButton;
   private JButton cancelButton;
   private JLabel monthButton;
   private JSpinner monthSpinner = new JSpinner();
   private JSpinner yearSpinner = new JSpinner();
   private SimpleDateFormat monthyearFormatter = new SimpleDateFormat("MMMM yyyy");
   private JPanel dayHeaderPanel;
   private JPanel dayPanel;
   private DateLabel[] dayLabel = new DateLabel[42];
   private ELSCalendar displayedCalendar = new ELSCalendar();
   private ELSCalendar todaysCalendar = new ELSCalendar();
   private ELSCalendar selectedCalendar = new ELSCalendar();
   private int todaysYear;
   private int todaysMonth;
   private int todaysDay;
   private int selectedYear;
   private int selectedMonth;
   private int selectedDay;
   private HashMap dayMap = new HashMap();
   private HashMap monthMap = new HashMap();

   private final static List monthStrings = Arrays.asList(DateUtils.getMonthStrings());
   private final static Color selectedColor = new Color(130, 130, 130);

   ///////////////////
   // Constructors
   ///////////////////

   /*
    * Creates a <code>DateChooser</code> with no initial date.
    */
   public DateChooser()
   {
      layoutControl();
      layoutCalendarWindow();
   }

   /*
    * Creates a <code>DateChooser</code> with a text field containing
    * the specified number of columns.
    *
    * @param cols the number of columns in the text field
    */
   public DateChooser(int cols)
   {
      layoutControl();
      layoutCalendarWindow();
      componentTextField.setColumns(cols);
   }

   /*
    * Creates a <code>DateChooser</code> with a specified initially
    * selected date.
    *
    * @param aDate the date which is initially selected and appears
    * in the text field
    */
   public DateChooser(ELSCalendar aDate)
   {
      layoutControl();
      layoutCalendarWindow();
      setDate(aDate);
   }

   /*
    * Creates a <code>DateChooser</code> with a specified initially
    * selected date and a text field containing the specified number
    * of columns.
    *
    * @param aDate the date which is initially selected and appears
    * in the text field
    * @param cols the number of columns in the text field
    */
   public DateChooser(ELSCalendar aDate, int cols)
   {
      layoutControl();
      layoutCalendarWindow();
      setDate(aDate);
      componentTextField.setColumns(cols);
   }

   ///////////////////
   // Public Methods
   ///////////////////
   public ELSCalendar getDate()
   {
      return selectedDate;
   }

   /**
    *
    *
    * @return returns
    */
   public String getDateFormatPattern()
   {
      return returnDateFormat;
   }

   /**
    *
    *
    * @return returns
    */
   public int getMonthScrollRate()
   {
      return monthScrollRate;
   }

   /**
    *
    *
    * @return returns
    */
   public Font getPopupFont()
   {
      Font myFont = monthButton.getFont();

      return myFont;
   }

   /**
    *
    *
    * @return returns
    */
   public Font getTextFont()
   {
      Font myFont = componentTextField.getFont();

      return myFont;
   }

   /**
    *
    *
    * @return returns
    */
   public boolean isEditable()
   {
      return isEditable;
   }

   /**
    *
    *
    * @param myDate param
    */
   public void setDate(ELSCalendar myDate)
   {
      if (myDate == null)
      {
         selectedDate = null;
         selectedCalendar.clear();
         displayedCalendar.setTime(new Date());
         componentTextField.setText("");
      }
      else
      {
         selectedDate = new ELSCalendar(myDate);
         selectedCalendar.setTime(selectedDate);
         displayedCalendar.setTime(selectedDate);
         componentTextField.setText(selectedDate.toString(getDateFormatPattern()));
      }

      selectedYear = selectedCalendar.get(Calendar.YEAR);
      selectedMonth = selectedCalendar.get(Calendar.MONTH);
      selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);
      updateCalendarDisplay();
   }

   /**
    *
    *
    * @param pattern param
    */
   public void setDateFormatPattern(String pattern)
   {
      returnDateFormat = pattern;
   }

   /**
    *
    *
    * @param myEditable param
    */
   public void setEditable(boolean myEditable)
   {
      componentTextField.setEditable(myEditable);
      isEditable = myEditable;
   }

   /**
    *
    *
    * @param myMonthScrollRate param
    */
   public void setMonthScrollRate(int myMonthScrollRate)
   {
      monthScrollRate = myMonthScrollRate;
   }

   /**
    *
    *
    * @param myFont param
    */
   public void setPopupFont(Font myFont)
   {
      monthButton.setFont(myFont);
      todayButton.setFont(myFont);
      noneButton.setFont(myFont);

      Component[] dayHeaderLabels = dayHeaderPanel.getComponents();

      for (int i = 0; i < dayHeaderLabels.length; i++)
      {
         dayHeaderLabels[i].setFont(myFont);
      }

      Component[] dayLabels = dayPanel.getComponents();

      for (int i = 0; i < dayLabels.length; i++)
      {
         dayLabels[i].setFont(myFont);
      }
   }

   /**
    *
    *
    * @param myFont param
    */
   public void setTextFont(Font myFont)
   {
      componentTextField.setFont(myFont);
   }

   ///////////////////
   // Listeners
   ///////////////////
   public void actionPerformed(ActionEvent evt)
   {
      Object source = evt.getSource();

      if (source == componentButton)
      {
         Point myOrigin = componentButton.getLocationOnScreen();
         Dimension buttonSize = componentButton.getSize();
         Dimension bounds = Toolkit.getDefaultToolkit().getScreenSize();
         popupCalendar.pack();

         Dimension mySize = popupCalendar.getSize();
         myOrigin.translate((buttonSize.width - mySize.width), (buttonSize.height));

         if ((myOrigin.y + mySize.height) > bounds.getHeight())
         {
            myOrigin = componentButton.getLocationOnScreen();
            myOrigin.translate((buttonSize.width - mySize.width), -mySize.height);
         }

         popupCalendar.setLocation(myOrigin);
         popupCalendar.setVisible(true);
      }

      if (source == todayButton)
      {
         selectedDate = new ELSCalendar(todaysCalendar);
         firePropertyChange("date", selectedCalendar, selectedDate);
         selectedCalendar.setTime(selectedDate);
         displayedCalendar.setTime(selectedDate);
         selectedYear = selectedCalendar.get(Calendar.YEAR);
         selectedMonth = selectedCalendar.get(Calendar.MONTH);
         selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);

         componentTextField.setText(selectedDate.toString(getDateFormatPattern()));
         popupCalendar.setVisible(false);
         updateCalendarDisplay();
      }

      if (source == noneButton)
      {
         popupCalendar.setVisible(false);
      }
   }

   /* (non-Javadoc)
    * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
    */
   public void stateChanged(ChangeEvent arg0)
   {
      Object source = arg0.getSource();

      if ((source == monthSpinner) || (source == yearSpinner))
      {
         String monthStr = (String) monthSpinner.getValue();
         int month = monthStrings.indexOf(monthStr) + Calendar.JANUARY;
         int year = ((Number) yearSpinner.getValue()).intValue();

         if ((displayedCalendar.get(Calendar.MONTH) != month) || (displayedCalendar.get(Calendar.YEAR) != year))
         {
            displayedCalendar.set(Calendar.MONTH, month);
            displayedCalendar.set(Calendar.YEAR, year);

            updateCalendarDisplay();
         }
      }
   }

   /**
    *
    *
    * @param evt param
    */
   public void mousePressed(MouseEvent evt)
   {
      Object source = evt.getSource();

      if (dayMap.containsKey(source))
      {
         selectedDate = (ELSCalendar) dayMap.get(source);
         firePropertyChange("date", selectedCalendar, selectedDate);
         selectedCalendar.setTime(selectedDate);
         selectedYear = selectedCalendar.get(Calendar.YEAR);
         selectedMonth = selectedCalendar.get(Calendar.MONTH);
         selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);

         componentTextField.setText(selectedDate.toString(getDateFormatPattern()));
         popupCalendar.setVisible(false);
         updateCalendarDisplay();
      }
      else
      {
      }
   }

   /**
    *
    *
    * @param evt param
    */
   public void mouseReleased(MouseEvent evt)
   {
   }

   /**
    *
    *
    * @param evt param
    */
   public void mouseEntered(MouseEvent evt)
   {
      Object source = evt.getSource();

      if (dayMap.containsKey(source))
      {
         DateLabel dayLabel = (DateLabel) source;
         ELSCalendar cal = (ELSCalendar) dayMap.get(source);

         if (!cal.equals(selectedDate))
         {
            //dayLabel.setBackground(Color.lightGray);
            dayLabel.setSelected(true);
         }
      }
   }

   /**
    *
    *
    * @param evt param
    */
   public void mouseExited(MouseEvent evt)
   {
      Object source = evt.getSource();

      if (dayMap.containsKey(source))
      {
         DateLabel dayLabel = (DateLabel) source;
         ELSCalendar cal = (ELSCalendar) dayMap.get(source);

         if (!cal.equals(selectedDate))
         {
            //dayLabel.setBackground(Color.white);
            dayLabel.setSelected(false);
         }
      }
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
    */
   public void windowGainedFocus(WindowEvent arg0)
   {
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
    */
   public void windowLostFocus(WindowEvent arg0)
   {
      popupCalendar.setVisible(false);
   }

   /**
    *
    *
    * @param evt param
    */
   public void mouseClicked(MouseEvent evt)
   {
   }

   ///////////////////
   // Private Methods
   ///////////////////
   private void layoutControl()
   {
      //The visible controls
      BorderLayout componentLayout = new BorderLayout(componentMargin, 0);
      setLayout(componentLayout);
      componentTextField = new JTextField(14);
      componentTextField.setEditable(false);
      add(componentTextField, BorderLayout.CENTER);
      componentButton = new BasicArrowButton(SwingConstants.SOUTH);
      componentButton.addActionListener(this);
      componentButton.setFocusPainted(false);
      add(componentButton, BorderLayout.EAST);

      setBorder(BorderFactory.createEmptyBorder(componentMargin, componentMargin, componentMargin, componentMargin));
      addMouseListener(this);
   }

   /**
    *
    */
   private void layoutCalendarWindow()
   {
      todaysCalendar.setTime(ELSCalendar.now());
      todaysYear = todaysCalendar.get(Calendar.YEAR);
      todaysMonth = todaysCalendar.get(Calendar.MONTH);
      todaysDay = todaysCalendar.get(Calendar.DAY_OF_MONTH);

      selectedDate = null;
      selectedCalendar.setTime(ELSCalendar.now());
      selectedYear = 0;
      selectedMonth = 0;
      selectedDay = 0;

      displayedCalendar.setTime(ELSCalendar.now());

      /* Create Month Spinner */
      SpinnerListModel monthModel = new CyclingSpinnerListModel(monthStrings.toArray());
      monthSpinner = new JSpinner(monthModel);
      monthSpinner.addChangeListener(this);

      //Tweak the spinner's formatted text field.
      JFormattedTextField ftf = ((JSpinner.DefaultEditor) monthSpinner.getEditor()).getTextField();
      if (ftf != null)
      {
         ftf.setColumns(8); //specify more width than we need
         ftf.setHorizontalAlignment(SwingConstants.RIGHT);
      }

      /* Create Year Spinner */
      int currentYear = ELSCalendar.now().get(Calendar.YEAR);
      SpinnerModel yearModel = new SpinnerNumberModel(currentYear, //initial value
                                                         currentYear - 100, //min
                                                         currentYear + 100, //max
                                                         1); //step
      ((CyclingSpinnerListModel) monthModel).setLinkedModel(yearModel);
      yearSpinner.setModel(yearModel);

      //Make the year be formatted without a thousands separator.
      yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
      yearSpinner.addChangeListener(this);

      popupCalendar = new JDialog((Frame) this.getTopLevelAncestor(), true);
      popupCalendar.setUndecorated(true);
      popupCalendar.addWindowFocusListener(this);
      Rectangle bounds = popupCalendar.getBounds();

      JPanel monthPanel = new JPanel(new BorderLayout());
      monthPanel.setBorder(BorderFactory.createEtchedBorder());
      monthPanel.add(monthSpinner, BorderLayout.WEST);
      monthPanel.add(yearSpinner, BorderLayout.EAST);

      dayHeaderPanel = new JPanel(new GridLayout(1, 7, 2, 2));
      dayHeaderPanel.setBackground(Color.white);

      JLabel dayTitleLabel1 = new JLabel("S", SwingConstants.RIGHT);
      dayTitleLabel1.setBackground(Color.white);
      dayTitleLabel1.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel1);

      JLabel dayTitleLabel2 = new JLabel("M", SwingConstants.RIGHT);
      dayTitleLabel2.setBackground(Color.white);
      dayTitleLabel2.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel2);

      JLabel dayTitleLabel3 = new JLabel("T", SwingConstants.RIGHT);
      dayTitleLabel3.setBackground(Color.white);
      dayTitleLabel3.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel3);

      JLabel dayTitleLabel4 = new JLabel("W", SwingConstants.RIGHT);
      dayTitleLabel4.setBackground(Color.white);
      dayTitleLabel4.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel4);

      JLabel dayTitleLabel5 = new JLabel("T", SwingConstants.RIGHT);
      dayTitleLabel5.setBackground(Color.white);
      dayTitleLabel5.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel5);

      JLabel dayTitleLabel6 = new JLabel("F", SwingConstants.RIGHT);
      dayTitleLabel6.setBackground(Color.white);
      dayTitleLabel6.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel6);

      JLabel dayTitleLabel7 = new JLabel("S", SwingConstants.RIGHT);
      dayTitleLabel7.setBackground(Color.white);
      dayTitleLabel7.setForeground(Color.black);
      dayHeaderPanel.add(dayTitleLabel7);

      dayPanel = new JPanel(new GridLayout(6, 7, 2, 2));
      dayPanel.setBackground(Color.white);
      dayPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.black));

      for (int i = 0; i < dayLabel.length; i++)
      {
         dayLabel[i] = new DateLabel("0", SwingConstants.RIGHT);
         dayLabel[i].setOpaque(true);
         dayLabel[i].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
         dayLabel[i].addMouseListener(this);
         dayPanel.add(dayLabel[i]);
      }

      updateCalendarDisplay();

      JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 6, 6));
      buttonPanel.setBackground(Color.white);
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      todayButton = new JButton("Today");
      todayButton.setMargin(new Insets(1, 2, 1, 2));
      todayButton.setFocusPainted(false);
      todayButton.addActionListener(this);
      buttonPanel.add(todayButton);

      noneButton = new JButton("Cancel");
      noneButton.setMargin(new Insets(1, 2, 1, 2));
      noneButton.setFocusPainted(false);
      noneButton.addActionListener(this);
      buttonPanel.add(noneButton);

      GridBagLayout bottomPanelGridBag = new GridBagLayout();
      JPanel bottomPanel = new JPanel(bottomPanelGridBag);
      bottomPanel.setBackground(Color.white);
      bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

      GridBagConstraints bottomPanelConstraints = new GridBagConstraints();
      buildConstraints(bottomPanelConstraints, 0, 0, 1, 1, 1, 0);
      bottomPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      bottomPanelConstraints.anchor = GridBagConstraints.CENTER;
      bottomPanelGridBag.setConstraints(dayHeaderPanel, bottomPanelConstraints);
      bottomPanel.add(dayHeaderPanel);

      buildConstraints(bottomPanelConstraints, 0, 1, 1, 1, 1, 0);
      bottomPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      bottomPanelConstraints.anchor = GridBagConstraints.CENTER;
      bottomPanelGridBag.setConstraints(dayPanel, bottomPanelConstraints);
      bottomPanel.add(dayPanel);

      buildConstraints(bottomPanelConstraints, 0, 2, 1, 1, 0, 0);
      bottomPanelConstraints.fill = GridBagConstraints.BOTH;
      bottomPanelGridBag.setConstraints(buttonPanel, bottomPanelConstraints);
      bottomPanel.add(buttonPanel);

      GridBagLayout mainPanelGridBag = new GridBagLayout();
      JPanel mainPanel = new JPanel(mainPanelGridBag);

      //		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

      GridBagConstraints mainPanelConstraints = new GridBagConstraints();
      buildConstraints(mainPanelConstraints, 0, 0, 1, 1, 0, 0);
      mainPanelConstraints.fill = GridBagConstraints.BOTH;
      mainPanelGridBag.setConstraints(monthPanel, mainPanelConstraints);
      mainPanel.add(monthPanel);

      buildConstraints(mainPanelConstraints, 0, 1, 1, 1, 1, 0);
      mainPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      mainPanelConstraints.anchor = GridBagConstraints.CENTER;
      mainPanelGridBag.setConstraints(bottomPanel, mainPanelConstraints);
      mainPanel.add(bottomPanel);

      Container contentPane = popupCalendar.getContentPane();
      contentPane.add(mainPanel, BorderLayout.CENTER);

      popupCalendar.pack();
   }

   /**
    *
    */
   private void updateCalendarDisplay()
   {
      monthSpinner.setValue(monthStrings.get(displayedCalendar.get(Calendar.MONTH)));
      yearSpinner.setValue(new Integer(displayedCalendar.get(Calendar.YEAR)));

      displayedCalendar.set(Calendar.DAY_OF_MONTH, 1);

      int firstDayOfDisplayedMonth = displayedCalendar.get(Calendar.DAY_OF_WEEK);
      int daysInDisplayedMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      displayedCalendar.roll(Calendar.MONTH, -1);

      int daysInPrevDisplayedMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      displayedCalendar.set(Calendar.DAY_OF_MONTH, daysInPrevDisplayedMonth);

      dayMap.clear();

      if (firstDayOfDisplayedMonth > 1)
      {
         for (int i = (firstDayOfDisplayedMonth - 2); i > (-1); i--)
         {
            dayMap.put(dayLabel[i], displayedCalendar.clone());

            int thisYear = displayedCalendar.get(Calendar.YEAR);
            int thisMonth = displayedCalendar.get(Calendar.MONTH);
            int thisDay = displayedCalendar.get(Calendar.DAY_OF_MONTH);
            dayLabel[i].setText(String.valueOf(thisDay));
            dayLabel[i].setSelected(false);

            if ((thisYear == selectedYear) && (thisMonth == selectedMonth) && (thisDay == selectedDay))
            {
               dayLabel[i].setBackground(selectedColor);
               dayLabel[i].setForeground(Color.white);
            }
            else
            {
               dayLabel[i].setBackground(Color.white);
               dayLabel[i].setForeground(Color.lightGray);
            }

            if ((thisYear == todaysYear) && (thisMonth == todaysMonth) && (thisDay == todaysDay))
            {
               dayLabel[i].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
            }
            else
            {
               dayLabel[i].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            displayedCalendar.roll(Calendar.DAY_OF_MONTH, -1);
         }
      }

      displayedCalendar.roll(Calendar.MONTH, 1);
      displayedCalendar.set(Calendar.DAY_OF_MONTH, 1);

      for (int i = (firstDayOfDisplayedMonth - 1); i < ((firstDayOfDisplayedMonth + daysInDisplayedMonth) - 1); i++)
      {
         dayMap.put(dayLabel[i], displayedCalendar.clone());

         int thisYear = displayedCalendar.get(Calendar.YEAR);
         int thisMonth = displayedCalendar.get(Calendar.MONTH);
         int thisDay = displayedCalendar.get(Calendar.DAY_OF_MONTH);
         dayLabel[i].setText(String.valueOf(thisDay));
         dayLabel[i].setSelected(false);

         if ((thisYear == selectedYear) && (thisMonth == selectedMonth) && (thisDay == selectedDay))
         {
            dayLabel[i].setBackground(selectedColor);
            dayLabel[i].setForeground(Color.white);
         }
         else
         {
            dayLabel[i].setBackground(Color.white);
            dayLabel[i].setForeground(Color.black);
         }

         if ((thisYear == todaysYear) && (thisMonth == todaysMonth) && (thisDay == todaysDay))
         {
            dayLabel[i].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
         }
         else
         {
            dayLabel[i].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
         }

         displayedCalendar.roll(Calendar.DAY_OF_MONTH, 1);
      }

      displayedCalendar.roll(Calendar.MONTH, 1);
      displayedCalendar.set(Calendar.DAY_OF_MONTH, 1);

      for (int i = ((firstDayOfDisplayedMonth + daysInDisplayedMonth) - 1); i < dayLabel.length; i++)
      {
         dayMap.put(dayLabel[i], displayedCalendar.clone());

         int thisYear = displayedCalendar.get(Calendar.YEAR);
         int thisMonth = displayedCalendar.get(Calendar.MONTH);
         int thisDay = displayedCalendar.get(Calendar.DAY_OF_MONTH);
         dayLabel[i].setText(String.valueOf(thisDay));
         dayLabel[i].setSelected(false);

         if ((thisYear == selectedYear) && (thisMonth == selectedMonth) && (thisDay == selectedDay))
         {
            dayLabel[i].setBackground(selectedColor);
            dayLabel[i].setForeground(Color.white);
         }
         else
         {
            dayLabel[i].setBackground(Color.white);
            dayLabel[i].setForeground(Color.lightGray);
         }

         if ((thisYear == todaysYear) && (thisMonth == todaysMonth) && (thisDay == todaysDay))
         {
            dayLabel[i].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
         }
         else
         {
            dayLabel[i].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
         }

         displayedCalendar.roll(Calendar.DAY_OF_MONTH, 1);
      }

      displayedCalendar.roll(Calendar.MONTH, -1);
      displayedCalendar.set(Calendar.DAY_OF_MONTH, 1);

   }

   /**
    *
    *
    * @param gbc param
    * @param gx param
    * @param gy param
    * @param gw param
    * @param gh param
    * @param wx param
    * @param wy param
    */
   private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
   {
      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;
   }

   class DateLabel extends JLabel
   {
      boolean selected = false;

      /**
       * @param arg0
       * @param arg1
       */
      public DateLabel(String arg0, int arg1)
      {
         super(arg0, arg1);
      }

      protected void paintComponent(Graphics g)
      {
         super.paintComponent(g);

         if (this.isSelected())
         {
            Rectangle r = g.getClipBounds();
            g.setColor(Color.blue);
            g.drawRoundRect(r.x, r.y + 1, r.width - 1, r.height - 3, 4, 8);
         }
      }

      /**
       * @return
       */
      public boolean isSelected()
      {
         return selected;
      }

      /**
       * @param b
       */
      public void setSelected(boolean b)
      {
         selected = b;
         this.repaint();
      }
   }

}

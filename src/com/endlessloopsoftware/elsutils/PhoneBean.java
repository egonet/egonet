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

import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PhoneBean
	extends JComponent
	implements Serializable, DocumentListener
{
	private final	JLabel		openLabel		= new JLabel("(");
	private final	JLabel		closeLabel		= new JLabel(")");
	private final	JLabel		hyphenLabel		= new JLabel("-");
	private final	JLabel		extLabel		= new JLabel("ext");
	
	private final	JTextField	areacode 		= new JTextField(3);
	private final	JTextField	exchange 		= new JTextField(3);
	private final	JTextField	number 			= new JTextField(4);
	private final	JTextField	extension		= new JTextField(6);
	//private 		boolean		hasExtension	= false;
	
	private			String		oldNumber		= "";
	private			String		oldExtension	= "";
	
	public PhoneBean(boolean hasExtension)
	{
		FormLayout 		layout 	= new FormLayout("r:p, c:p, l:p, 4dlu, c:p, 2dlu, c:p, 2dlu, c:p, 4dlu, r:p, 2dlu, l:p", "d");
		CellConstraints cc		= new CellConstraints();
		
		this.setLayout(layout);
		
		this.add(openLabel,		cc.xy(1, 1));
		this.add(areacode, 		cc.xy(2, 1));
		this.add(closeLabel,	cc.xy(3, 1));
		this.add(exchange, 		cc.xy(5, 1));
		this.add(hyphenLabel,	cc.xy(7, 1));
		this.add(number, 		cc.xy(9, 1));
		
		//this.hasExtension = hasExtension;
		if (hasExtension) 
		{
			this.add(extLabel,	cc.xy(11, 1));
			this.add(extension,	cc.xy(13, 1));
		}
		
		areacode	.setDocument(new WholeNumberDocument(3));
		areacode	.getDocument().addDocumentListener(this);
		areacode	.getDocument().addDocumentListener(new PhoneDocumentListener(null, exchange, 3));
		
		exchange	.setDocument(new WholeNumberDocument(3));
		exchange	.getDocument().addDocumentListener(this);
		exchange	.getDocument().addDocumentListener(new PhoneDocumentListener(areacode, number, 3));
		
		number		.setDocument(new WholeNumberDocument(4));
		number		.getDocument().addDocumentListener(this);
		
		if (hasExtension)
		{
			number	.getDocument().addDocumentListener(new PhoneDocumentListener(exchange, extension, 4));

			extension	.setDocument(new SizeConstrainedDocument(6));
			extension	.getDocument().addDocumentListener(this);
			extension	.getDocument().addDocumentListener(new PhoneDocumentListener(number, null, 6));
		}
		else
		{
			number	.getDocument().addDocumentListener(new PhoneDocumentListener(exchange, null, 4));
		}		
	}
	
	public void setNumber(int n)
	{
		setNumber(Integer.toString(n), null);
	}
	
	public void setNumber(int number, int extension)
	{
		setNumber(Integer.toString(number), Integer.toString(extension));
	}
	
	public void setNumber(String s)
	{
		setNumber(s, null);
	}

	public void setNumber(String number, String extension)
	{		
		String newNumber	= (number == null) ? "" : number;
		String newExtension	= (extension == null) ? "" : extension;
		
		oldNumber 		= newNumber;
		oldExtension	= newExtension;

		// Start at end and work backwards
		this.extension.setText(newExtension);
		
		// If we get a null, clear out values
		number = newNumber;
		if (number.length() >= 4)
		{
			this.number.setText(number.substring(number.length() - 4));
			number = number.substring(0, number.length() - 4);
		}
		else
		{
			this.number.setText("");
		}
		
		if (number.length() >= 3)
		{
			this.exchange.setText(number.substring(number.length() - 3));
			number = number.substring(0, number.length() - 3);
		}
		else
		{
			this.exchange.setText("");
		}
		
		if (number.length() >= 3)
		{
			this.areacode.setText(number.substring(number.length() - 3));
			number = number.substring(0, number.length() - 3);
		}
		else
		{
			this.areacode.setText("");
		}
	}
	
	public String getExtension()
	{
		return this.extension.getText();
	}

	public String getNumber()
	{
		return this.areacode.getText() + this.exchange.getText() + this.number.getText();
	}

	private void propertyChanged()
	{
		String newNumber 	= getNumber();
		String newExtension	= getExtension();
		
		firePropertyChange("number", oldNumber, newNumber);
		firePropertyChange("extension", oldExtension, newExtension);
		
		oldNumber 		= newNumber;
		oldExtension	= newExtension;
	}

	public void changedUpdate(DocumentEvent arg0) 	{ propertyChanged(); }
	public void insertUpdate(DocumentEvent arg0) 	{ propertyChanged(); }
	public void removeUpdate(DocumentEvent arg0) 	{ propertyChanged(); }
	
	/* (non-Javadoc)
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean arg0)
	{
		super		.setEnabled(arg0);
		areacode	.setEnabled(arg0);
		areacode	.setEditable(arg0);
		exchange	.setEnabled(arg0);
		exchange	.setEditable(arg0);
		number		.setEnabled(arg0);
		number		.setEditable(arg0);
		extension	.setEnabled(arg0);
		extension	.setEditable(arg0);
		openLabel	.setEnabled(arg0);
		closeLabel	.setEnabled(arg0);
		hyphenLabel	.setEnabled(arg0);
		extLabel	.setEnabled(arg0);
	}
	
	class PhoneDocumentListener
		implements DocumentListener
	{
		private JTextField 	follower;
		private JTextField	preceeder;
		private int			maxLength;
		
		public PhoneDocumentListener(JTextField preceeder, JTextField follower, int maxLength)
		{
			this.follower	= follower;
			this.preceeder	= preceeder;
			this.maxLength	= maxLength;
			
			if (this.follower == null)	this.maxLength = Integer.MAX_VALUE;
		}
				
		private void checkLength(Document doc)
		{
			if (doc.getLength() == this.maxLength) follower.requestFocus();
		}

		/* Listeners */
		public void changedUpdate(DocumentEvent arg0)
		{
			checkLength(arg0.getDocument());
		}

		public void insertUpdate(DocumentEvent arg0)
		{
			checkLength(arg0.getDocument());
		}

		public void removeUpdate(DocumentEvent arg0)
		{
			if ((preceeder != null) && (arg0.getDocument().getLength() == 0))
			{
				preceeder.requestFocus(); 
				preceeder.setCaretPosition(preceeder.getDocument().getLength());
			}
		}
	}
}

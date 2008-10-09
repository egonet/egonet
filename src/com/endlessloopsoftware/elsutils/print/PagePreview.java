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
package com.endlessloopsoftware.elsutils.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class PagePreview extends JDialog implements ActionListener
{
	private final static 	double 		DEFAULT_ZOOM_FACTOR_STEP 	= 0.5;
	private 				JLabel		pageNum						= new JLabel("Page #1");
	private					JButton		print;
	protected 				Pageable 	pageable;

	public PagePreview(String title, JFrame owner, Pageable pageable, double zoom)
	{
		super(owner, title, true);
		this.pageable = pageable;
		
		Preview 	preview 	= new Preview(pageable, pageNum, zoom);
		JScrollPane scrollPane 	= new JScrollPane(preview);
		JToolBar 	toolbar 	= new JToolBar();
		
		print = getButton("Print", "print.gif", null);
		print.addActionListener(this);
		JButton close = new JButton("Close");
		close.addActionListener(this);

		getContentPane().add(scrollPane, "Center");

		//toolbar.setRollover(true);
		getContentPane().add(toolbar, "North");
		toolbar.add(getButton("Back24.gif", new BrowseAction(preview, -1)));
		toolbar.add(getButton("Forward24.gif", new BrowseAction(preview, 1)));
		toolbar.add(new JToolBar.Separator());
		toolbar.add(getButton("ZoomIn24.gif", new ZoomAction(preview, DEFAULT_ZOOM_FACTOR_STEP)));
		toolbar.add(getButton("ZoomOut24.gif", new ZoomAction(preview, -DEFAULT_ZOOM_FACTOR_STEP)));
		toolbar.add(new JToolBar.Separator());
		toolbar.add(print);
		toolbar.add(close);
		
		JPanel dialog = new JPanel();
		dialog.setLayout(new FlowLayout(FlowLayout.CENTER));
		dialog.add(pageNum);
		getContentPane().add(dialog, "South");
		
		this.pack();
	}

	public PagePreview(String title, JFrame owner, Pageable pageable)
	{
		this(title, owner, pageable, 0.0);
	}

	public PagePreview(String title, JFrame owner, Printable printable, PageFormat format, int pages, double zoom)
	{
		this(title, owner, new MyPageable(printable, format, pages), zoom);
	}

	public PagePreview(String title, JFrame owner, Printable printable, PageFormat format, int pages)
	{
		this(title, owner, printable, format, pages, 0.0);
	}

	private static class MyPageable implements Pageable
	{
		public MyPageable(Printable printable, PageFormat format, int pages)
		{
			this.printable = printable;
			this.format = format;
			this.pages = pages;
		}

		public int getNumberOfPages()
		{
			return pages;
		}

		public Printable getPrintable(int index)
		{
			if (index >= pages)
				throw new IndexOutOfBoundsException();
			return printable;
		}

		public PageFormat getPageFormat(int index)
		{
			if (index >= pages)
				throw new IndexOutOfBoundsException();
			return format;
		}

		private Printable printable;
		private PageFormat format;
		private int pages;
	}

	public JButton getButton(String iconName)
	{
		return getButton(null, iconName, null);
	}

	private JButton getButton(String iconName, AbstractAction action)
	{
		return getButton(null, iconName, action);
	}

	private JButton getButton(String name, String iconName, AbstractAction action)
	{
		JButton result = null;

		ImageIcon icon = null;
		URL imageURL = PagePreview.class.getResource(iconName);
		if (imageURL != null) icon = new ImageIcon(imageURL);

		if (action != null)
		{
			if (icon != null) action.putValue(Action.SMALL_ICON, new ImageIcon(imageURL));
			if (name != null) action.putValue(Action.NAME, name);
			result = new JButton(action);
		}
		else result = new JButton(name, icon);

		return result;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == print)
		{
			final PrinterJob prnJob = PrinterJob.getPrinterJob();
			PageFormat pf = pageable.getPageFormat(0);
			prnJob.setPrintable(pageable.getPrintable(0), pf);

			if (!prnJob.printDialog()) return;
				
			Thread runner = new Thread()
			{
				public void run()
				{
					try
					{
						prnJob.print();
					}
					catch (PrinterException ex)
					{
						System.err.println("Printing error: " + ex.toString());
						throw new RuntimeException(ex);
					}
				}
			};
			runner.start();
		}
		
		dispose();
	}
}

class Preview extends JComponent
{
	protected Pageable 	pageable;
	protected int 		index = 0;
	protected double 	zoom = 0.0;
	protected JLabel	pageNum;

	private final static int DEFAULT_PREVIEW_SIZE = 300;
	private final static double MINIMUM_ZOOM_FACTOR = 0.1;

	public Preview(Pageable pageable, JLabel pageNum, double zoom)
	{
		this.pageable 	= pageable;
		this.pageNum	= pageNum;
		
		PageFormat format = pageable.getPageFormat(index);
		if (zoom == 0.0)
		{
			if (format.getOrientation() == PageFormat.PORTRAIT) 
			{
				this.zoom = DEFAULT_PREVIEW_SIZE / format.getHeight();
			}
			else
			{
				this.zoom = DEFAULT_PREVIEW_SIZE / format.getWidth();
			}
		}
		else this.zoom = zoom;
		resize();
	}

	protected void paintPaper(Graphics g, PageFormat format)
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, (int) format.getWidth(), (int) format.getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, (int) format.getWidth() - 1, (int) format.getHeight() - 1);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(zoom, zoom);
		try
		{
			PageFormat format = pageable.getPageFormat(index);
			Printable printable = pageable.getPrintable(index);
			
			pageNum.setText("Page " + (index + 1) + " of " + pageable.getNumberOfPages());
			paintPaper(g, format);
			printable.print(g, format, index);
		}
		catch (PrinterException e)
		{
			e.printStackTrace();
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}

	public void moveIndex(int indexStep)
	{
		int newIndex = index + indexStep;
		try
		{
			pageable.getPrintable(newIndex);
			resize();
			index = newIndex;
		}
		catch (IndexOutOfBoundsException ignored)
		{
			ignored.printStackTrace();
		}
	}

	public void changeZoom(double zoom)
	{
		this.zoom = Math.max(MINIMUM_ZOOM_FACTOR, this.zoom + zoom);
		resize();
	}

	public void resize()
	{
		PageFormat format = pageable.getPageFormat(index);
		int size = (int) Math.max(format.getWidth() * zoom, format.getHeight() * zoom);
		setPreferredSize(new Dimension(size, size));
		revalidate();
	}

	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}
}


class BrowseAction extends AbstractAction
{
	protected Preview	preview;
	protected int 		pageStep;

	public BrowseAction(Preview preview, int pageStep)
	{
		super();
		this.preview = preview;
		this.pageStep = pageStep;
	}

	public void actionPerformed(ActionEvent e)
	{
		preview.moveIndex(pageStep);
		preview.repaint();
	}
}

class ZoomAction extends AbstractAction
{
	protected Preview	preview;
	protected double 	zoomStep;

	public ZoomAction(Preview preview, double zoomStep)
	{
		super();
		this.preview = preview;
		this.zoomStep = zoomStep;
	}

	public void actionPerformed(ActionEvent e)
	{
		preview.changeZoom(zoomStep);
		preview.repaint();
	}
}

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
package com.endlessloopsoftware.egonet.web.servlet;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jboss.logging.Logger;

import sun.awt.image.codec.JPEGImageEncoderImpl;

/**
 * A Servlet for displaying swing components
 */

public class SwingServlet extends HttpServlet
{

	public static final Logger	logger	= Logger.getLogger("Network Servlet");

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		logger.debug("DoGet");

		JComponent c = createComponent(req);
		BufferedImage image = convertToGIF(c, req.getParameter("transparent") != null);
		sendAsJPEG(req, res, image);
	}

	/**
	 * Override me!
	 */
	protected JComponent createComponent(HttpServletRequest req) throws ServletException, IOException
	{
		return new JButton("Swing Servlet");
	}

	/**
	 * Paints the swing component to an Image
	 * 
	 * @return The image containing the painted component
	 */
	protected BufferedImage convertToGIF(final JComponent comp, boolean transparent)
	{
		comp.setDoubleBuffered(false);
		JFrame frame = new JFrame();
		frame.setContentPane(comp);
		frame.pack();

		// Get a graphics region, using the Frame
		Dimension size = comp.getSize();
		BufferedImage bImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		final Graphics g = bImage.getGraphics();

		// Workaround for JDK 1.2.2 bug (Linux)
		g.setClip(0, 0, size.width, size.height);

		try
		{
			// Paint the Swing component into the image, being careful to comply
			// with Swing's single thread requirements.

			SwingUtilities.invokeAndWait(new Runnable() {

				public void run()
				{
					comp.paint(g);
				}
			});
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		finally
		{
			g.dispose();
			frame.dispose();
		}

		return bImage;
	}

	protected void sendAsJPEG(HttpServletRequest req, HttpServletResponse res, BufferedImage image) throws IOException
	{
		ServletOutputStream out = res.getOutputStream(); // binary output!

		// Encode the off screen image into a jpeg and send it to the client
		res.setContentType("image/jpeg");

		JPEGImageEncoderImpl jpegEncoder = new JPEGImageEncoderImpl(out);
		jpegEncoder.encode(image);

		out.close();
	}
}
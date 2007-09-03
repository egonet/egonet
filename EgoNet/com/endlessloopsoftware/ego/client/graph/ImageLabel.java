package com.endlessloopsoftware.ego.client.graph;
//
import java.awt.Canvas;
//import java.net.*;
//
//// This appears in Core Web Programming from
//// Prentice Hall Publishers, and may be freely used
//// or adapted. 1997 Marty Hall, hall@apl.jhu.edu.
//
////======================================================
///**
// * A class for displaying images. It places the Image into a canvas so that it can moved around by layout managers, will get repainted automatically, etc. No
// * mouseXXX or action events are defined, so it is most similar to the Label Component.
// * <P>
// * By default, with FlowLayout the ImageLabel takes its minimum size (just enclosing the image). The default with BorderLayout is to expand to fill the region
// * in width (North/South), height (East/West) or both (Center). This is the same behavior as with the builtin Label class. If you give an explicit resize or
// * reshape call <B>before</B> adding the ImageLabel to the Container, this size will override the defaults.
// * <P>
// * Here is an example of its use:
// * <P>
// * 
// * <PRE>
// * 
// * public class ShowImages extends Applet { private ImageLabel image1, image2;
// * 
// * public void init() { image1 = new ImageLabel(getCodeBase(), "some-image.gif"); image2 = new ImageLabel(getCodeBase(), "other-image.jpg"); add(image1);
// * add(image2); } }
// * 
// * </PRE>
// * 
// * @author Marty Hall (hall@apl.jhu.edu)
// * @see Icon
// * @see ImageButton
// * @version 1.0 (1997)
// */
//
public class ImageLabel extends Canvas
{
//	//----------------------------------------------------
//	// Instance variables.
//
//	// The actual Image drawn on the canvas.
//	private Image image;
//
//	// A String corresponding to the URL of the image
//	// you will get if you call the constructor with
//	// no arguments.
//	private static String defaultImageString = "http://www.endlessloopsoftware.com/eLoop_web.png";
//
//	// The URL of the image. But sometimes we will use
//	// an existing image object (e.g. made by
//	// createImage) for which this info will not be
//	// available, so a default string is used here.
//	private String imageString = "<Existing Image>";
//
//	// Turn this on to get verbose debugging messages.
//	private boolean debug = false;
//
//	/** Amount of extra space around the image. */
//	private int border = 0;
//
//	/**
//	 * If there is a non-zero border, what color should it be? Default is to use the background color of the Container.
//	 */
//	private Color borderColor = null;
//
//	// Width and height of the Canvas. This is the
//	//  width/height of the image plus twice the border.
//	private int width, height;
//
//	/**
//	 * Determines if it will be sized automatically. If the user issues a resize() or reshape() call before adding the label to the Container, or if the
//	 * LayoutManager resizes before drawing (as with BorderLayout), then those sizes override the default, which is to make the label the same size as the image
//	 * it holds (after reserving space for the border, if any). This flag notes this, so subclasses that override ImageLabel need to check this flag, and if it
//	 * is true, and they draw modified image, then they need to draw them based on the width height variables, not just blindly drawing them full size.
//	 */
//	private boolean explicitSize = false;
//	private int explicitWidth = 0, explicitHeight = 0;
//
//	// The MediaTracker that can tell if image has been
//	// loaded before trying to paint it or resize
//	// based on its size.
//	private MediaTracker tracker;
//
//	// Used by MediaTracker to be sure image is loaded
//	// before paint & resize, since you can't find out
//	// the size until it is done loading.
//	private static int lastTrackerID = 0;
//	private int currentTrackerID;
//	private boolean doneLoading = false;
//
//	private Container parentContainer;
//
//	//----------------------------------------------------
//	/**
//	 * Create an ImageLabel with the default image.
//	 * 
//	 * @see #getDefaultImageString
//	 * @see #setDefaultImageString
//	 */
//	// Remember that the funny "this()" syntax calls
//	// constructor of same class
//	public ImageLabel()
//	{
//		this(defaultImageString);
//	}
//
//	/**
//	 * Create an ImageLabel using the image at URL specified by the string.
//	 * 
//	 * @param imageURLString
//	 *           A String specifying the URL of the image.
//	 */
//	public ImageLabel(String imageURLString)
//	{
//		this(makeURL(imageURLString));
//	}
//
//	/**
//	 * Create an ImageLabel using the image at URL specified.
//	 * 
//	 * @param imageURL
//	 *           The URL of the image.
//	 */
//	public ImageLabel(URL imageURL)
//	{
//		this(loadImage(imageURL));
//		imageString = imageURL.toExternalForm();
//	}
//
//	/**
//	 * Create an ImageLabel using the image in the file in the specified directory.
//	 * 
//	 * @param imageDirectory
//	 *           Directory containing image
//	 * @param file
//	 *           Filename of image
//	 */
//	public ImageLabel(URL imageDirectory, String file)
//	{
//		this(makeURL(imageDirectory, file));
//		imageString = file;
//	}
//
//	/**
//	 * Create an ImageLabel using the image specified. The other constructors eventually call this one, but you may want to call it directly if you already have
//	 * an image (e.g. created via createImage).
//	 * 
//	 * @param image
//	 *           The image
//	 */
//	public ImageLabel(Image image)
//	{
//		this.image = image;
//		tracker = new MediaTracker(this);
//		currentTrackerID = lastTrackerID++;
//		tracker.addImage(image, currentTrackerID);
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Makes sure that the Image associated with the Canvas is done loading before returning, since loadImage spins off a separate thread to do the loading.
//	 * Once you get around to drawing the image, this will make sure it is loaded, waiting if not. The user does not need to call this at all, but if several
//	 * ImageLabels are used in the same Container, this can cause several repeated layouts, so users might want to explicitly call this themselves before adding
//	 * the ImageLabel to the Container. Another alternative is to start asynchronous loading by calling prepareImage on the ImageLabel's image (see getImage).
//	 * 
//	 * @param doLayout
//	 *           Determines if the Container should be re-laid out after you are finished waiting. <B>This should be true when called from user functions</B>,
//	 *           but is set to false when called from preferredSize to avoid an infinite loop. This is needed when using BorderLayout, which calls preferredSize
//	 *           <B>before</B> calling paint.
//	 */
//	public void waitForImage(boolean doLayout)
//	{
//		if (!doneLoading)
//		{
//			debug("[waitForImage] - Resizing and waiting for " + imageString);
//			try
//			{
//				tracker.waitForID(currentTrackerID);
//			}
//			catch (InterruptedException ie)
//			{
//			}
//			catch (Exception e)
//			{
//				System.out.println("Error loading " + imageString + ": " + e.getMessage());
//				e.printStackTrace();
//			}
//			if (tracker.isErrorID(0))
//				new Throwable("Error loading image " + imageString).printStackTrace();
//			doneLoading = true;
//			if (explicitWidth != 0)
//				width = explicitWidth;
//			else
//				width = image.getWidth(this) + 2 * border;
//			if (explicitHeight != 0)
//				height = explicitHeight;
//			else
//				height = image.getHeight(this) + 2 * border;
//			resize(width, height);
//			debug("[waitForImage] - " + imageString + " is " + width + "x" + height + ".");
//
//			// If no parent, you are OK, since it will have
//			// been resized before being added. But if
//			// parent exists, you have already been added,
//			// and the change in size requires re-layout.
//			if (((parentContainer = getParent()) != null) && doLayout)
//			{
//				setBackground(parentContainer.getBackground());
//				parentContainer.layout();
//			}
//		}
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Moves the image so that it is <I>centered</I> at the specified location, as opposed to the move method of Component which places the top left corner at
//	 * the specified location.
//	 * <P>
//	 * <B>Note:</B> The effects of this could be undone by the LayoutManager of the parent Container, if it is using one. So this is normally only used in
//	 * conjunction with a null LayoutManager.
//	 * 
//	 * @param x
//	 *           The X coord of center of the image (in parent's coordinate system)
//	 * @param y
//	 *           The Y coord of center of the image (in parent's coordinate system)
//	 * @see java.awt.Component#move
//	 */
//
//	public void centerAt(int x, int y)
//	{
//		debug("Placing center of " + imageString + " at (" + x + "," + y + ")");
//		move(x - width / 2, y - height / 2);
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Determines if the x and y <B>(in the ImageLabel's own coordinate system)</B> is inside the ImageLabel. Put here because Netscape 2.02 has a bug in
//	 * which it doesn't process inside() and locate() tests correctly.
//	 */
//	public synchronized boolean inside(int x, int y)
//	{
//		return ((x >= 0) && (x <= width) && (y >= 0) && (y <= height));
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Draws the image. If you override this in a subclass, be sure to call super.paint.
//	 */
//	public void paint(Graphics g)
//	{
//		if (!doneLoading)
//			waitForImage(true);
//		else
//		{
//			if (explicitSize)
//				g.drawImage(image, border, border, width - 2 * border, height - 2 * border, this);
//			else
//				g.drawImage(image, border, border, this);
//			drawRect(g, 0, 0, width - 1, height - 1, border, borderColor);
//		}
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Used by layout managers to calculate the usual size allocated for the Component. Since some layout managers (e.g. BorderLayout) may call this before
//	 * paint is called, you need to make sure that the image is done loading, which will force a resize, which determines the values returned.
//	 */
//	public Dimension preferredSize()
//	{
//		if (!doneLoading)
//			waitForImage(false);
//		return (super.preferredSize());
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Used by layout managers to calculate the smallest size allocated for the Component. Since some layout managers (e.g. BorderLayout) may call this before
//	 * paint is called, you need to make sure that the image is done loading, which will force a resize, which determines the values returned.
//	 */
//	public Dimension minimumSize()
//	{
//		if (!doneLoading)
//			waitForImage(false);
//		return (super.minimumSize());
//	}
//
//	//----------------------------------------------------
//	// LayoutManagers (such as BorderLayout) might call
//	// resize or reshape with only 1 dimension of
//	// width/height non-zero. In such a case, you still
//	// want the other dimension to come from the image
//	// itself.
//
//	/**
//	 * Resizes the ImageLabel. If you don't resize the label explicitly, then what happens depends on the layout manager. With FlowLayout, as with FlowLayout
//	 * for Labels, the ImageLabel takes its minimum size, just enclosing the image. With BorderLayout, as with BorderLayout for Labels, the ImageLabel is
//	 * expanded to fill the section. Stretching GIF/JPG files does not always result in clear looking images. <B>So just as with builtin Labels and Buttons,
//	 * don't use FlowLayout if you don't want the Buttons to get resized.</B> If you don't use any LayoutManager, then the ImageLabel will also just fit the
//	 * image.
//	 * <P>
//	 * Note that if you resize explicitly, you must do it <B>before</B> the ImageLabel is added to the Container. In such a case, the explicit size overrides
//	 * the image dimensions.
//	 * 
//	 * @see #reshape
//	 */
//	public void resize(int width, int height)
//	{
//		if (!doneLoading)
//		{
//			explicitSize = true;
//			if (width > 0)
//				explicitWidth = width;
//			if (height > 0)
//				explicitHeight = height;
//		}
//		super.resize(width, height);
//	}
//
//	/**
//	 * Resizes the ImageLabel. If you don't resize the label explicitly, then what happens depends on the layout manager. With FlowLayout, as with FlowLayout
//	 * for Labels, the ImageLabel takes its minimum size, just enclosing the image. With BorderLayout, as with BorderLayout for Labels, the ImageLabel is
//	 * expanded to fill the section. Stretching GIF/JPG files does not always result in clear looking images. <B>So just as with builtin Labels and Buttons,
//	 * don't use FlowLayout if you don't want the Buttons to get resized.</B> If you don't use any LayoutManager, then the ImageLabel will also just fit the
//	 * image.
//	 * <P>
//	 * Note that if you resize explicitly, you must do it <B>before</B> the ImageLabel is added to the Container. In such a case, the explicit size overrides
//	 * the image dimensions.
//	 * 
//	 * @see #resize
//	 */
//	public void reshape(int x, int y, int width, int height)
//	{
//		if (!doneLoading)
//		{
//			explicitSize = true;
//			if (width > 0)
//				explicitWidth = width;
//			if (height > 0)
//				explicitHeight = height;
//		}
//		super.reshape(x, y, width, height);
//	}
//
//	//----------------------------------------------------
//	// You can't just set the background color to
//	// the borderColor and skip drawing the border,
//	// since it messes up transparent gifs. You
//	// need the background color to be the same as
//	// the container.
//
//	/**
//	 * Draws a rectangle with the specified OUTSIDE left, top, width, and height. Used to draw the border.
//	 */
//	protected void drawRect(
//		Graphics g,
//		int left,
//		int top,
//		int width,
//		int height,
//		int lineThickness,
//		Color rectangleColor)
//	{
//		g.setColor(rectangleColor);
//		for (int i = 0; i < lineThickness; i++)
//		{
//			g.drawRect(left, top, width, height);
//			if (i < lineThickness - 1)
//			{ // Skip last iteration
//				left = left + 1;
//				top = top + 1;
//				width = width - 2;
//				height = height - 2;
//			}
//		}
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Calls System.out.println if the debug variable is true; does nothing otherwise.
//	 * 
//	 * @param message
//	 *           The String to be printed.
//	 */
//	protected void debug(String message)
//	{
//		if (debug)
//			System.out.println(message);
//	}
//
//	//----------------------------------------------------
//	// Creates the URL with some error checking.
//
//	private static URL makeURL(String s)
//	{
//		URL u = null;
//		try
//		{
//			u = new URL(s);
//		}
//		catch (MalformedURLException mue)
//		{
//			System.out.println("Bad URL " + s + ": " + mue);
//			mue.printStackTrace();
//		}
//		return (u);
//	}
//
//	private static URL makeURL(URL directory, String file)
//	{
//		URL u = null;
//		try
//		{
//			u = new URL(directory, file);
//		}
//		catch (MalformedURLException mue)
//		{
//			System.out.println("Bad URL " + directory.toExternalForm() + ", " + file + ": " + mue);
//			mue.printStackTrace();
//		}
//		return (u);
//	}
//
//	//----------------------------------------------------
//	// Loads the image. Needs to be static since it is
//	// called by the constructor.
//
//	private static Image loadImage(URL url)
//	{
//		return (Toolkit.getDefaultToolkit().getImage(url));
//	}
//
//	//----------------------------------------------------
//	/** The Image associated with the ImageLabel. */
//
//	public Image getImage()
//	{
//		return (image);
//	}
//
//	//----------------------------------------------------
//	/** Gets the border width. */
//
//	public int getBorder()
//	{
//		return (border);
//	}
//
//	/** Sets the border thickness. */
//
//	public void setBorder(int border)
//	{
//		this.border = border;
//	}
//
//	//----------------------------------------------------
//	/** Gets the border color. */
//
//	public Color getBorderColor()
//	{
//		return (borderColor);
//	}
//
//	/** Sets the border color. */
//
//	public void setBorderColor(Color borderColor)
//	{
//		this.borderColor = borderColor;
//	}
//
//	//----------------------------------------------------
//	// You could just call size().width and size().height,
//	// but since we've overridden resize to record
//	// this, we might as well use it.
//
//	/** Gets the width (image width plus twice border). */
//
//	public int getWidth()
//	{
//		return (width);
//	}
//
//	/** Gets the height (image height plus 2x border). */
//
//	public int getHeight()
//	{
//		return (height);
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Has the ImageLabel been given an explicit size? This is used to decide if the image should be stretched or not. This will be true if you call resize or
//	 * reshape on the ImageLabel before adding it to a Container. It will be false otherwise.
//	 */
//	protected boolean hasExplicitSize()
//	{
//		return (explicitSize);
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Returns the string representing the URL that will be used if none is supplied in the constructor.
//	 */
//	public static String getDefaultImageString()
//	{
//		return (defaultImageString);
//	}
//
//	/**
//	 * Sets the string representing the URL that will be used if none is supplied in the constructor. Note that this is static, so is shared by all ImageLabels.
//	 * Using this might be convenient in testing, but "real" applications should avoid it.
//	 */
//	public static void setDefaultImageString(String file)
//	{
//		defaultImageString = file;
//	}
//
//	//----------------------------------------------------
//	/**
//	 * Returns the string representing the URL of image.
//	 */
//	protected String getImageString()
//	{
//		return (imageString);
//	}
//
//	//----------------------------------------------------
//	/** Is the debugging flag set? */
//
//	public boolean isDebugging()
//	{
//		return (debug);
//	}
//
//	/**
//	 * Set the debugging flag. Verbose messages will be printed to System.out if this is true.
//	 */
//	public void setIsDebugging(boolean debug)
//	{
//		this.debug = debug;
//	}
//
	//----------------------------------------------------
}

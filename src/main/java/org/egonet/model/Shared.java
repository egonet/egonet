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
package org.egonet.model;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Window;
import java.io.Serializable;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.egonet.model.answer.*;
import org.egonet.model.question.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author admin
 *
 */
public class Shared
{
	final private static Logger logger = LoggerFactory.getLogger(Shared.class);

	@SuppressWarnings("unchecked")
	public final static Class<? extends Question> questionClasses[] = new Class[]{
			StudyQuestion.class,
			EgoQuestion.class,
			AlterQuestion.class,
			AlterPromptQuestion.class,
			AlterPairQuestion.class
	}; 
	
	@SuppressWarnings("unchecked")
	public final static Class<? extends Answer> answerClasses[] = new Class[]{
			CategoricalAnswer.class,
			TextAnswer.class,
			NumericalAnswer.class,
			InformationalAnswer.class,
	}; 
	
	   public enum AlterSamplingModel
	   {
		   ALL,
		   RANDOM_SUBSET,
		   NTH_ALTER;
	   }

	   public enum AlterNameModel
	   {
		   FIRST_LAST,
		   SINGLE;
		   
	   }

	
	/* Constants */
	public static final int MIN_QUESTION_TYPE			= 1;
   public static final int ERROR                      = -1;
	public static final int STUDY_CONFIG					   = 0;
	public static final int EGO_QUESTION 					= 1;
	public static final int ALTER_PROMPT 					= 2;
	public static final int ALTER_QUESTION 				   = 3;
	public static final int ALTER_PAIR_QUESTION 			= 4;
   public static final int CORRECTION                 = 5;
	public static final int COMPLETE							= 6;
	public static final int NUM_QUESTION_TYPES			= 5;
	public static final int ALL_QUESTION_TYPES		   = 5;
	public static final int MAX_QUESTION_TYPE			= 4;

	public static final int MIN_ANSWER_TYPE 			= 0;
	public static final int CATEGORICAL 					   = 0;
	public static final int NUMERICAL						   = 1;
	public static final int TEXT								= 2;
	public static final int INFORMATIONAL						= 3;
	public static final int MAX_ANSWER_TYPE 			= 3;
	
	public static final int 		   NO_ALTER				= -1;
   
   /* UI Types */
   public final static String    TRADITIONAL_QUESTIONS  = "Traditional";
   public final static String    PAIR_ELICITATION       = "Applet Linking";
   public final static String    THREE_STEP_ELICITATION = "Three Step Elicitation";

   /* Dynamic question placeholders */
   public final static Long      GENERIC_ALTER_PROMPT   = new Long(45);
   public final static Long      GENERIC_CORRECTION     = new Long(49);
   public final static Long      GENERIC_APPLET_LINK    = new Long(54);

   /* For dynamic Alter Prompts */
   public final static class AlterPromptType
      implements Serializable
   {
      private final String _s;
      public AlterPromptType(String s) {_s = s;}
      public String toString()         {return _s;}
      public boolean equals(Object obj){return ((AlterPromptType) obj).toString().equals(toString());}
   }
   public final static AlterPromptType NOT_ALTER_PROMPT = new AlterPromptType("Not An Alter Prompt");
   public final static AlterPromptType LINK_TO_NONE     = new AlterPromptType("Link To None");
   public final static AlterPromptType LINK_TO_NEXT     = new AlterPromptType("Link To Next");
   public final static AlterPromptType LINK_TO_PRIOR    = new AlterPromptType("Link To Prior");
   public final static AlterPromptType LINK_PAIR        = new AlterPromptType("Link Pair");
   
   public static String getTypeName(int type)
	{
		switch (type)
		{
			case EGO_QUESTION:
				return "Ego";
				
			case ALTER_PROMPT:
				return "Alter Prompt";
				
			case ALTER_QUESTION:
				return "Alter Question";
				
			case ALTER_PAIR_QUESTION:
				return "Alter Pair Question";
				
         case CORRECTION:
            return "Correction Question";
            
			default:
				return "Unknown " + type;
		}
	}
	
	public static int toInt(String s)
	{
		int n = -1;
		try 
		{ 
			n = Integer.parseInt(s); 
		} 
		catch(NumberFormatException nfe){} 
		
		return n;
	}
   
   /* For Three Part Elicitations, determines next state machine state */
   public static AlterPromptType getNextPromptType(AlterPromptType type, String UIType)
   {
      AlterPromptType rval = Shared.NOT_ALTER_PROMPT;
      logger.info("getNextPromptType: " + type);
      
      if (type.equals(Shared.LINK_TO_NEXT))
      {
         logger.info("equals " + Shared.LINK_TO_NEXT);
         rval = Shared.LINK_TO_PRIOR;
      }
      else if (type.equals(Shared.LINK_TO_PRIOR))
      {
         logger.info("equals " + Shared.LINK_TO_PRIOR);
         rval = Shared.LINK_TO_NEXT;
      }
      else if (type.equals(Shared.NOT_ALTER_PROMPT))
      {
         logger.info("equals " + Shared.NOT_ALTER_PROMPT);
         rval = UIType.equals(Shared.PAIR_ELICITATION) ? Shared.LINK_PAIR : Shared.LINK_TO_NEXT;
      }
      else
      {
         logger.info("Fell through with " + type);
         rval = Shared.LINK_TO_NONE;
      }
      
      logger.info("Returning: " + rval);
      
      return rval;
   }

public static final String 			version 	= "2.0 Beta 9 (7 Apr 2004)";
/**
* Configures the UI; tries to set the system look on Mac,
* <code>ExtWindowsLookAndFeel</code> on general Windows, and
* <code>Plastic3DLookAndFeel</code> on Windows XP and all other OS.<p>
* 
* The JGoodies Swing Suite's <code>ApplicationStarter</code>,
* <code>ExtUIManager</code>, and <code>LookChoiceStrategies</code>
* classes provide a much more fine grained algorithm to choose and
* restore a look and theme.
*/
/* Added by sonam : 08/20.2007 */

    public static final String USE_SYSTEM_FONTS_APP_KEY = "Application.useSystemFontSettings";

    public static void setWaitCursor(JFrame frame, boolean waitCursor)
    {
        if (waitCursor)
        {
            frame.getGlassPane().setVisible(true);
            frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        else
        {
            frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            frame.getGlassPane().setVisible(false);
        }
    }
    
    public static void displayAboutBox(Window parent)
    {
        String msg = "<html>";
        
        msg += "<p>Egonet is a tool for studying personal networks.</p>";
        
        msg += "<p>Thanks to:</p>";
        msg += "<p>Dr. Chris McCarty";
        msg += "<br/><a href=\"mailto:ufchris@ufl.edu\">ufchris@ufl.edu</a>";
        msg += "<br/>University of Florida</p>";

        msg += "<p>Egonet is hosted at SourceForge.net and Github.com.</p>";
        msg += "<p>&nbsp;</p>";
            
        JEditorPane editor = new JEditorPane();
        editor.setEditorKit(new HTMLEditorKit());
        editor.setText(msg);
        editor.setEditable(false);
        editor.setOpaque(false);
        editor.setBorder(null);
        
        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
        ((HTMLDocument)editor.getDocument()).getStyleSheet().addRule(bodyRule);
        
        
        editor.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e)
                {
                    if(!e.getEventType().equals(EventType.ACTIVATED))
                        return;
                    
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Throwable cause) {}
                }
        });
        JOptionPane.showMessageDialog(parent, editor, "About Egonet", JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: Shared.java,v 1.6 2004/05/17 00:05:24 admin Exp $
 */

package com.endlessloopsoftware.egonet;

import java.io.Serializable;

/**
 * @author admin
 *
 */
public class Shared
{
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
	public static final int MAX_ANSWER_TYPE 			= 2;
	
	public static final int 		   NO_ALTER				= -1;
	public static final AlterPair NO_ALTERS				= new AlterPair();
   
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
      System.out.println("getNextPromptType: " + type);
      
      if (type.equals(Shared.LINK_TO_NEXT))
      {
         System.out.println("equals " + Shared.LINK_TO_NEXT);
         rval = Shared.LINK_TO_PRIOR;
      }
      else if (type.equals(Shared.LINK_TO_PRIOR))
      {
         System.out.println("equals " + Shared.LINK_TO_PRIOR);
         rval = Shared.LINK_TO_NEXT;
      }
      else if (type.equals(Shared.NOT_ALTER_PROMPT))
      {
         System.out.println("equals " + Shared.NOT_ALTER_PROMPT);
         rval = UIType.equals(Shared.PAIR_ELICITATION) ? Shared.LINK_PAIR : Shared.LINK_TO_NEXT;
      }
      else
      {
         System.out.println("Fell through with " + type);
         rval = Shared.LINK_TO_NONE;
      }
      
      System.out.println("Returning: " + rval);
      
      return rval;
   }
}

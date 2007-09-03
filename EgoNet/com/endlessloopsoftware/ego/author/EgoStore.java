package com.endlessloopsoftware.ego.author;


/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id$
 *
 *
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.elsutils.DateUtils;
import com.endlessloopsoftware.elsutils.files.DirList;
import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.elsutils.files.FileReadException;
import com.endlessloopsoftware.elsutils.files.FileWriteException;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;

/****
 * Handles IO for the EgoNet program
 * Tracks data files and changes to those files
 */
public class EgoStore
{
   private File      studyFile      = null;
   private boolean   studyFileInUse = false;

	private static final String[]	questionExtensions	= { "qst", "qtp"};
	private static FileFilter		readQuestionFilter	= (FileFilter) new ExtensionFileFilter("Question Files", questionExtensions[0]);
	private static FileFilter		writeQuestionFilter	= (FileFilter) new ExtensionFileFilter("Question Templates", questionExtensions);
	private static FileFilter		studyFilter				= new ExtensionFileFilter("Study Files", "ego");
   
   private static final String FILE_PREF = "FILE_PREF";

   /**
    * Sets parent frame
    * 
    * @param frame
    *            parent
    */
   public EgoStore()
   {
   }

   /************************************************************************************************************************************************************
    * Returns study file
    * 
    * @return studyFile file containing study overview information
    */
   public File getStudyFile()
   {
      return (studyFile);
   }

   /************************************************************************************************************************************************************
    * Returns study file
    * 
    * @return studyFile file containing study overview information
    */
   public boolean getStudyInUse()
   {
      return (studyFileInUse);
   }

   /************************************************************************************************************************************************************
    * Sets baseQuestionFile variable and notifies observers of change to study
    * 
    * @param f
    *            question file
    */
   public void setStudyFile(File f)
   {
      studyFile = f;
   }

   /************************************************************************************************************************************************************
    * Select a directory in which to store project related files Create subdirectories if needed.
    */
   public void newStudyFiles()
   {
      Preferences    prefs = Preferences.userNodeForPackage(EgoNet.class);
      JFileChooser   jNewStudyChooser = new JFileChooser();
      File           dirFile, newStudyFile;
      String         projectPath = null;
      String         projectName = null;

      jNewStudyChooser.addChoosableFileFilter(studyFilter);
      jNewStudyChooser.setDialogTitle("Select Study Path");

      if (getStudyFile() != null)
      {
         jNewStudyChooser.setCurrentDirectory(getStudyFile().getParentFile());
      }
      else
      {
         File directory = new File(prefs.get(FILE_PREF, "."));
         jNewStudyChooser.setCurrentDirectory(directory);
      }

      try
      {
         if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showSaveDialog(EgoNet.frame))
         {
            projectPath = jNewStudyChooser.getSelectedFile().getParent();
            projectName = jNewStudyChooser.getSelectedFile().getName();

            if (projectName.indexOf(".") != -1)
            {
               projectName = projectName.substring(0, projectName.indexOf("."));
            }

            try
            {
               String folder = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1);
               if (!folder.equals(projectName))
               {
                  dirFile = new File(projectPath, projectName);
                  dirFile.mkdir();
                  projectPath = dirFile.getPath();
               }
            }
            catch (SecurityException e)
            {
            	JOptionPane.showMessageDialog(
            			EgoNet.frame,
            			"Unable to create study directories.",
						"New Study Error",
						JOptionPane.ERROR_MESSAGE);
            	throw new FileCreateException(false);
            }

            try
            {
               newStudyFile = new File(projectPath, projectName);
               newStudyFile = ((ExtensionFileFilter) studyFilter).getCorrectFileName(newStudyFile);
               if (!newStudyFile.createNewFile())
               {
                  int confirm =
                     JOptionPane.showConfirmDialog(
                     	EgoNet.frame,
                        "<HTML><h2>Study already exists at this location.</h2>" + "<p>Shall I overwrite it?</p></html>",
                        "Overwrite Study File",
                        JOptionPane.OK_CANCEL_OPTION);

                  if (confirm != JOptionPane.OK_OPTION)
                  {
                     throw new FileCreateException(true);
                  }
               }

               /* Clean out study variables */
               EgoNet.study = new Study();
               setStudyFile(newStudyFile);
               EgoNet.study.setStudyName(projectName);

               /* Write out default info */
               writeStudy(newStudyFile, new Long(System.currentTimeMillis()));
               studyFileInUse = false;

               // Store location in prefs file
               prefs.put(FILE_PREF, newStudyFile.getParent());
            }
            catch (java.io.IOException e)
            {
               JOptionPane.showMessageDialog(
               		EgoNet.frame,
               		"Unable to create study file.",
					"File Error",
					JOptionPane.ERROR_MESSAGE);
               throw new FileCreateException(false);
            }

            try
            {
               dirFile = new File(projectPath, "Statistics");
               dirFile.mkdir();

               dirFile = new File(projectPath, "Interviews");
               dirFile.mkdir();
            }
            catch (SecurityException e)
            {
            	JOptionPane.showMessageDialog(
            		EgoNet.frame,
            		"Unable to create study directories.",
					"New Study Error",
					JOptionPane.ERROR_MESSAGE);
            	throw new FileCreateException(false);
            }
         }
      }
      catch (FileCreateException e)
      {
         if (e.report)
         {
            JOptionPane.showMessageDialog(EgoNet.frame, "Study not created.");
         }

         setStudyFile(null);
      }
   }

   /************************************************************************************************************************************************************
    * Select a directory in which to store project related files Create subdirectories if needed.
    */
   public void selectStudy()
   {
      Preferences    prefs             = Preferences.userNodeForPackage(EgoNet.class);
      JFileChooser   jNewStudyChooser  = new JFileChooser();
      File f;

      jNewStudyChooser.addChoosableFileFilter(studyFilter);
      jNewStudyChooser.setDialogTitle("Select Study");

      if (getStudyFile() != null)
      {
         jNewStudyChooser.setCurrentDirectory(getStudyFile().getParentFile());
      }
      else
      {
         jNewStudyChooser.setCurrentDirectory(new File(prefs.get(FILE_PREF, ".")));
      }

      if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(EgoNet.frame))
      {
         f = jNewStudyChooser.getSelectedFile();

         try
         {
            if (!f.canRead())
            {
               throw new FileReadException();
            }
            else
            {
               readStudy(f);
               setStudyFile(f);

               // Store location in prefs file
               prefs.put(FILE_PREF, f.getParent());
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();

            setStudyFile(null);
            JOptionPane.showMessageDialog(null, "Unable to read study file.", "File Error", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   /************************************************************************************************************************************************************
    * Select a question file to use for custom questions
    */
   public void importQuestions()
   {
      JFileChooser jNewStudyChooser = new JFileChooser();
      File 				newFile;
      FileReader 	file = null;
      Study 			newStudy = null;

      jNewStudyChooser.setCurrentDirectory(DirList.getLibraryDirectory());
      jNewStudyChooser.addChoosableFileFilter(readQuestionFilter);
      jNewStudyChooser.setDialogTitle("Select Custom Questions File");

      if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(EgoNet.frame))
      {
         newFile = jNewStudyChooser.getSelectedFile();

         try
         {
            if (!newFile.canRead())
            {
               throw (new FileReadException());
            }

            /* read question file here */
            Document document = new Document(newFile);
            readQuestions(document);
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(
               null,
               "Unable to read question file.",
               "File Error",
               JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   /************************************************************************************************************************************************************
    * Save study information to a file with a new name
    */
   public void saveStudyFile()
   {
      FileWriter file = null;
      PrintWriter out = null;
      File studyFile = getStudyFile();

      try
      {
         if (!studyFile.canWrite())
         {
            throw (new FileWriteException());
         }

         writeStudy(studyFile, new Long(EgoNet.study.getStudyId()));
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(EgoNet.frame, "Unable to write to study file. Study not saved.");
      }
   }

   /************************************************************************************************************************************************************
    * Save question information to a file with a new name
    */
   public void exportQuestions()
   {
      JFileChooser jNewQuestionsChooser = new JFileChooser();
      File newQuestionFile;

      jNewQuestionsChooser.setCurrentDirectory(new File(getStudyFile().getParent(), "/Questions/"));
      jNewQuestionsChooser.addChoosableFileFilter(writeQuestionFilter);
      jNewQuestionsChooser.setDialogTitle("Save Custom Questions As...");

      if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser.showSaveDialog(EgoNet.frame))
      {
         try
         {
            newQuestionFile =
               ((ExtensionFileFilter) writeQuestionFilter).getCorrectFileName(jNewQuestionsChooser.getSelectedFile());
            if (!newQuestionFile.createNewFile())
            {
               int confirm =
                  JOptionPane.showConfirmDialog(
                  		EgoNet.frame,
                     "<HTML><h2>Question File already exists at this location.</h2>"
                        + "<p>Shall I overwrite it?</p></html>",
                     "Overwrite Questions File",
                     JOptionPane.OK_CANCEL_OPTION);

               if (confirm != JOptionPane.OK_OPTION)
               {
                  throw new FileCreateException(true);
               }
            }

            writeAllQuestions(newQuestionFile);
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(
            	EgoNet.frame,
               "Unable to create question file.",
               "File Error",
               JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   /************************************************************************************************************************************************************
    * Save study info and questions as a package
    */
	public void saveAsStudyFile()
	{
		JFileChooser 	jNewQuestionsChooser = new JFileChooser("Save Study As...");
		File 				newStudyFile;
		FileWriter 		file 						= null;
		PrintWriter 	out 						= null;
		boolean 			complete					= false;

		jNewQuestionsChooser.setCurrentDirectory(getStudyFile().getParentFile());
		jNewQuestionsChooser.addChoosableFileFilter(studyFilter);
		
		while (!complete)
		{
			if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser.showSaveDialog(EgoNet.frame))
			{
				try
				{
					int confirm = JOptionPane.OK_OPTION;
					newStudyFile = ((ExtensionFileFilter) studyFilter).getCorrectFileName(jNewQuestionsChooser.getSelectedFile());

					if (!newStudyFile.createNewFile())
					{
						if (newStudyFile.canWrite())
						{
							confirm = JOptionPane.showConfirmDialog(EgoNet.frame,
									"<HTML><h3>A Study File already exists at this location.</h3>"
											+ "<p>Shall I overwrite it?</p></html>", "Overwrite Study Package File",
									JOptionPane.OK_CANCEL_OPTION);
						}
						else
						{
							confirm = JOptionPane.showConfirmDialog(EgoNet.frame,
									"<HTML><h2>An <b>Active</b> Study File already exists at this location.</h2>"
											+ "<p>If you overwrite it, any interviews created with it will be unreadable!</p>"
											+ "<p>Shall I overwrite it?</p></html>", "Overwrite Study Package File",
									JOptionPane.OK_CANCEL_OPTION);
						}
					}

					if (confirm == JOptionPane.OK_OPTION)
					{
						if (!newStudyFile.canWrite()) { throw (new FileWriteException()); }

						writeStudy(newStudyFile, new Long(System.currentTimeMillis()));
						setStudyFile(newStudyFile);
						studyFileInUse = false;
						complete 		= true;

						// Store location in prefs file
						Preferences prefs = Preferences.userNodeForPackage(EgoNet.class);
						prefs.put(FILE_PREF, newStudyFile.getParent());
					}
				}
				catch (FileWriteException e)
				{
					JOptionPane.showMessageDialog(EgoNet.frame, "Unable to write to study file. Study not saved.");
				}
				catch (java.io.IOException e)
				{
					JOptionPane.showMessageDialog(EgoNet.frame, "Unable to write to study file. Study not saved.");
				}
			}
			else
			{
				complete = true;
			}
		}
	}

   /*********************************************************************************
    * Reads in study information from an XML DOM
    * and arrays of question orders
    * 
    * @param document
    *            XML tree containing study data
    */
   private void readStudyData(Document document)
   {
      String data;

      Element root = document.getRoot();
      root = root.getElement("Study");

      if (root.getElement("name") != null)
      {
      	EgoNet.study.setStudyName(root.getTextString("name"));
      }

      if (root.getElement("numalters") != null)
      {
      	EgoNet.study.setNumAlters(root.getInt("numalters"));
      }

      Elements elements = root.getElements("questionorder");
      while (elements.hasMoreElements())
      {
         int qOrderId;
         List questionOrder;
         Elements ids;

         Element element = elements.next();
         qOrderId = Integer.parseInt(element.getAttribute("questiontype"));
         questionOrder = (EgoNet.study.getQuestionOrderArray())[qOrderId];

         ids = element.getElements("id");
         while (ids.hasMoreElements())
         {
            questionOrder.add(new Long(ids.next().getLong()));
         }
      }
   }

   /************************************************************************************************************************************************************
    * Writes Study information to a file for later retrieval Includes files paths and arrays of question orders
    * 
    * @param f File into which to write study @todo prune order lists, possibly need to load question files to do this
    * @throws IOException
    */
   private void writeStudy(File f, Long id) throws IOException
   {
      Document document = new Document();

      document.setEncoding("UTF-8");
      document.setVersion("1.0");
      Element studyElement = document.setRoot("Package");
      studyElement.setAttribute("Id", id.toString());
      studyElement.setAttribute("InUse", studyFileInUse ? "Y" : "N");
      studyElement.setAttribute("Creator", Shared.version);
      studyElement.setAttribute("Updated", DateUtils.getDateString(Calendar.getInstance().getTime(), "dd/MM/yyyy hh:mm a"));
      
      EgoNet.study.writeStudyData(studyElement);
      EgoNet.study.writeAllQuestionData(studyElement);

      document.write(f);
   }

   /************************************************************************************************************************************************************
    * Reads in questions from an XML like input file Includes files paths and arrays of question orders
    * 
    * @param document   XML tree containing question list
    */
   private void readQuestions(Document document)
   {
      File f;
      int choice;
      Element root, question;
      Elements questions;

      /**
	   * Load new questions from array
	   */
      try
      {
         /**
		  * Parse XML file
		  */
         root = document.getRoot();
         root = root.getElement("QuestionList");
         questions = root.getElements("Question");

         while (questions.hasMoreElements())
         {
            try
            {
               Question q = new Question(questions.next());

               if (q != null)
               {
                  /* Question complete, add it */
                  EgoNet.study.addQuestion(q);
               }
            }
            catch (Exception ex)
            {
               //					ex.printStackTrace();
               throw ex;
            }
         }
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(
         	EgoNet.frame,
            "Unable to read question file",
            "Question Reading Error",
            JOptionPane.ERROR_MESSAGE);
      }
   }

   /************************************************************************************************************************************************************
    * Writes all questions to a package file for later use
    * 
    * @param f
    *            File to write data to
    * @throws IOException
    */
   private void writeAllQuestions(File f) throws IOException
   {
      Document document = new Document();

      //document.addChild( new XMLDecl( "1.0", "UTF-8" ) );
      document.setEncoding("UTF-8");
      document.setVersion("1.0");
      Element study = document.setRoot("QuestionFile");
      study.setAttribute("Id", Long.toString(new Date().getTime()));

      EgoNet.study.writeAllQuestionData(study);

      document.write(f);
   }

   /************************************************************************************************************************************************************
    * Reads in study information from an XML like input file Includes files paths and arrays of question orders
    * 
    * @param file
    *            XML file from which to read study
    */
   public void readStudy(File file)
   {
      if (file != null)
      {
         try
         {
            Document document = new Document(file);
            Element root = document.getRoot();
            String inUse = root.getAttribute("InUse");

            if ((inUse != null) && inUse.equals("Y"))
            {
               studyFileInUse = true;

               JOptionPane.showMessageDialog(
                     EgoNet.frame,
                     "This study has already been used for at least one interview.\n" +
                     "You may change the text of questions while still using previously generated interview files. However, \n" +
                     "if you add, delete, reorder, or modify the answer types of any questions you will no longer be able to use \n" +
                     "it to view existing interview files.",
                     "File In Use",
                     JOptionPane.WARNING_MESSAGE);
            }

            EgoNet.study = new Study(document);
            EgoNet.study.setInUse(studyFileInUse);
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(
               EgoNet.frame,
               "Unable to read this study file",
               "Study Reading Error",
               JOptionPane.ERROR_MESSAGE);

            EgoNet.study = new Study();
         }
      }
   }
}


/********************
 *
 * $Log$
 * Revision 1.1  2007/09/03 13:51:19  schoaff
 * Initial Checkin
 *
 * Revision 1.16  2004/04/11 00:24:48  admin
 * Fixing headers
 *
 * Revision 1.15  2004/04/02 20:02:51  admin
 * Maintaining InUse State in study files
 *
 * Revision 1.14  2004/04/02 19:48:58  admin
 * Keep Study Id when possible
 * Store updated time in file
 *
 * Revision 1.13  2004/04/01 21:50:52  admin
 * Aborting interview if unable to write answers to file
 *
 * Revision 1.12  2004/03/23 14:58:48  admin
 * Update UI
 * Study creation now occurs in instantiators
 *
 * Revision 1.11  2004/03/22 00:00:34  admin
 * Extended text entry area
 * Started work on importing studies from server
 *
 * Revision 1.10  2004/03/21 20:29:37  admin
 * Warn before making incompatible changes to in use study file
 *
 * Revision 1.9  2004/03/21 14:00:38  admin
 * Cleaned up Question Panel Layout using FOAM
 *
 * Revision 1.8  2004/02/26 21:19:17  admin
 * adding jardescs
 *
 * Revision 1.7  2004/02/10 20:10:43  admin
 * Version 2.0 beta 3
 *
 * Revision 1.6  2004/01/23 13:36:07  admin
 * Updating Libraries
 * Allowing upload to web server
 *
 * Revision 1.5  2003/12/05 19:15:43  admin
 * Extracting Study
 *
 * Revision 1.4  2003/12/04 15:14:08  admin
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 *
 * Revision 1.3  2003/11/25 19:29:53  admin
 * Formatting
 *
 * Revision 1.2  2003/11/25 19:25:44  admin
 * Warn before closing window
 *
 * Revision 1.1.1.1  2003/06/08 15:09:40  admin
 * Egocentric Network Survey Authoring Module
 *
 * Revision 1.17  2002/09/01 20:06:16  admin
 * Structural question now selected in client. Visual feedback for alter pair
 * categorical questions.
 *
 * Revision 1.16  2002/08/30 09:30:37  admin
 * Allowing user to select study file name. Using it for study name.
 *
 * Revision 1.15  2002/08/11 22:26:05  admin
 * Final Statistics window, new file handling
 *
 * Revision 1.14  2002/08/08 17:07:25  admin
 * Preparing to change file system
 *
 * Revision 1.13  2002/07/25 14:54:24  admin
 * Question Links
 *
 * Revision 1.12  2002/07/24 14:17:09  admin
 * xml files, links
 *
 * Revision 1.11  2002/07/18 14:43:06  admin
 * New Alter Prompt Panel, packages
 *
 * Revision 1.10  2002/06/30 15:59:18  admin
 * Moving questions in lists, between lists
 * Better category input
 *
 * Revision 1.9  2002/06/26 15:43:42  admin
 * More selection dialog work
 * File loading fixes
 *
 * Revision 1.8  2002/06/26 00:10:48  admin
 * UI Work including base question coloring and category selections
 *
 * Revision 1.7  2002/06/25 15:41:01  admin
 * Lots of UI work
 *
 * Revision 1.6  2002/06/21 22:47:12  admin
 * question lists working again
 *
 * Revision 1.5  2002/06/21 21:52:50  admin
 * Many changes to event handling, file handling
 *
 * Revision 1.4  2002/06/19 01:57:04  admin
 * Much UI work done
 *
 * Revision 1.3  2002/06/16 17:52:01  admin
 * New Project, Open Project methods
 * DirList class w/method
 *
 * Revision 1.2  2002/06/15 14:19:50  admin
 * Initial Checkin of question and survey
 * General file system work
 *
 * Revision 1.1  2002/06/14 20:34:35  admin
 * Created
 *
 */

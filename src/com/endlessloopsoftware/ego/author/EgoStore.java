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
package com.endlessloopsoftware.ego.author;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.egonet.util.DateUtils;
import org.egonet.util.DirList;
import org.egonet.util.ExtensionFileFilter;
import org.egonet.util.FileCreateException;
import org.egonet.util.FileReadException;
import org.egonet.util.FileWriteException;

import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;

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
   private final EgoNet egoNet;

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
   public EgoStore(EgoNet egoNet)
   {
	   this.egoNet = egoNet;
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
         if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showSaveDialog(egoNet.getFrame()))
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
            			egoNet.getFrame(),
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
                     	egoNet.getFrame(),
                        "<HTML><h2>Study already exists at this location.</h2>" + "<p>Shall I overwrite it?</p></html>",
                        "Overwrite Study File",
                        JOptionPane.OK_CANCEL_OPTION);

                  if (confirm != JOptionPane.OK_OPTION)
                  {
                	  //do not overwrite
                     throw new FileCreateException(true);
                  }else{
                	  //delete the existing file and create a new one 
                	  newStudyFile.delete();
                	  newStudyFile.createNewFile();                	  
                  }
               }

               /* Clean out study variables */
               egoNet.setStudy(new Study());
               setStudyFile(newStudyFile);
               egoNet.getStudy().setStudyName(projectName);

               /* Write out default info */
               writeStudy(newStudyFile, new Long(System.currentTimeMillis()));
               studyFileInUse = false;

               // Store location in prefs file
               prefs.put(FILE_PREF, newStudyFile.getParent());
            }
            catch (java.io.IOException e)
            {
               JOptionPane.showMessageDialog(
               		egoNet.getFrame(),
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
            		egoNet.getFrame(),
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
            JOptionPane.showMessageDialog(egoNet.getFrame(), "Study not created.");
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

      if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(egoNet.getFrame()))
      {
         f = jNewStudyChooser.getSelectedFile();

         try
         {
            if (!f.canRead())
            {
               throw new FileReadException("Cannot read study file");
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

      jNewStudyChooser.setCurrentDirectory(DirList.getLibraryDirectory());
      jNewStudyChooser.addChoosableFileFilter(readQuestionFilter);
      jNewStudyChooser.setDialogTitle("Select Custom Questions File");

      if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(egoNet.getFrame()))
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
	   File studyFile = getStudyFile();

      try
      {
    	  if (!studyFile.exists())
    	  {
    		  throw new FileWriteException("File "+studyFile.getName()+" does not exist");
    	  }
         if (!studyFile.canWrite())
         {
            throw new FileWriteException("File "+studyFile.getName()+" is not writeable");
         }

         writeStudy(studyFile, new Long(egoNet.getStudy().getStudyId()));
      }
      catch (Throwable ex)
      {
         //JOptionPane.showMessageDialog(egoNet.getFrame(), "Unable to write to study file. Study not saved.");
         throw new RuntimeException(ex);
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

      if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser.showSaveDialog(egoNet.getFrame()))
      {
         try
         {
            newQuestionFile =
               ((ExtensionFileFilter) writeQuestionFilter).getCorrectFileName(jNewQuestionsChooser.getSelectedFile());
            if (!newQuestionFile.createNewFile())
            {
               int confirm =
                  JOptionPane.showConfirmDialog(
                  		egoNet.getFrame(),
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
            	egoNet.getFrame(),
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
		boolean 			complete					= false;

		if(getStudyFile() != null)
			jNewQuestionsChooser.setCurrentDirectory(getStudyFile().getParentFile());
		jNewQuestionsChooser.addChoosableFileFilter(studyFilter);
		
		while (!complete)
		{
			if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser.showSaveDialog(egoNet.getFrame()))
			{
				try
				{
					int confirm = JOptionPane.OK_OPTION;
					newStudyFile = ((ExtensionFileFilter) studyFilter).getCorrectFileName(jNewQuestionsChooser.getSelectedFile());

					if (!newStudyFile.createNewFile())
					{
						if (newStudyFile.canWrite())
						{
							confirm = JOptionPane.showConfirmDialog(egoNet.getFrame(),
									"<HTML><h3>A Study File already exists at this location.</h3>"
											+ "<p>Shall I overwrite it?</p></html>", "Overwrite Study Package File",
									JOptionPane.OK_CANCEL_OPTION);
						}
						else
						{
							confirm = JOptionPane.showConfirmDialog(egoNet.getFrame(),
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
					JOptionPane.showMessageDialog(egoNet.getFrame(), "Unable to write to study file. Study not saved.");
					throw new RuntimeException(e);
				}
				catch (java.io.IOException e)
				{
					JOptionPane.showMessageDialog(egoNet.getFrame(), "Unable to write to study file. Study not saved.");
					throw new RuntimeException(e);
				}
			}
			else
			{
				complete = true;
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
      studyElement.setAttribute("Creator", com.endlessloopsoftware.egonet.Shared.version);
      studyElement.setAttribute("Updated", DateUtils.getDateString(Calendar.getInstance().getTime(), "dd/MM/yyyy hh:mm a"));
      
      egoNet.getStudy().writeStudyData(studyElement);
      egoNet.getStudy().writeAllQuestionData(studyElement);

      document.write(f);
   }

   /************************************************************************************************************************************************************
    * Reads in questions from an XML like input file Includes files paths and arrays of question orders
    * 
    * @param document   XML tree containing question list
    */
   private void readQuestions(Document document)
   {
    //  File f;
      Element root;
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
               Question q = new Question(questions.next());

               if (q != null)
               {
                  /* Question complete, add it */
                  egoNet.getStudy().addQuestion(q);
               }
         }
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(
         	egoNet.getFrame(),
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

      egoNet.getStudy().writeAllQuestionData(study);

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
                     egoNet.getFrame(),
                     "This study has already been used for at least one interview.\n" +
                     "You may change the text of questions while still using previously generated interview files. However, \n" +
                     "if you add, delete, reorder, or modify the answer types of any questions you will no longer be able to use \n" +
                     "it to view existing interview files.",
                     "File In Use",
                     JOptionPane.WARNING_MESSAGE);
            }

            egoNet.setStudy(new Study(document));
            egoNet.getStudy().setInUse(studyFileInUse);
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(
               egoNet.getFrame(),
               "Unable to read this study file",
               "Study Reading Error",
               JOptionPane.ERROR_MESSAGE);

            egoNet.setStudy(new Study());
         }
      }
   }
}
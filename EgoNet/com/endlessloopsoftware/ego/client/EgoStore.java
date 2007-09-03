package com.endlessloopsoftware.ego.client;

/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.ego.exceptions.CorruptedInterviewException;
import com.endlessloopsoftware.ego.exceptions.FileMismatchException;
import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.elsutils.files.FileHelpers;
import com.endlessloopsoftware.elsutils.files.FileReadException;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.ParseException;

/****
 * Handles IO for the EgoNet program
 * Tracks data files and changes to those files
 */
public class EgoStore extends Observable
{
	private File                    packageFile     = null;
   private File                    interviewFile   = null;
   private boolean                 loaded          = false;
   private boolean                 interviewLoaded = false;
   private Document                packageDocument = null;

   private static final FileFilter packageFilter   = new ExtensionFileFilter("Study Definition Files", "ego");
   private static final FileFilter interviewFilter = new ExtensionFileFilter("Interview Files", "int");

   private static final String     FILE_PREF       = "FILE_PREF";

	/**
	 * Sets parent frame
	 * @param g EgoClient
	 */
	public EgoStore() {}

	/****
	 * Notifies observers that a field in the study has changed
	 */
	public void notifyObservers()
	{
		setChanged();
		super.notifyObservers(this);
	}

	/****
	 * Returns study file
	 * @return	studyFile file containing study overview information
	 */
	public boolean getStudyLoaded()
	{
		return (loaded);
	}

	/****
	 * Returns study file
	 * @return	studyFile file containing study overview information
	 */
	public File getPackageFile()
	{
		return (packageFile);
	}

	/****
	 * Returns study file
	 * @return	interview file containing answers
	 */
	public File getInterviewFile()
	{
		return (interviewFile);
	}

	/****
	 * Sets interview file variable and notifies observers of change to study
	 * @param	f 	question file
	 */
	public void setInterviewFile(File f)
	{
		interviewFile = f;
		notifyObservers();
	}

	/****
	 * Sets baseQuestionFile variable and notifies observers of change to study
	 * @param	f 	question file
	 */
	public void setPackageFile(File f)
	{
		packageFile = f;
		notifyObservers();
	}

	/****
	 * Select a directory in which to store project related files
	 * Create subdirectories if needed.
	 */
	public void selectStudy()
	{
		Preferences prefs = Preferences.userNodeForPackage(EgoClient.class);
		JFileChooser jNewStudyChooser = new JFileChooser();
		File f;

		jNewStudyChooser.addChoosableFileFilter(packageFilter);
		jNewStudyChooser.setDialogTitle("Select Study Definition File");

		if (getPackageFile() != null)
		{
			jNewStudyChooser.setCurrentDirectory(getPackageFile().getParentFile());
		}
		else
		{
			File directory = new File(prefs.get(FILE_PREF, "."));
			jNewStudyChooser.setCurrentDirectory(directory);
		}

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(EgoClient.getFrame()))
		{
			f = jNewStudyChooser.getSelectedFile();

			if (f != null)
			{
				try
				{
					if (!f.canRead())
					{
						throw new FileReadException();
					}
					else
					{
						setPackageFile(f);

						// Store location in prefs file
						prefs.put(FILE_PREF, f.getParent());
					}
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(
						null,
						"Unable to read study file.",
						"File Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/****
	 * Select a directory in which to store project related files
	 * Create subdirectories if needed.
	 */
	public void selectInterview()
	{
		JFileChooser jNewStudyChooser = new JFileChooser();
		File f;

		jNewStudyChooser.addChoosableFileFilter(interviewFilter);
		jNewStudyChooser.setCurrentDirectory(new File(getPackageFile().getParent(), "/Interviews/"));
		jNewStudyChooser.setDialogTitle("Select Interview File");

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser.showOpenDialog(EgoClient.frame))
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
					setInterviewFile(f);
				}
			}
			catch (Exception e)
			{
				/** @todo Handle file failure */
				e.printStackTrace();

				JOptionPane.showMessageDialog(
					null,
					"Unable to read interview file.",
					"File Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/****
	 * Reads in study information from an XML like input file
	 * Includes files paths and arrays of question orders
	 */
	public void readPackage()
	{
		File file = getPackageFile();
      
		if (file != null)
		{
			try
			{
				packageDocument = new Document(file);
				EgoClient.study = new Study(packageDocument);
				loaded = true;
			}
			catch (ParseException ex)
			{
				/** @todo handle package parsing error */
				loaded = false;
			}
		}
	}

	/****
	 * Writes an attribute to the package file to indicate that
	 * it is being used for interviews. EgoNet will then warn before
	 * allowing user to save
	 */
	public void setPackageInUse()
	{
		try
		{
			if (getPackageFile().canWrite())
			{
				Element root = packageDocument.getRoot();
				root.setAttribute("InUse", "Y");
				packageDocument.write(getPackageFile());

				getPackageFile().setReadOnly();
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Unable to update study file.", "File Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (SecurityException ignored)
		{
		}
	}

	/****
	 * Reads in study information from an XML like input file
	 * Includes files paths and arrays of question orders
	 * @return interview structure derived from file
	 */
	public Interview readInterview()
	{
		Interview interview = null;

		try
		{
			interview = readInterview(getInterviewFile());
		}
		catch (FileReadException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Unable to Read Interview.",
				"Read Interview Error",
				JOptionPane.ERROR_MESSAGE);
		}
		catch (FileMismatchException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Interview file not created from this study file.",
				"Read Interview Error",
				JOptionPane.ERROR_MESSAGE);
		}

		return (interview);
	}

	/****
	 * Reads in study information from an XML like input file
	 * Includes files paths and arrays of question orders
	 * @param f file from which to read interview
	 * @return Interview derived from file
	 * @throws FileReadException
	 * @throws FileMismatchException
	 */
	private Interview readInterview(File f) 
      throws FileReadException, FileMismatchException
	{
		Interview interview = null;
		long studyId;

		try
      {
//         SAXReader reader = new SAXReader();
//         org.dom4j.Document document = reader.read(f);
         Document document = new Document(f);

         /* make sure id matches study */
         studyId = Long.parseLong(document.getRoot().getAttribute("StudyId"));
//         studyId = Long.parseLong(document.getRootElement().attributeValue("StudyId"));
         if (studyId != EgoClient.study.getStudyId())
         {
            interviewLoaded = false;
            interview = null;

            throw (new FileMismatchException());
         }

         interview = Interview.readInterview(document.getRoot());
         //interview = Interview.readInterview(document.getRootElement());
         interviewLoaded = true;
      }
      catch (CorruptedInterviewException ex)
      {
         interviewLoaded = false;
         interview = null;

         throw (new FileReadException());
      }
      catch (ParseException ex)
      {
         interviewLoaded = false;
         interview = null;

         throw (new FileReadException());
      }

		return (interview);
	}

	/****
	 * Writes all questions to a package file for later use
	 * @return successful
	 * @throws FileCreateException
	 * @throws FileReadException
	 */
	public boolean saveInterview() throws FileCreateException, FileReadException
	{
		boolean exists = false;
		boolean complete = false;
		boolean confirmed = false;
		boolean resume = false;
		boolean success = false;

		try
		{
			String[] name = EgoClient.interview.getName();
			File path = new File(getPackageFile().getParent(), "/Interviews/");
			File f = new File(path, name[0].toLowerCase() + "_" + name[1].toLowerCase() + ".int");

			if (!path.exists())
			{
				path.mkdir();
			}

			if (f.exists())
			{
				exists = true;

				try
				{
					Document document = new Document(f);
					Element e = document.getRoot();
					complete = e.getBoolean("Complete");
				}
				catch (Exception ex)
				{
					exists = false;
					complete = false;
				}

				if (exists && complete)
				{
					int selected =
						JOptionPane.showConfirmDialog(
							EgoClient.frame,
							"There is already a complete interview for "
								+ name[0]
								+ " "
								+ name[1]
								+ "\nDo you wish to replace it with a new interview?",
							"Completed Interview Exists",
							JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION)
					{
						exists = false;
					}
					confirmed = true;
				}
				else if (exists && !complete)
				{
					int selected =
						JOptionPane.showConfirmDialog(
							EgoClient.frame,
							"There is already an incomplete interview for "
								+ name[0]
								+ " "
								+ name[1]
								+ "\nWould you like to continue this interview?",
							"Incomplete Interview Exists",
							JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION)
					{
						resume = true;
						confirmed = true;
					}
				}

				if (exists && !confirmed)
				{
					int selected =
						JOptionPane.showConfirmDialog(
							EgoClient.frame,
							"Should I erase the old interview and start a new one?",
							"Delete Interview",
							JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION)
					{
						exists = false;
					}
					confirmed = true;
				}
			}

			if (!exists)
			{
				writeInterviewFile(f);
				success = true;
				setInterviewFile(f);
			}
			else if (resume)
			{
				EgoClient.interview = readInterview(f);

				if (EgoClient.interview != null)
				{
					success = true;
					setInterviewFile(f);
				}
			}
		}
		catch (SecurityException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Unable to create interview directory.",
				"New Interview Error",
				JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}
		catch (FileReadException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Unable to Read Interview.",
				"Read Interview Error",
				JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}
		catch (FileMismatchException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Interview file not created from this study file.",
				"Read Interview Error",
				JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}

		return (success);
	}

	/****
	 * Writes all questions to a package file for later use
	 * @throws FileCreateException
	 */
	public void writeInterviewFile() throws FileCreateException
	{
		if (getInterviewFile() != null)
		{
			writeInterviewFile(getInterviewFile());
		}
	}

	/****
	 * Writes all questions to a package file for later use
	 * @param f File to write data to
	 * @throws FileCreateException
	 */
	private void writeInterviewFile(File f) throws FileCreateException
	{
		Document document = new Document();

		if (f != null)
		{
			document.setEncoding("UTF-8");
			document.setVersion("1.0");
			Element interviewDocument = document.setRoot("Interview");

			interviewDocument.setAttribute("StudyId", Long.toString(EgoClient.study.getStudyId()));
			interviewDocument.setAttribute("StudyName", EgoClient.study.getStudyName());
			interviewDocument.setAttribute("NumAlters", Integer.toString(EgoClient.study.getNumAlters()));
			interviewDocument.setAttribute("Creator", Shared.version);

			EgoClient.interview.writeInterview(interviewDocument);

			try
			{
				document.write(f);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(
					EgoClient.frame,
					"Unable to write interview. \nYour answers are not being saved so the interview will now abort.\n Please report this error.",
					"Interview Error",
					JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
	}

	/****
	 * Writes all questions to a package file for later use
	 * @param stats Statistics object
	 * @throws FileCreateException
	 */
	public void writeStatisticsFiles(Statistics stats, String[] egoName) throws FileCreateException
	{
		File file = getInterviewFile();
      String name = file.getName();
      String statdir;
      String parentDir;
		
      if (getPackageFile() != null)
      {
         try
         {
            parentDir = getPackageFile().getParent();
            statdir = (new File(parentDir, "/Statistics/")).getCanonicalPath();
         }
         catch (IOException e)
         {
            e.printStackTrace();
            throw new FileCreateException();
         }
         
         file = int2ist(statdir, name);
         writeStatisticsFile(file, stats);
         
         file = int2matrix(statdir, name);
         writeAdjacencyFile(file, stats, egoName, false);
         
         file = int2weightedmatrix(statdir, name);
         writeAdjacencyFile(file, stats, egoName, true);
         
         file = int2alterSummary(statdir, name);
         writeAlterSummaryFile(file, stats);
      }

	}

   /***
    * Writes an alter summary file to the statistics directory
    * @param f
    * @param stats
    */
   void writeAlterSummaryFile(File f, Statistics stats)
   {
      PrintWriter alterWriter;
      try
      {
         alterWriter = new PrintWriter(new BufferedWriter(new FileWriter(f), (32 * 1024)));
      }
      catch (IOException e1)
      {
         alterWriter = null;
      }
      
      if (alterWriter != null)
      {
         try
         {
            stats.writeAlterArray(alterWriter);
         }
         finally
         {
            alterWriter.close();
         }
      }
   }

   /****
	 * Writes all questions to a package file for later use
	 * @param f File to write data to
	 * @param stats Statistics Object
	 * @throws FileCreateException
	 */
	private void writeAdjacencyFile(File f, Statistics stats, String[] name, boolean weighted) 
	{
		PrintWriter adjacencyWriter;
		try
		{
			adjacencyWriter = new PrintWriter(new BufferedWriter(new FileWriter(f), (32 * 1024)));
		}
		catch (IOException e1)
		{
			adjacencyWriter = null;
		}
		
		if (adjacencyWriter != null)
		{
			try
			{
				stats.writeAdjacencyArray(name[0] + " " + name[1], adjacencyWriter, weighted);
			}
			finally
			{
				adjacencyWriter.close();
			}
		}
	}

	/****
	 * Writes all questions to a package file for later use
	 * @param f File to write data to
	 * @param stats Statistics Object
	 * @throws FileCreateException
	 */
	private void writeStatisticsFile(File f, Statistics stats) throws FileCreateException
	{
		Document document = new Document();

		document.setEncoding("UTF-8");
		document.setVersion("1.0");
		Element study = document.setRoot("Statistics");

		study.setAttribute("StudyId", Long.toString(EgoClient.study.getStudyId()));
		study.setAttribute("Creator", Shared.version);

		stats.writeStructuralStatistics(study);
		EgoClient.interview.writeEgoAnswers(study);
		stats.writeCompositionalStatistics(study);

		try
		{
			document.write(f);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Unable to write statistics.",
				"Interview Error",
				JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}
	}

	/****
	 * Creates a new csv file to store statistics in
	 * @param parent Frame for centering error messages
	 * @param filetype file type for newFile call
	 * @param defaultname Default name of new file for newFile call
	 * @returns PrintWriter for csv file
	 * @throws IOException
	 */
	public PrintWriter newStatisticsPrintWriter(String filetype, String suffix, String defaultname)
	{
      Preferences  prefs = Preferences.userNodeForPackage(EgoClient.class);
		File         startdir;
		PrintWriter  out = null;
		File file;

      if (getPackageFile() != null)
      {
         startdir = new File(getPackageFile().getParent(), "/Statistics/");
      }
      else
      {
         startdir = new File(prefs.get("STAT_DIR", "Statistics/"));
      }
      
		try
		{
			file = FileHelpers.newFile(filetype, defaultname, "Statistics", "." + suffix, startdir, EgoClient.frame, false);

			if (file == null)
			{
				throw new FileCreateException();
			}

			out = new PrintWriter(new BufferedWriter(new FileWriter(file), (32 * 1024)));
         prefs.put("STAT_DIR", file.getParent());
		}
		catch (Exception fce)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"Unable to create " + filetype + "file.",
				"New Statics File Error",
				JOptionPane.ERROR_MESSAGE);
			out = null;
		}

		return out;
	}

	/****
	 * Creates a new .ist file from an existing complete interview file
	 * @param interviewFile File from which to read interview
	 * @throws FileCreateException
	 */
	public void generateStatisticsFile(File interviewFile) throws FileCreateException
	{
		EgoClient.interview = null;

		setInterviewFile(interviewFile);

		if (EgoClient.storage.getInterviewFile() != null)
		{
			EgoClient.interview = EgoClient.storage.readInterview();
		}

		if ((EgoClient.interview != null) && EgoClient.interview.isComplete())
		{
			EgoClient.interview.completeInterview();
		}
		else
		{
			throw new FileCreateException();
		}
	}

	public static File int2ist(String istPath, String intFile)
	{
		return(new File(istPath, intFile.substring(0, intFile.lastIndexOf(".")) + ".ist"));
	}

	public static File int2matrix(String istPath, String intFile)
	{
		return(new File(istPath, intFile.substring(0, intFile.lastIndexOf(".")) + "_matrix.csv"));
	}
	
	public static File int2weightedmatrix(String istPath, String intFile)
	{
		return(new File(istPath, intFile.substring(0, intFile.lastIndexOf(".")) + "_weighted_matrix.csv"));
	}
   
   public static File int2alterSummary(String istPath, String intFile)
   {
      return(new File(istPath, intFile.substring(0, intFile.lastIndexOf(".")) + "_alter_summary.csv"));
   }
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:17  schoaff
 * Initial Checkin
 *
 * Revision 1.10  2004/04/11 00:24:48  admin
 * Fixing headers
 *
 * Revision 1.9  2004/04/08 15:06:06  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 * Revision 1.8  2004/04/06 23:09:19  admin
 * storing statistics file path in prefs for server interviews
 *
 * Revision 1.7  2004/04/02 19:48:58  admin
 * Keep Study Id when possible
 * Store updated time in file
 *
 * Revision 1.6  2004/04/01 21:50:52  admin
 * Aborting interview if unable to write answers to file
 *
 * Revision 1.5  2004/03/23 14:58:48  admin
 * Update UI
 * Study creation now occurs in instantiators
 *
 * Revision 1.4  2004/03/19 20:28:45  admin
 * Converted statistics frome to a panel. Incorporated in a tabbed panel
 * as part of main frame.
 *
 * Revision 1.3  2004/02/10 20:10:43  admin
 * Version 2.0 beta 3
 *
 * Revision 1.2  2003/12/08 15:57:50  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 * Revision 1.1  2003/12/04 15:14:09  admin
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 *
 * Revision 1.3  2003/12/03 15:12:08  admin
 * creating subdirectories for graph and statistics
 *
 * Revision 1.2  2003/11/25 19:23:35  admin
 * Adding weighted matrix output
 * First checking of graph panel
 *
 * Revision 1.1.1.1  2003/06/08 14:50:21  admin
 * Egocentric Network client program
 *
 * Revision 1.7  2002/09/01 20:05:11  admin
 * UI Changes to allow selection of arbitrary ALTER_PAIR question for stats
 * all sorts of bug fixes
 * optimized statistics
 * allow no answer for numerical questions
 *
 * Revision 1.6  2002/08/12 19:19:15  admin
 * Summary Screen
 *
 * Revision 1.5  2002/08/11 22:29:37  admin
 * statistics frame, new file structure
 *
 * Revision 1.4  2002/07/31 20:19:36  admin
 * Statistics
 *
 * Revision 1.3  2002/07/25 14:55:50  admin
 * question Links
 *
 * Revision 1.2  2002/07/22 02:53:21  admin
 * Interview implemented and working
 *
 * Revision 1.1.1.1  2002/07/19 15:51:30  admin
 * Client Module for EgoNet project
 *
 */
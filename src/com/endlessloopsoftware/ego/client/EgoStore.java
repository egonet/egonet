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
package com.endlessloopsoftware.ego.client;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.ProgressMonitor;
import org.jdesktop.swingworker.*;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.exceptions.FileMismatchException;

import com.endlessloopsoftware.ego.Answer;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.elsutils.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.FileCreateException;
import com.endlessloopsoftware.elsutils.FileHelpers;
import com.endlessloopsoftware.elsutils.FileReadException;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.ParseException;

/*******************************************************************************
 * Handles IO for the EgoNet program Tracks data files and changes to those
 * files
 */
public class EgoStore extends Observable {
	private File packageFile = null;

	private File interviewFile = null;

	private boolean loaded = false;

	private Document packageDocument = null;

	private static final FileFilter packageFilter = new ExtensionFileFilter(
			"Study Definition Files", "ego");

	private static final String FILE_PREF = "FILE_PREF";

	private final EgoClient egoClient;
	
	/**
	 * Sets parent frame
	 * 
	 * @param g
	 *            EgoClient
	 */
	public EgoStore(EgoClient egoClient) {
		this.egoClient = egoClient;
	}

	/***************************************************************************
	 * Notifies observers that a field in the study has changed
	 */
	public void notifyObservers() {
		setChanged();
		super.notifyObservers(this);
	}

	/***************************************************************************
	 * Returns study file
	 * 
	 * @return studyFile file containing study overview information
	 */
	public boolean getStudyLoaded() {
		return (loaded);
	}

	/***************************************************************************
	 * Returns study file
	 * 
	 * @return studyFile file containing study overview information
	 */
	public File getPackageFile() {
		return (packageFile);
	}

	/***************************************************************************
	 * Returns interview file
	 * 
	 * @return interview file containing answers
	 */
	public File getInterviewFile() {
		return (interviewFile);
	}

	/***************************************************************************
	 * Sets interview file variable and notifies observers of change to study
	 * 
	 * @param f
	 *            question file
	 */
	public void setInterviewFile(File f) {
		interviewFile = f;
		notifyObservers();
	}

	/***************************************************************************
	 * Sets baseQuestionFile variable and notifies observers of change to study
	 * 
	 * @param f
	 *            question file
	 */
	public void setPackageFile(File f) {
		packageFile = f;
		notifyObservers();
	}

	/***************************************************************************
	 * Select a directory in which to store project related files Create
	 * subdirectories if needed.
	 */
	public void selectStudy() {

		JFileChooser jNewStudyChooser = new JFileChooser();
		File f, directory;

		Preferences prefs = null;
		try {
			prefs = Preferences.userNodeForPackage(EgoClient.class);
		} catch (Throwable t) {
			// eat this exception
		}

		jNewStudyChooser.addChoosableFileFilter(packageFilter);
		jNewStudyChooser.setDialogTitle("Select Study Definition File");

		if (getPackageFile() != null) {
			jNewStudyChooser.setCurrentDirectory(getPackageFile().getParentFile());
		} else {
			String userHome = ".";
			try { 
				userHome = System.getProperty("user.home",".");
			} catch (Throwable t) {	}
			//if (prefs != null) directory = new File(prefs.get(FILE_PREF, "."));
			directory = new File(userHome);
			jNewStudyChooser.setCurrentDirectory(directory);
		}

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser
				.showOpenDialog(egoClient.getFrame())) {
			f = jNewStudyChooser.getSelectedFile();

			if (f != null) {
				try {
					if (!f.canRead()) {
						throw new FileReadException();
					} else {
						setPackageFile(f);

						// Store location in prefs file
						if (prefs != null)
							prefs.put(FILE_PREF, f.getParent());
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Unable to read study file.", "File Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	/**
	 * File filter to filter the interview files based on selected study The
	 * file chooser displays only the interview files compatible with the
	 * currently chosen study
	 * 
	 * @author sonam
	 * 
	 */
	public class VersionFileFilter extends ExtensionFileFilter {
		Map<File, Boolean> cacheResults = new HashMap<File, Boolean>();

		public VersionFileFilter(String description, String extension) {
			super(description, extension);
		}

		public void cacheList(File currentDirectory,
				final ProgressMonitor progress) {
			int ct = 0;

			for (File ptr : currentDirectory.listFiles()) {
				final Integer tct = ++ct;
				cacheResults.put(ptr, ptr.canRead() && ptr.isFile()
						&& cacheAccept(ptr));
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progress.setProgress(tct);
					}
				});

			}
		}

		public boolean accept(File ptr) {
			if (cacheResults.containsKey(ptr)) {
				// cache hit
				return cacheResults.get(ptr);
			} else {
				// cache miss
				boolean accept = ptr.canRead() && cacheAccept(ptr);
				cacheResults.put(ptr, accept);
				return accept;
			}
		}

		public boolean cacheAccept(java.io.File f) {

			if (f.isDirectory())
				return true;

			boolean cantread = (!f.isFile()) || (!f.canRead());
			if (cantread)
				return !cantread;

			boolean accept = true;
			try {
				// compare study id of interview file with id of currently
				// selected study
				Document document = new Document(f);
				long studyId = Long.parseLong(document.getRoot().getAttribute(
						"StudyId"));
				if (studyId != egoClient.getStudy().getStudyId()) {
					throw (new FileMismatchException());
				}
				// readInterview(f);
			} catch (FileMismatchException exception) {
				accept = false;
				// exception.printStackTrace();
			} catch (ParseException ex) {
				accept = false;
			} catch (Throwable t) {
				accept = false;
			}
			return accept;
		}

		public String getDescription() {
			String str = "Interview files";
			return str;
		}
	}

	/***************************************************************************
	 * Select a directory in which to store project related files Create
	 * subdirectories if needed.
	 */

	public void selectInterview() {

		File packageFile = getPackageFile();
		File parentFile = packageFile.getParentFile();
		File interviewFile = new File(parentFile, "/Interviews/");
		
		File guessLocation = new File(".");
		if(parentFile.exists() && parentFile.isDirectory() && parentFile.canRead())
			guessLocation = parentFile;
		
		if(interviewFile.exists() && interviewFile.isDirectory() && interviewFile.canRead())
			guessLocation = interviewFile;
		
		final File currentDirectory = guessLocation;
		

		final int numFiles = currentDirectory.list().length;

		final VersionFileFilter filter = new VersionFileFilter(
				"Interview Files", "int");
		final JFileChooser jNewInterviewChooser = new JFileChooser();
		final ProgressMonitor progressMonitor = new ProgressMonitor(
				egoClient.getFrame(),
				"Searching for interviews and caching the list of files that match the current study",
				"", 0, numFiles);
		jNewInterviewChooser.setCurrentDirectory(currentDirectory);
		jNewInterviewChooser.setDialogTitle("Select Interview File");

		progressMonitor.setMaximum(numFiles);

		SwingWorker<Integer, Integer> filterWorker = new SwingWorker<Integer, Integer>() {
			public Integer doInBackground() {
				filter.cacheList(currentDirectory, progressMonitor);
				return 0;
			}

			public void done() {
				progressMonitor.close();

				jNewInterviewChooser.addChoosableFileFilter(filter);
				int result = jNewInterviewChooser
						.showOpenDialog(egoClient.getFrame());

				if (JFileChooser.APPROVE_OPTION == result) {
					final File f = jNewInterviewChooser.getSelectedFile();

					try {
						// if (!(f != null && f.canRead())) {
						if (!f.canRead()) {
							throw new IOException("Couldn't read file");
						} else {
							setInterviewFile(f);
							boolean complete = true;

							try {
								Document document = new Document(f);
								Element e = document.getRoot();
								complete = e.getBoolean("Complete");
							} catch (Exception ex) {
								complete = false;
							}

							egoClient.setInterview(egoClient.getStorage().readInterview());
							if (complete == false) {
								JOptionPane
										.showMessageDialog(
												egoClient.getFrame(),
												"This interview is not completed and no "
														+ "graph or statictical data will be displayed!",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
							}
							if (egoClient.getInterview() != null)
							    egoClient.getFrame().gotoViewInterviewPanel();
						}
					} catch (Throwable e) {
						/** @todo Handle file failure */
						e.printStackTrace();

						JOptionPane.showMessageDialog(null,
								"Unable to read interview file.", "File Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}

			}

		};
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);

		filterWorker.execute();
	}
	
	public void combineInterviews()
	{
		//Find the interview files associated with this study
		File packageFile = getPackageFile();
		File parentFile = packageFile.getParentFile();
		File interviewFile = new File(parentFile, "/Interviews/");
		
		File guessLocation = new File(".");
		if(parentFile.exists() && parentFile.isDirectory() && parentFile.canRead())
			guessLocation = parentFile;
		
		if(interviewFile.exists() && interviewFile.isDirectory() && interviewFile.canRead())
			guessLocation = interviewFile;
		
		final File currentDirectory = guessLocation;
		
		String[] fileList = currentDirectory.list();	
		VersionFileFilter filter = new VersionFileFilter("Interview Files", "int");
		ArrayList<String> alterList = new ArrayList<String>();
		
		for (String s: fileList){
			try{	
				File f = new File(currentDirectory.toString() + "/" + s);			
				if(!filter.cacheAccept(f) || !f.canRead()){
					throw new IOException("Couldn't read file or file not associated" +
							" with selected study.");
				}
				else{
					setInterviewFile(f);
					Interview interview = readInterview();
					
					//String [] thisInterviewAlters = interview.getAlterList();
					
					Iterator questions = egoClient.getStudy().getQuestionOrder(Question.ALTER_PAIR_QUESTION).iterator();
					while (questions.hasNext()) {
						Question q = egoClient.getStudy().getQuestion((Long) questions.next());
						Answer a = q.answer;
						int[][] adj = interview.generateAdjacencyMatrix(q, false);
						
						// loop through adj
						// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix
						
						for(int i = 0; i < adj.length; i++)
						{
							for(int j = 0; j < adj[i].length; j++)
							{
								if(a.adjacent)
								{
									// alter1 = a.getAlters()[0]
									// alter2 = a.getAlters()[1]
									// mark those as adjacent in the new big matrix
									System.out.println(a.getAlters()[0] + " and " + a.getAlters()[1] + " are adjacent");
								}
							}
						}
					}
					
					//add alters to alterList
					alterList.addAll(Arrays.asList(interview.getAlterList()));
				}
			}
			catch(Throwable e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Unable to read interview file.", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		System.out.println(alterList);
		//
	}

	/***************************************************************************
	 * Reads in study information from an XML like input file Includes files
	 * paths and arrays of question orders
	 */
	public void readPackage() {
		File file = getPackageFile();

		if (file != null) {
			try {
				packageDocument = new Document(file);
				egoClient.setStudy(new Study(packageDocument));
				loaded = true;
			} catch (ParseException ex) {
				/** @todo handle package parsing error */
				loaded = false;
			}
		}
	}

	/***************************************************************************
	 * Writes an attribute to the package file to indicate that it is being used
	 * for interviews. EgoNet will then warn before allowing user to save
	 */
	public void setPackageInUse() {
		try {
			if (getPackageFile().canWrite()) {
				Element root = packageDocument.getRoot();
				root.setAttribute("InUse", "Y");
				packageDocument.write(getPackageFile());

				getPackageFile().setReadOnly();
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "Unable to update study file.",
					"File Error", JOptionPane.ERROR_MESSAGE);
		} catch (SecurityException ignored) {
		}
	}

	/***************************************************************************
	 * Reads in study information from an XML like input file Includes files
	 * paths and arrays of question orders
	 * 
	 * @return interview structure derived from file
	 */
	public Interview readInterview() {
		Interview interview = null;

		try {
			interview = readInterview(getInterviewFile());
		} catch (FileReadException e) {
			String msg = (e != null && !e.getMessage().equals("") ? " " + e.getMessage() : "");
			
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Unable to Read Interview."+msg, "Read Interview Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (FileMismatchException e) {
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Interview file not created from this study file.",
					"Read Interview Error", JOptionPane.ERROR_MESSAGE);
		}

		return (interview);
	}

	/***************************************************************************
	 * Reads in study information from an XML like input file Includes files
	 * paths and arrays of question orders
	 * 
	 * @param f
	 *            file from which to read interview
	 * @return Interview derived from file
	 * @throws FileReadException
	 * @throws FileMismatchException
	 */
	private Interview readInterview(File f) throws FileReadException,
			FileMismatchException {
		Interview interview = null;
		long studyId;

		try {

			Document document = new Document(f);

			/* make sure id matches study */
			studyId = Long
					.parseLong(document.getRoot().getAttribute("StudyId"));
			if (studyId != egoClient.getStudy().getStudyId()) {
				interview = null;
				throw (new FileMismatchException());
			}
			interview = Interview.readInterview(egoClient, document.getRoot());
		} catch (CorruptedInterviewException ex) {
			interview = null;

			throw (new FileReadException(ex));
		} catch (ParseException ex) {
			interview = null;

			throw (new FileReadException());
		}

		return (interview);
	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @return successful
	 * @throws FileCreateException
	 * @throws FileReadException
	 */
	public boolean saveInterview() throws FileCreateException,
			FileReadException {
		boolean exists = false;
		boolean complete = false;
		boolean confirmed = false;
		boolean resume = false;
		boolean success = false;

		try {
			String[] name = egoClient.getInterview().getName();
			File path = new File(getPackageFile().getParent(), "/Interviews/");
			File f = new File(path, name[0].toLowerCase() + "_"
					+ name[1].toLowerCase() + ".int");

			if (!path.exists()) {
				path.mkdir();
			}

			if (f.exists()) {
				exists = true;

				try {
					Document document = new Document(f);
					Element e = document.getRoot();
					complete = e.getBoolean("Complete");
				} catch (Exception ex) {
					exists = false;
					complete = false;
				}

				if (exists && complete) {
					int selected = JOptionPane
							.showConfirmDialog(
									egoClient.getFrame(),
									"There is already a complete interview for "
											+ name[0]
											+ " "
											+ name[1]
											+ "\nDo you wish to replace it with a new interview?",
									"Completed Interview Exists",
									JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION) {
						exists = false;
					}
					confirmed = true;
				} else if (exists && !complete) {
					int selected = JOptionPane
							.showConfirmDialog(
									egoClient.getFrame(),
									"There is already an incomplete interview for "
											+ name[0]
											+ " "
											+ name[1]
											+ "\nWould you like to continue this interview?",
									"Incomplete Interview Exists",
									JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION) {
						resume = true;
						confirmed = true;
					}
				}

				if (exists && !confirmed) {
					int selected = JOptionPane
							.showConfirmDialog(
									egoClient.getFrame(),
									"Should I erase the old interview and start a new one?",
									"Delete Interview",
									JOptionPane.YES_NO_OPTION);

					if (selected == JOptionPane.YES_OPTION) {
						exists = false;
					}
					confirmed = true;
				}
			}

			if (!exists) {
				writeInterviewFile(f);
				success = true;
				setInterviewFile(f);
			} else if (resume) {
				egoClient.setInterview(readInterview(f));

				if (egoClient.getInterview() != null) {
					success = true;
					setInterviewFile(f);
				}
			}
		} catch (SecurityException e) {
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Unable to create interview directory.",
					"New Interview Error", JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		} catch (FileReadException e) {
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Unable to Read Interview.", "Read Interview Error",
					JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		} catch (FileMismatchException e) {
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Interview file not created from this study file.",
					"Read Interview Error", JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}

		return (success);
	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @throws FileCreateException
	 */
	public void writeInterviewFile() throws FileCreateException {
		if (getInterviewFile() != null) {
			writeInterviewFile(getInterviewFile());
		}
	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param f
	 *            File to write data to
	 * @throws FileCreateException
	 */
	private void writeInterviewFile(File f) throws FileCreateException {
		Document document = new Document();

		if (f != null) {
			document.setEncoding("UTF-8");
			document.setVersion("1.0");
			Element interviewDocument = document.setRoot("Interview");

			interviewDocument.setAttribute("StudyId", Long
					.toString(egoClient.getStudy().getStudyId()));
			interviewDocument.setAttribute("StudyName", egoClient.getStudy()
					.getStudyName());
			interviewDocument.setAttribute("NumAlters", Integer
					.toString(egoClient.getStudy().getNumAlters()));
			interviewDocument.setAttribute("Creator", Shared.version);

			egoClient.getInterview().writeInterview(interviewDocument);

			try {
				document.write(f);
			} catch (Exception e) {
				JOptionPane
						.showMessageDialog(
								egoClient.getFrame(),
								"Unable to write interview. \nYour answers are not being saved so the interview will now abort.\n Please report this error.",
								"Interview Error", JOptionPane.ERROR_MESSAGE);
				throw new RuntimeException(e);
			}
		}
	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param stats
	 *            Statistics object
	 * @throws FileCreateException
	 */
	public void writeStatisticsFiles(Statistics stats, String[] egoName)
			throws FileCreateException {
		File file = getInterviewFile();
		String name = file.getName();
		String statdir;
		String parentDir;

		if (getPackageFile() != null) {
			try {
				parentDir = getPackageFile().getParent();
				statdir = (new File(parentDir, "/Statistics/"))
						.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				throw new FileCreateException();
			}

			file = int2ist(statdir, name);
			writeStatisticsFile(file, stats);

			file = int2matrix(statdir, name);
			writeAdjacencyFile(file, stats, egoName, false);

			file = int2weightedmatrix(statdir, name);
			writeAdjacencyFile(file, stats, egoName, true);
		}

	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param f
	 *            File to write data to
	 * @param stats
	 *            Statistics Object
	 * @throws FileCreateException
	 */
	private void writeAdjacencyFile(File f, Statistics stats, String[] name,
			boolean weighted) {
		PrintWriter adjacencyWriter;
		try {
			adjacencyWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(f), (32 * 1024)));
		} catch (IOException e1) {
			adjacencyWriter = null;
		}

		if (adjacencyWriter != null) {
			try {
				stats.writeAdjacencyArray(name[0] + " " + name[1],
						adjacencyWriter, weighted);
			} finally {
				adjacencyWriter.close();
			}
		}
	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param f
	 *            File to write data to
	 * @param stats
	 *            Statistics Object
	 * @throws FileCreateException
	 */
	private void writeStatisticsFile(File f, Statistics stats)
			throws FileCreateException {
		Document document = new Document();

		document.setEncoding("UTF-8");
		document.setVersion("1.0");
		Element study = document.setRoot("Statistics");

		study.setAttribute("StudyId", Long.toString(egoClient.getStudy()
				.getStudyId()));
		study.setAttribute("Creator", Shared.version);

		stats.writeStructuralStatistics(study);
		egoClient.getInterview().writeEgoAnswers(study);
		stats.writeCompositionalStatistics(study);

		try {
			document.write(f);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"Unable to write statistics.", "Interview Error",
					JOptionPane.ERROR_MESSAGE);
			throw new FileCreateException();
		}
	}

	/***************************************************************************
	 * Creates a new csv file to store statistics in
	 * 
	 * @param parent
	 *            Frame for centering error messages
	 * @param filetype
	 *            file type for newFile call
	 * @param defaultname
	 *            Default name of new file for newFile call
	 * @returns PrintWriter for csv file
	 * @throws IOException
	 */
	public PrintWriter newStatisticsPrintWriter(String filetype, String suffix,
			String defaultname) {
		Preferences prefs = Preferences.userNodeForPackage(EgoClient.class);
		File startdir;
		PrintWriter out = null;
		File file;

		if (getPackageFile() != null) {
			startdir = new File(getPackageFile().getParent(), "/Statistics/");
		} else {
			startdir = new File(prefs.get("STAT_DIR", "Statistics/"));
		}

		try {
			file = FileHelpers.newFile(filetype, defaultname, "Statistics", "."
					+ suffix, startdir, egoClient.getFrame(), false);

			if (file == null) {
				throw new FileCreateException();
			}

			out = new PrintWriter(new BufferedWriter(new FileWriter(file),
					(32 * 1024)));
			prefs.put("STAT_DIR", file.getParent());
		} catch (Exception fce) {
			JOptionPane.showMessageDialog(egoClient.getFrame(), "Unable to create "
					+ filetype + "file.", "New Statics File Error",
					JOptionPane.ERROR_MESSAGE);
			out = null;
		}

		return out;
	}

	/***************************************************************************
	 * Creates a new .ist file from an existing complete interview file
	 * 
	 * @param interviewFile
	 *            File from which to read interview
	 * @throws FileCreateException
	 */
	public void generateStatisticsFile(File interviewFile)
			throws FileCreateException {
		egoClient.setInterview(null);

		setInterviewFile(interviewFile);

		if (egoClient.getStorage().getInterviewFile() != null) {
			egoClient.setInterview(egoClient.getStorage().readInterview());
		}

		if ((egoClient.getInterview() != null) && egoClient.getInterview().isComplete()) {
			egoClient.getInterview().completeInterview();
		} else if(egoClient.getInterview() == null) {
			throw new FileCreateException("Interview for " + interviewFile.getName() + " could not be read.");
		} else {
			throw new FileCreateException("Interview for " + interviewFile.getName() + " was not completed.");
		}
	}

	public void writeGraphSettings(File settingsFile,
			Iterator QAsettingsIterator) {

	}

	public static File int2ist(String istPath, String intFile) {
		return (new File(istPath, intFile
				.substring(0, intFile.lastIndexOf("."))
				+ ".ist"));
	}

	public static File int2matrix(String istPath, String intFile) {
		return (new File(istPath, intFile
				.substring(0, intFile.lastIndexOf("."))
				+ "_matrix.csv"));
	}

	public static File int2weightedmatrix(String istPath, String intFile) {
		return (new File(istPath, intFile
				.substring(0, intFile.lastIndexOf("."))
				+ "_weighted_matrix.csv"));
	}

}
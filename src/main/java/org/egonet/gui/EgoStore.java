package org.egonet.gui;

import java.util.*;
import java.util.prefs.Preferences;
import java.awt.Window;
import java.io.*;

import javax.swing.filechooser.FileFilter;

import net.sf.functionalj.Function0;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.exceptions.EgonetException;
import org.egonet.exceptions.StudyIdMismatchException;
import org.egonet.io.InterviewFileFilter;
import org.egonet.io.InterviewReader;
import org.egonet.io.InterviewWriter;
import org.egonet.io.PDFWriter;
import org.egonet.io.RTFWriter;
import org.egonet.io.StatisticsFileWriter;
import org.egonet.io.StudyReader;
import org.egonet.io.StudyWriter;
import org.egonet.io.VnaInterviewWriter;
import org.egonet.model.answer.*;
import org.egonet.model.question.Question;
import org.egonet.util.ExtensionFileFilter;
import org.egonet.util.FileHelpers;
import org.egonet.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.*;

import javax.swing.*;

/**
 * This class represents storage for a session of the client. It specifically
 * encapsulates one dataset of files, i.e. one study and many interviews.
 * 
 * @author Martin
 * 
 */
public class EgoStore {

	final private static Logger logger = LoggerFactory.getLogger(EgoStore.class);

	// a window for dialogs to parent
	private Window parent;

	// represents all loaded interviews and their study
	private Tuple<File, Study> currentStudy;

	// represents an interview in progress
	private Tuple<File, Interview> currentInterview;

	// useful file filters
	protected static final FileFilter packageFilter = new ExtensionFileFilter("Study Definition Files", "ego");

	// constant lookup for the prefs API
	protected static final String FILE_PREF = "FILE_PREF";

	private static final String[] questionExtensions = { "qst", "qtp" };
	private static FileFilter readQuestionFilter = (FileFilter) new ExtensionFileFilter("Question Files", questionExtensions[0]);
	private static FileFilter writeQuestionFilter = (FileFilter) new ExtensionFileFilter("Question Templates", questionExtensions);
	private static FileFilter studyFilter = new ExtensionFileFilter(
			"Study Files", "ego");

	public String str = "uninit";

	public EgoStore(Window parent) {
		this.parent = parent;

		// terrible hack to try to figure out WHO created us
		try {

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			PrintStream pis = new PrintStream(bos);

			Throwable t = new Throwable();
			t.setStackTrace(Thread.currentThread().getStackTrace());
			t.printStackTrace(pis);

			pis.flush();

			str = this + " " + os.toString();
		} catch (Throwable ex) {
			logger.error(ex.toString());
		}
	}

	public void writeCurrentInterview() throws IOException {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		logger.info("Writing current interview called on EgoStore instance "+this.toString());
		if(ste.length >= 2)
			logger.info("Called from " + ste[1].toString());
		logger.info("Being asked to write interview: " + currentInterview.second().dump());
		InterviewWriter iw = new InterviewWriter(currentStudy.second(), currentInterview.first());
		iw.setInterview(currentInterview.second());
	}

	public void setCurrentInterview(Interview interview, File file) {
		logger.info("Set current interview " + (file == null ? file : file.toString()) + " with " + (interview == null ? interview : interview.toString()));
		currentInterview = new Tuple<File, Interview>(file, interview);
	}

	public void unsetCurrentInterview() {
		logger.info("Unset current interview");
		currentInterview = null;
	}
	
	public boolean saveInterview(File fInterview) throws IOException {
		logger.info("Saving a brand new, never before used file");
		setCurrentInterview(currentInterview.second(), fInterview);
		writeCurrentInterview();
		return true;
	}
	
	public boolean continueInterview(File fInterview) throws IOException, CorruptedInterviewException {
		logger.info("Saving on top of an existing file");
		setCurrentInterview(readInterview(fInterview), fInterview);
		
		setCurrentInterview(currentInterview.second(), fInterview);
		writeCurrentInterview();
		return true;
	}
	
	public boolean saveLongitudinalFile(File fOriginal, File fInterview) throws IOException, CorruptedInterviewException {
		InterviewReader tempIr = new InterviewReader(currentStudy.second(), fOriginal);
		Interview curInt = tempIr.getInterview();
		File newFile = fInterview;
		
		curInt.setComplete(false);
		for(Answer answer : curInt.getEgoAnswers()) {
			answer.setAnswered(false);
		}
		for(Question answer : curInt.getAlterAnswers()) {
			answer.getAnswer().setAnswered(false);
		}
		
		curInt.setFollowup(true);
		
		setCurrentInterview(curInt, newFile);
		writeCurrentInterview();
		return true;
	}

	public Window getParent() {
		return parent;
	}

	public void setParent(Window parent) {
		this.parent = parent;
	}

	public Interview readInterview(File interviewFile) throws CorruptedInterviewException, IOException {
		return readInterview(interviewFile, false);
	}
	
	/***************************************************************************
	 * Reads in study information from an XML like input file Includes files
	 * paths and arrays of question orders
	 * 
	 * @return interview structure derived from file 
	 */
	public Interview readInterview(File interviewFile, boolean ignoreStudyIdMismatch)
			throws CorruptedInterviewException, IOException {
		try {
			InterviewReader ir = new InterviewReader(currentStudy.second(),
					interviewFile);
			Interview interview = ir.getInterview(ignoreStudyIdMismatch);

			if (!interview.isComplete()
					&& InterviewReader.checkForCompleteness(interview)) {
				String msg = interviewFile.getName() + " does not indicate a completed interview, but Egonet has determined that all questions have been answered. Would you like to mark it completed now and save it?";
				int choice = JOptionPane.showConfirmDialog(parent, msg, "Read Interview Error", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					interview.setComplete(true);
					InterviewWriter iw = new InterviewWriter(currentStudy.second(), interviewFile);
					iw.setInterview(interview);
				}
			}

			currentInterview = new Tuple<File, Interview>(interviewFile,
					interview);

			return interview;
		} catch (CorruptedInterviewException e) {
			String msg = (e != null && !e.getMessage().equals("") ? " "
					+ e.getMessage() : "");

			JOptionPane.showMessageDialog(parent, "Unable to Read Interview."
					+ msg, "Read Interview Error", JOptionPane.ERROR_MESSAGE);
			throw e;
		} catch (IOException e) {
			String msg = (e != null && !e.getMessage().equals("") ? " "
					+ e.getMessage() : "");

			JOptionPane.showMessageDialog(parent, "Unable to Read Interview."
					+ msg, "Read Interview Error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	public void exportStudy(boolean includeInterview)
			throws CorruptedInterviewException {
		JFileChooser exportFileChooser = new JFileChooser("Export Study...");
		File exportFile;
		boolean complete = false;

		if (currentStudy.first() != null)
			exportFileChooser.setCurrentDirectory(currentStudy.first()
					.getParentFile());

		exportFileChooser.addChoosableFileFilter(new ExtensionFileFilter(
				"Rich Text Format (RTF)", "rtf")); // add filters for any export
		// formats we know about
		exportFileChooser.addChoosableFileFilter(new ExtensionFileFilter(
				"Portable Document Format (PDF)", "pdf")); // add filters for
		// any export
		// formats we know
		// about

		while (!complete) {
			if (JFileChooser.APPROVE_OPTION == exportFileChooser
					.showSaveDialog(parent)) {
				try {
					int confirm = JOptionPane.OK_OPTION;

					FileFilter filter = exportFileChooser.getFileFilter();
					if (filter instanceof ExtensionFileFilter)
						exportFile = ((ExtensionFileFilter) filter)
								.getCorrectFileName(exportFileChooser
										.getSelectedFile());
					else
						exportFile = exportFileChooser.getSelectedFile();

					if (!exportFile.createNewFile()) {
						if (exportFile.canWrite()) {
							confirm = JOptionPane
									.showConfirmDialog(
											parent,
											"<HTML><h3>A file already exists at this location.</h3>"
													+ "<p>Shall I overwrite it?</p></html>",
											"Overwrite file",
											JOptionPane.OK_CANCEL_OPTION);
						} else {
							confirm = JOptionPane
									.showConfirmDialog(
											parent,
											"<HTML><h2>File already exists at this location.</h2>"
													+ "<p>I cannot overwrite it.</p></html>",
											"File already exists",
											JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					if (confirm == JOptionPane.OK_OPTION) {
						if (!exportFile.canWrite()) {
							throw (new java.io.IOException(
									"Cannot write to file"));
						}

						if (exportFile.getName().toLowerCase().endsWith("pdf")) {

							PDFWriter pw = includeInterview ? new PDFWriter(currentStudy.second(), currentInterview.second()) : new PDFWriter(currentStudy.second(), "Interviewee");
							pw.write(exportFile);
						} else if (exportFile.getName().toLowerCase().endsWith("rtf")) {
							RTFWriter pw = includeInterview ? new RTFWriter(currentStudy.second(), currentInterview.second()) : new RTFWriter(currentStudy.second(), "Interviewee");
							pw.write(exportFile);

						}
						complete = true;
					}
				} catch (java.io.IOException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to write to file. File not saved.");
					throw new RuntimeException(e);
				}
			} else {
				complete = true;
			}
		}
	}

	public File getStudyFile() {
		return currentStudy.first();
	}

	public Study getStudy() {
		// logger.info("Returning study from " + this.str);

		return currentStudy.second();
	}

	public File getInterviewFile() {
		return currentInterview != null ? currentInterview.first() : null;
	}

	public Interview getInterview() {
		return currentInterview != null ? currentInterview.second() : null;
	}

	public void chooseStudy() throws IOException, EgonetException {
		File studyFile = selectStudy(currentStudy != null ? currentStudy
				.first() : null);
		StudyReader sr = new StudyReader(studyFile);
		Study study = sr.getStudy();
		setCurrentStudy(studyFile, study);
	}

	/**
	 * Show the user a dialog box to select a study file, then return that file.
	 * 
	 * @param parent
	 *            a parent Window for the dialog box
	 * @param studyFile
	 *            a parent directory for the study
	 * @return null the selected file
	 * @throws IOException
	 *             if the dialog was cancelled
	 */
	public File selectStudy(File studyFile) throws IOException {

		JFileChooser jNewStudyChooser = new JFileChooser();

		Preferences prefs = null;
		try {
			prefs = Preferences.userNodeForPackage(getClass());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		jNewStudyChooser.addChoosableFileFilter(packageFilter);
		jNewStudyChooser.setDialogTitle("Select Study Definition File");

		if (studyFile != null) {
			jNewStudyChooser.setCurrentDirectory(studyFile.getParentFile());
		} else {
			String userHome = ".";
			try {
				userHome = System.getProperty("user.home", ".");
			} catch (Throwable t) {
				t.printStackTrace();
			}
			File directory = new File(userHome);
			if (prefs != null && !prefs.get(FILE_PREF, ".").equals("."))
				directory = new File(prefs.get(FILE_PREF, "."));

			jNewStudyChooser.setCurrentDirectory(directory);
		}

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser
				.showOpenDialog(parent)) {
			File f = jNewStudyChooser.getSelectedFile();

			if (f != null) {
				try {
					if (!f.canRead())
						throw new IOException("File exists but was unreadable");

					// Store location in prefs file
					if (prefs != null)
						prefs.put(FILE_PREF, f.getParent());
					return f;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"Unable to read study file.", "File Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		throw new IOException("Dialog cancelled");
	}

	public void readStudy(File file) {
		if (file != null) {
			try {
				StudyReader sr = new StudyReader(file);

				if (sr.isStudyInUse()) {
					JOptionPane
							.showMessageDialog(
									parent,
									"This study has already been used for at least one interview.\n"
											+ "You may change the text of questions while still using previously generated interview files. However, \n"
											+ "if you add, delete, reorder, or modify the answer types of any questions you will no longer be able to use \n"
											+ "it to view existing interview files.",
									"File In Use", JOptionPane.WARNING_MESSAGE);
				}

				Study study = sr.getStudy();
				setCurrentStudy(file, study);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parent,
						"Unable to read this study file",
						"Study Reading Error", JOptionPane.ERROR_MESSAGE);

				setCurrentStudy(null, null);
			}
		}
	}

	/************************************************************************************************************************************************************
	 * Select a directory in which to store project related files Create
	 * subdirectories if needed.
	 */
	public void newStudyFiles() {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		JFileChooser jNewStudyChooser = new JFileChooser();
		File dirFile, newStudyFile;
		String projectPath = null;
		String projectName = null;

		jNewStudyChooser.addChoosableFileFilter(studyFilter);
		jNewStudyChooser.setDialogTitle("Select Study Path");

		if (getStudyFile() != null) {
			jNewStudyChooser
					.setCurrentDirectory(getStudyFile().getParentFile());
		} else {
			File directory = new File(prefs.get(FILE_PREF, "."));
			jNewStudyChooser.setCurrentDirectory(directory);
		}

		try {
			if (JFileChooser.APPROVE_OPTION == jNewStudyChooser
					.showSaveDialog(parent)) {
				projectPath = jNewStudyChooser.getSelectedFile().getParent();
				projectName = jNewStudyChooser.getSelectedFile().getName();

				if (projectName.indexOf(".") != -1) {
					projectName = projectName.substring(0, projectName
							.indexOf("."));
				}

				try {
					String folder = projectPath.substring(projectPath
							.lastIndexOf(File.separator) + 1);
					if (!folder.equals(projectName)) {
						dirFile = new File(projectPath, projectName);
						dirFile.mkdir();
						projectPath = dirFile.getPath();
					}
				} catch (SecurityException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to create study directories.",
							"New Study Error", JOptionPane.ERROR_MESSAGE);
					throw new IOException("Cannot create study directory for "
							+ projectPath);
				}

				try {
					newStudyFile = new File(projectPath, projectName);
					newStudyFile = ((ExtensionFileFilter) studyFilter)
							.getCorrectFileName(newStudyFile);
					if (!newStudyFile.createNewFile()) {
						int confirm = JOptionPane
								.showConfirmDialog(
										parent,
										"<HTML><h2>Study already exists at this location.</h2>"
												+ "<p>Shall I overwrite it?</p></html>",
										"Overwrite Study File",
										JOptionPane.OK_CANCEL_OPTION);

						if (confirm != JOptionPane.OK_OPTION) {
							// do not overwrite
							throw new IOException("Won't overwrite "
									+ newStudyFile.getName());
						} else {
							// delete the existing file and create a new one
							newStudyFile.delete();
							newStudyFile.createNewFile();
						}
					}

					// logger.info("Creating new study2, ditching " +
					// currentStudy);
					Throwable t = new Throwable();
					t.setStackTrace(Thread.currentThread().getStackTrace());
					// t.printStackTrace(System.out);

					/* Clean out study variables */
					Study study = new Study();
					study.setStudyId(System.currentTimeMillis()+"");

					setCurrentStudy(newStudyFile, study);
					getStudy().setStudyName(projectName);

					/* Write out default info */
					StudyWriter sw = new StudyWriter(newStudyFile);
					sw.setStudy(study);

					// Store location in prefs file
					prefs.put(FILE_PREF, newStudyFile.getParent());
				} catch (java.io.IOException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to create study file.", "File Error",
							JOptionPane.ERROR_MESSAGE);
					throw new IOException(e);
				}

				try {
					dirFile = new File(projectPath, "Statistics");
					dirFile.mkdir();

					dirFile = new File(projectPath, "Interviews");
					dirFile.mkdir();
				} catch (SecurityException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to create study directories.",
							"New Study Error", JOptionPane.ERROR_MESSAGE);
					throw new IOException(e);
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, "Study not created.");
		}
	}

	public void selectStudy() {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		JFileChooser jNewStudyChooser = new JFileChooser();
		File f;

		jNewStudyChooser.addChoosableFileFilter(studyFilter);
		jNewStudyChooser.setDialogTitle("Select Study");

		if (getStudyFile() != null) {
			jNewStudyChooser
					.setCurrentDirectory(getStudyFile().getParentFile());
		} else {
			jNewStudyChooser.setCurrentDirectory(new File(prefs.get(FILE_PREF,
					".")));
		}

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser
				.showOpenDialog(parent)) {
			f = jNewStudyChooser.getSelectedFile();

			try {
				if (!f.canRead()) {
					throw new IOException("Cannot read study file");
				} else {
					readStudy(f);

					// Store location in prefs file
					prefs.put(FILE_PREF, f.getParent());
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Unable to read study file.", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
		public static File getLibraryDirectory()
		{
			return new File("./lib/");
		}

	

	/************************************************************************************************************************************************************
	 * Select a question file to use for custom questions
	 */
	public void importQuestions() throws Exception {
		JFileChooser jNewStudyChooser = new JFileChooser();
		File newFile;

		jNewStudyChooser.setCurrentDirectory(getLibraryDirectory());
		jNewStudyChooser.addChoosableFileFilter(readQuestionFilter);
		jNewStudyChooser.setDialogTitle("Select Custom Questions File");

		if (JFileChooser.APPROVE_OPTION == jNewStudyChooser
				.showOpenDialog(parent)) {
			newFile = jNewStudyChooser.getSelectedFile();

			try {
				if (!newFile.canRead()) {
					throw (new IOException("Cannot read file " + newFile));
				}

				List<Question> questions = StudyReader.getQuestions(newFile);
				for (Question q : questions)
					getStudy().addQuestion(q);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"Unable to read question file.", "File Error",
						JOptionPane.ERROR_MESSAGE);
				throw ex;
			}
		}
	}

	/************************************************************************************************************************************************************
	 * Save study information to a file with a new name
	 */
	public void saveStudyFile() {
		File studyFile = getStudyFile();

		try {
			if (!studyFile.exists()) {
				throw new java.io.IOException("File " + studyFile.getName()
						+ " does not exist");
			}
			if (!studyFile.canWrite()) {
				throw new java.io.IOException("File " + studyFile.getName()
						+ " is not writeable");
			}

			StudyWriter sw = new StudyWriter(studyFile);
			sw.setStudy(getStudy());

		} catch (Throwable ex) {
			// JOptionPane.showMessageDialog(parent,
			// "Unable to write to study file. Study not saved.");
			throw new RuntimeException(ex);
		}
	}

	/************************************************************************************************************************************************************
	 * Save question information to a file with a new name
	 */
	public void exportQuestions() {
		JFileChooser jNewQuestionsChooser = new JFileChooser();
		File newQuestionFile;

		jNewQuestionsChooser.setCurrentDirectory(new File(getStudyFile()
				.getParent(), "/Questions/"));
		jNewQuestionsChooser.addChoosableFileFilter(writeQuestionFilter);
		jNewQuestionsChooser.setDialogTitle("Save Custom Questions As...");

		if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser
				.showSaveDialog(parent)) {
			try {
				newQuestionFile = ((ExtensionFileFilter) writeQuestionFilter)
						.getCorrectFileName(jNewQuestionsChooser
								.getSelectedFile());
				if (!newQuestionFile.createNewFile()) {
					int confirm = JOptionPane.showConfirmDialog(parent,
							"<HTML><h2>Question File already exists at this location.</h2>"
									+ "<p>Shall I overwrite it?</p></html>",
							"Overwrite Questions File",
							JOptionPane.OK_CANCEL_OPTION);

					if (confirm != JOptionPane.OK_OPTION) {
						throw new IOException("Won't overwrite "
								+ newQuestionFile.getName());
					}
				}

				writeAllQuestions(newQuestionFile);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parent,
						"Unable to create question file.", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/************************************************************************************************************************************************************
	 * Save study info and questions as a package
	 */
	public void saveAsStudyFile() {
		JFileChooser jNewQuestionsChooser = new JFileChooser("Save Study As...");
		File newStudyFile;
		boolean complete = false;

		if (getStudyFile() != null)
			jNewQuestionsChooser.setCurrentDirectory(getStudyFile()
					.getParentFile());
		jNewQuestionsChooser.addChoosableFileFilter(studyFilter);

		while (!complete) {
			if (JFileChooser.APPROVE_OPTION == jNewQuestionsChooser
					.showSaveDialog(parent)) {
				try {
					int confirm = JOptionPane.OK_OPTION;
					newStudyFile = ((ExtensionFileFilter) studyFilter)
							.getCorrectFileName(jNewQuestionsChooser
									.getSelectedFile());

					if (!newStudyFile.createNewFile()) {
						if (newStudyFile.canWrite()) {
							confirm = JOptionPane
									.showConfirmDialog(
											parent,
											"<HTML><h3>A Study File already exists at this location.</h3>"
													+ "<p>Shall I overwrite it?</p></html>",
											"Overwrite Study Package File",
											JOptionPane.OK_CANCEL_OPTION);
						} else {
							confirm = JOptionPane
									.showConfirmDialog(
											parent,
											"<HTML><h2>An <b>Active</b> Study File already exists at this location.</h2>"
													+ "<p>If you overwrite it, any interviews created with it will be unreadable!</p>"
													+ "<p>Shall I overwrite it?</p></html>",
											"Overwrite Study Package File",
											JOptionPane.OK_CANCEL_OPTION);
						}
					}

					if (confirm == JOptionPane.OK_OPTION) {
						if (!newStudyFile.canWrite()) {
							throw (new java.io.IOException());
						}

						StudyWriter sw = new StudyWriter(newStudyFile);
						getStudy().setStudyId(System.currentTimeMillis()+"");
						sw.setStudy(getStudy());

						setCurrentStudy(newStudyFile, getStudy());
						complete = true;

						// Store location in prefs file
						Preferences prefs = Preferences
								.userNodeForPackage(getClass());
						prefs.put(FILE_PREF, newStudyFile.getParent());
					}
				} catch (java.io.IOException e) {
					JOptionPane.showMessageDialog(parent,
							"Unable to write to study file. Study not saved.");
					throw new RuntimeException(e);
				}
			} else {
				complete = true;
			}
		}
	}

	private void writeAllQuestions(File f) throws IOException {

		StudyWriter sw = new StudyWriter(f);
		sw.writeAllQuestionData(getStudy().getQuestions());
	}

	public void createNewStudy() {
		setCurrentStudy(null, new Study());
	}

	public boolean isStudyLoaded() {
		return currentStudy != null && currentStudy.second() != null;
	}

	/***************************************************************************
	 * Select a directory in which to store project related files Create
	 * subdirectories if needed.
	 */

	public void selectInterview(final Function0<Void> whenDone) {

		File packageFile = getStudyFile();
		File parentFile = packageFile.getParentFile();
		File interviewFile = new File(parentFile, "/Interviews/");

		File guessLocation = new File(".");
		if (parentFile.exists() && parentFile.isDirectory()
				&& parentFile.canRead())
			guessLocation = parentFile;

		if (interviewFile.exists() && interviewFile.isDirectory()
				&& interviewFile.canRead())
			guessLocation = interviewFile;

		final File currentDirectory = guessLocation;

		final int numFiles = currentDirectory.list().length;

		final InterviewFileFilter filter = new InterviewFileFilter(getStudy(),
				"Interview Files", "int");
		final JFileChooser jNewInterviewChooser = new JFileChooser();
		final ProgressMonitor progressMonitor = new ProgressMonitor(
				parent,
				"Searching for interviews and caching the list of files that match the current study",
				"", 0, numFiles);
		jNewInterviewChooser.setCurrentDirectory(currentDirectory);
		jNewInterviewChooser.setDialogTitle("Select Interview File");

		progressMonitor.setMaximum(numFiles);

		org.jdesktop.swingworker.SwingWorker<Integer, Integer> filterWorker = new org.jdesktop.swingworker.SwingWorker<Integer, Integer>() {
			public Integer doInBackground() {
				filter.cacheList(currentDirectory, progressMonitor);
				return 0;
			}

			public void done() {
				progressMonitor.close();

				jNewInterviewChooser.addChoosableFileFilter(filter);
				int result = jNewInterviewChooser.showOpenDialog(parent);

				if (JFileChooser.APPROVE_OPTION == result) {
					final File f = jNewInterviewChooser.getSelectedFile();

					try {
						// if (!(f != null && f.canRead())) {
						if (!f.canRead()) {
							throw new IOException("Couldn't read file");
						} else {
							boolean complete = true;
							setCurrentInterview(null, f);

							try {
								InterviewReader sr = new InterviewReader(getStudy(), f);
								complete = sr.getInterview().isComplete();
							} catch (StudyIdMismatchException ex) {
								int confirm = JOptionPane.showConfirmDialog(parent, "Study file and interview file don't match. Do you wish to override? (This may not work)", "", JOptionPane.YES_NO_OPTION);
								if(confirm != JOptionPane.YES_OPTION) {
									complete = false;
									throw new RuntimeException(ex);
								}
								try {
									InterviewReader sr = new InterviewReader(getStudy(), f);
									complete = sr.getInterview(true).isComplete();
								}
								catch (Exception ex2) {
									complete = false;
									throw new RuntimeException(ex2);
								}
								
							} catch (Exception ex) {
								complete = false;
								throw new RuntimeException(ex);
							}

							readInterview(f, true);
							if (complete == false) {
								JOptionPane
										.showMessageDialog(
												parent,
												"This interview is not completed and no "
														+ "graph or statictical data will be displayed!",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
							}
							// else {
							whenDone.call();
							// }
						}
					} catch (Throwable e) {
						JOptionPane.showMessageDialog(null,
								"Unable to read interview file.", "File Error",
								JOptionPane.ERROR_MESSAGE);
						throw new RuntimeException(e);
					}
				}

			}

		};
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);

		filterWorker.execute();
	}

	public File interviewStatisticsFile(String istPath, String intFile) {
		return createFileWithNewEndingFromDot(istPath,intFile,".ist");
	}
	
	private File createFileWithNewEndingFromDot(String path, String oldFileName, String newEnding) {
		String n = oldFileName;
		if(n.contains("."))
			n= n.substring(0, oldFileName.lastIndexOf("."));
		return (new File(path, n + newEnding));
	}

	public void writeStatisticsFiles(Statistics stats)
			throws IOException {
		String name = getInterviewFile().getName().replace(".int", "");
		String statdir;
		String parentDir;

		if (getStudyFile() != null) {
			parentDir = getStudyFile().getParent();
			statdir = (new File(parentDir, "/Statistics/")).getCanonicalPath();

			File statFile = interviewStatisticsFile(statdir, name);
			writeStatisticsFile(statFile, stats);

                        File altByAltFile = createFileWithNewEndingFromDot(statdir, name, "_alter_by_alter_prompt_Matrix.csv");
                        PrintWriter pwABAP = new PrintWriter(altByAltFile);
			stats.writeAlterByPromptFile(pwABAP, name);
                        
			File adjFile = createFileWithNewEndingFromDot(statdir,name,"_matrix.csv");
			PrintWriter pwNA = new PrintWriter(adjFile);
			stats.writeAdjacencyFile(pwNA, name, false);

			File wadjFile = createFileWithNewEndingFromDot(statdir,name,"_weighted_matrix.csv");
			PrintWriter pwAA = new PrintWriter(wadjFile);
			stats.writeAdjacencyFile(pwAA, name, true);
			
			File asFile = createFileWithNewEndingFromDot(statdir,name,"_alter_summary.csv");
			PrintWriter pwAS = new PrintWriter(asFile);
			stats.writeAlterArray(pwAS);
			pwAS.close();
			
			new VnaInterviewWriter(stats.getStudy(),stats.getInterview())
			.write(createFileWithNewEndingFromDot(statdir,name,".vna"));
		}
		else {
			logger.warn("Study file was null. While you asked for statistics, you aren't going to get any.");
		}

	}

	/***************************************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param f
	 *            File to write data to
	 * @param stats
	 *            Statistics Object
	 * @throws IOException
	 */
	private void writeStatisticsFile(File f, Statistics stats)
			throws IOException {
		StatisticsFileWriter sfw = new StatisticsFileWriter(getStudy(),
				getInterview(), f);
		sfw.writeStatisticsFile(stats);
	}

	public void setPackageInUse() {
		try {
			StudyWriter sr = new StudyWriter(getStudyFile());
			sr.setStudyInUse(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Unable to update study file.",
					"File Error", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException(ex);
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

		if (getStudyFile() != null) {
			startdir = new File(getStudyFile().getParent(), "/Statistics/");
		} else {
			startdir = new File(prefs.get("STAT_DIR", "Statistics/"));
		}

		try {
			file = FileHelpers.newFile(filetype, defaultname, "Statistics", "."
					+ suffix, startdir, null, false);

			if (file == null) {
				throw new IOException("empty file");
			}

			out = new PrintWriter(new BufferedWriter(new FileWriter(file),
					(32 * 1024)));
			prefs.put("STAT_DIR", file.getParent());
		} catch (Exception fce) {
			JOptionPane.showMessageDialog(parent, "Unable to create "
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
	 * @throws IOException
	 * @throws IOException
	 * @throws CorruptedInterviewException
	 */
	public void generateStatisticsFile(File interviewFile) throws IOException,
			CorruptedInterviewException {

		if (interviewFile != null) {
			readInterview(interviewFile);
		}

		if ((getInterview() != null) && getInterview().isComplete()) {
			getInterview().completeInterview(this);
		} else if (getInterview() == null) {
			throw new IOException("Interview for " + interviewFile.getName()
					+ " could not be read.");
		} else {
			throw new IOException("Interview for " + interviewFile.getName()
					+ " was not completed.");
		}
	}

	public void setCurrentStudy(File file, Study study) {
		currentStudy = new Tuple<File, Study>(file, study);
	}

	public String parseISToString(java.io.InputStream is) {
		java.io.DataInputStream din = new java.io.DataInputStream(is);
		StringBuffer sb = new StringBuffer();
		try {
			String line = null;
			while ((line = din.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}
}

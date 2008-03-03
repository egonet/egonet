package com.endlessloopsoftware.ego;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: Question.java,v 1.1 2005/08/02 19:36:02 samag Exp $
 *
 */

import java.util.Date;

import com.endlessloopsoftware.egonet.data.QuestionLinkDataValue;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.SelectionDataValue;

import org.egonet.exceptions.MalformedQuestionException;
import org.egonet.util.listbuilder.Selection;

import electric.xml.Element;
import electric.xml.Elements;

/*******************************************************************************
 * Routines for creating and handling atomic question elements
 */
public class Question implements Cloneable {
	public boolean centralMarker = false;

	public boolean statable = false;

	public Long UniqueId = new Long(new Date().getTime());

	public int questionType = Question.EGO_QUESTION;

	public String title = "";

	public String text = "";

	public String citation = "";

	public int answerType = Question.TEXT;

	public int numQAlters = -1;

	public QuestionLink link = new QuestionLink();

	/*
	 * public class selection extends Selection { public boolean modified=false; }
	 */

	public Selection[] selections = new Selection[0];

	public Answer answer = new Answer(new Long(-1));

	/* Constants */
	public static final int MIN_QUESTION_TYPE = 1;

	public static final int STUDY_CONFIG = 0;

	public static final int EGO_QUESTION = 1;

	public static final int ALTER_PROMPT = 2;

	public static final int ALTER_QUESTION = 3;

	public static final int ALTER_PAIR_QUESTION = 4;

	public static final int NUM_QUESTION_TYPES = 5;

	public static final int ALL_QUESTION_TYPES = 5;

	public static final int MAX_QUESTION_TYPE = 4;

	public static final int MIN_ANSWER_TYPE = 0;

	public static final int CATEGORICAL = 0;

	public static final int NUMERICAL = 1;

	public static final int TEXT = 2;

	public static final int MAX_ANSWER_TYPE = 2;

	public static final int MAX_CATEGORICAL_CHOICES = 5;

	public static final String[] questionName = { "Study", "Ego",
			"Alter Prompt", "Alter", "Alter Pair" };

	/***************************************************************************
	 * Creates question
	 * 
	 * @return question new question
	 */
	public Question() {
	}

	/***************************************************************************
	 * Creates question with string as title
	 * 
	 * @param s
	 *            question title
	 * @return question new question
	 */
	public Question(String s) {
		this.title = s;
	}

	/***************************************************************************
	 * Converts a QuestionDataValue to a question object
	 * 
	 * @param question
	 *            XML element of question
	 * @param base
	 *            whether this is from the base file
	 * @throws MalformedQuestionException
	 *             if theres is a problem with the XML representation
	 */
	public Question(QuestionDataValue data) {
		this.UniqueId = data.getId();
		this.questionType = data.getQuestionType();
		this.answerType = data.getAnswerType();
		this.title = data.getTitle();
		this.text = data.getText();
		this.citation = data.getCitation();

		SelectionDataValue[] selectionData = data.getSelectionDataValues();

		/* Fix to properly display Apple UI interviews */
		if (this.questionType == Question.ALTER_PROMPT) {
			this.answerType = Question.TEXT;
		}

		/*
		 * temp vars for determining statable, a question must have at least one
		 * of each selection type to be statable
		 */
		boolean adjacent = false;
		boolean nonadjacent = false;

		this.selections = new Selection[selectionData.length];
		for (int i = 0; i < selectionData.length; ++i) {

			Selection selection = new Selection();
			selection.setString(selectionData[i].getText());
			selection.setIndex(selectionData[i].getIndex());
			selection.setValue(selectionData[i].getValue());
			selection.setAdjacent(selectionData[i].getAdjacent());

			/*
			 * selection selectionobj = new selection(); selectionobj.string =
			 * selectionData[i].getText(); selectionobj.index =
			 * selectionData[i].getIndex(); selectionobj.value =
			 * selectionData[i].getValue(); selectionobj.adjacent =
			 * selectionData[i].getAdjacent();
			 */
			if (selection.isAdjacent())
				adjacent = true;
			else
				nonadjacent = true;

			selections[i] = selection;
		}

		/*
		 * a question must have at least one of each selection type to be
		 * statable
		 */
		this.statable = adjacent && nonadjacent;

		if (data.getQuestionLinkDataValue() != null) {
			QuestionLinkDataValue qlData = data.getQuestionLinkDataValue();

			this.link.active = true;
			this.link.answer = new Answer(qlData.getQuestionId());
			this.link.answer.value = qlData.getAnswerValue();
			this.link.answer.string = qlData.getAnswerString();
		} else {
			this.link.active = false;
		}
	}

	/***************************************************************************
	 * Reads a single question from an input stream
	 * 
	 * @param question
	 *            XML element of question
	 * @param base
	 *            whether this is from the base file
	 * @throws MalformedQuestionException
	 *             if theres is a problem with the XML representation
	 */
	public Question(Element question) throws MalformedQuestionException {
		if ((question.getElement("QuestionTitle") == null)
				|| (question.getElement("QuestionText") == null)
				|| (question.getElement("Id") == null)
				|| (question.getElement("QuestionType") == null)
				|| (question.getElement("AnswerType") == null)) {
			throw (new MalformedQuestionException());
		}

		this.title = question.getTextString("QuestionTitle");
		this.title = (this.title == null) ? "" : this.title;

		this.text = question.getTextString("QuestionText");
		this.text = (this.text == null) ? "" : this.text;

		this.citation = question.getTextString("Citation");
		this.citation = (this.citation == null) ? "" : this.citation;

		this.UniqueId = new Long(question.getLong("Id"));
		this.questionType = question.getInt("QuestionType");
		this.answerType = question.getInt("AnswerType");

		if (this.questionType == Question.ALTER_PROMPT) {
			this.answerType = Question.TEXT;
		}

		if (question.getAttribute("CentralityMarker") != null) {
			boolean centrality = question.getAttribute("CentralityMarker")
					.equals("true");

			if (centrality
					&& (this.questionType != Question.ALTER_PAIR_QUESTION)) {
				throw (new MalformedQuestionException());
			}
		}

		Element link = question.getElement("Link");
		if (link != null) {
			this.link.active = true;
			this.link.answer = new Answer(new Long(link.getLong("Id")));
			this.link.answer.value = link.getInt("value");

			/* Only support questions with single answers for link */
			this.link.answer.string = link.getTextString("string");
		}

		if (this.answerType == Question.CATEGORICAL) {
			Element answerList = question.getElement("Answers");

			if (answerList != null) {
				Elements selections = answerList.getElements("AnswerText");

				if (selections.size() == 0) {
					throw (new MalformedQuestionException());
				}

				/*
				 * temp vars for determining statable, a question must have at
				 * least one of each selection type to be statable
				 */
				boolean adjacent = false;
				boolean nonadjacent = false;

				this.selections = new Selection[selections.size()];

				while (selections.hasMoreElements()) {

					Element selection = selections.next();
					int index = Integer.parseInt(selection
							.getAttributeValue("index"));

					try {
						this.selections[index] = new Selection();
						this.selections[index].setString(selection
								.getTextString());
						this.selections[index]
								.setValue(Integer.parseInt(selection
										.getAttributeValue("value")));

						this.selections[index].setAdjacent(Boolean.valueOf(
								selection.getAttributeValue("adjacent"))
								.booleanValue());
						this.selections[index].setIndex(index);

					} catch (NumberFormatException ex) {
						this.selections[index].setValue(selections.size()
								- (index + 1));
						this.selections[index].setAdjacent(false);
					}

					if (this.selections[index].isAdjacent())
						adjacent = true;
					else
						nonadjacent = true;
				}

				/*
				 * a question must have at least one of each selection type to
				 * be statable
				 */
				this.statable = adjacent && nonadjacent;

				/* Check to make sure all answers are contiguous */
				for (int i = 0; i < selections.size(); i++) {
					if (this.selections[i] == null) {
						throw (new MalformedQuestionException());
					}
				}
			}
		}
	}

	/***************************************************************************
	 * Returns whether a given selection is adjacent based on the values stored
	 * in the question. Is used to override value found in an interview file
	 * 
	 * @param value
	 * @return true iff that selection is marked as adjacent
	 */
	public boolean selectionAdjacent(int value) {
		boolean rval = false;

		if (this.selections.length > 0) {
			int size = this.selections.length;

			for (int i = 0; i < size; i++) {
				if (value == this.selections[i].getValue()) {
					rval = this.selections[i].isAdjacent();
					break;
				}
			}
		}

		return rval;
	}

	/***************************************************************************
	 * Returns String representation of questionType
	 * 
	 * @param type
	 *            Question type to return as string
	 * @return string Question Type string
	 */
	public static String questionTypeString(int type) {
		return (new String(questionName[type]));
	}

	/***************************************************************************
	 * Overrides toString method for question, returns title
	 * 
	 * @return String title of question
	 */
	public String toString() {
		if (title == null) {
			return (new String("Untitled"));
		} else {
			return (title);
		}
	}

	/***************************************************************************
	 * Writes a single question to an output stream
	 * 
	 * @param w
	 *            Print Writer of open output file
	 * @param q
	 *            question
	 */
	public void writeQuestion(Element e, QuestionList list) {
		if (this.centralMarker) {
			e.setAttribute("CentralityMarker", "true");
		}

		e.addElement("Id").setLong(this.UniqueId.longValue());
		e.addElement("QuestionType").setInt(this.questionType);
		e.addElement("AnswerType").setInt(this.answerType);

		if ((this.title != null) && (!this.title.equals(""))) {
			e.addElement("QuestionTitle").setText(this.title);
		}

		if ((this.text != null) && (!this.text.equals(""))) {
			e.addElement("QuestionText").setText(this.text);
		}

		if ((this.citation != null) && (!this.citation.equals(""))) {
			e.addElement("Citation").setText(this.citation);
		}

		if (this.selections.length > 0) {
			int size = this.selections.length;
			Element selections = e.addElement("Answers");

			for (int i = 0; i < size; i++) {
				Element answer = selections.addElement("AnswerText");
				answer.setText(this.selections[i].getString());
				answer.setAttribute("index", Integer.toString(i));
				answer.setAttribute("value", Integer
						.toString(this.selections[i].getValue()));
				answer.setAttribute("adjacent",
						this.selections[i].isAdjacent() ? "true" : "false");
			}
		}

		if (this.link.active
				&& (list.getQuestion(this.link.answer.questionId) != null)) {
			try {
				Element link = e.addElement("Link");
				link.addElement("Id").setLong(
						this.link.answer.questionId.longValue());
				link.addElement("value").setInt(this.link.answer.value);
				link.addElement("string").setText(this.link.answer.string);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public QuestionDataValue getDataValue(QuestionList list, Long studyId) {
		QuestionEJBPK pk = new QuestionEJBPK(this.UniqueId, studyId);
		QuestionDataValue data = new QuestionDataValue(pk);

		data.setId(this.UniqueId);
		data.setQuestionType(this.questionType);
		data.setAnswerType(this.answerType);
		data.setTitle(this.title);
		data.setText(this.text);
		data.setCitation(this.citation);

		if (this.selections.length > 0) {
			int size = this.selections.length;

			for (int i = 0; i < size; i++) {
				SelectionDataValue selectionData = new SelectionDataValue();
				selectionData.setText(this.selections[i].getString());
				selectionData.setIndex(i);
				selectionData.setValue(this.selections[i].getValue());
				selectionData.setAdjacent(this.selections[i].isAdjacent());

				data.addSelectionDataValue(selectionData);
			}
		}

		if (this.link.active
				&& (list.getQuestion(this.link.answer.questionId) != null)) {
			try {
				QuestionLinkDataValue qlData = new QuestionLinkDataValue();
				qlData.setActive(true);
				qlData.setAnswerValue(this.link.answer.value);
				qlData.setAnswerString(this.link.answer.string);
				qlData.setQuestionId(this.link.answer.questionId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return data;
	}

	/***************************************************************************
	 * Implements Clone interface
	 * 
	 * @return Clone of Question
	 */
	public Object clone() {
		Question q;

		try {
			q = (Question) super.clone();
			q.link = (QuestionLink) this.link.clone();

			/*******************************************************************
			 * Dangerous to clone answers as multiple answers refer to same
			 * question Make sure they are assigned explicitly
			 */
			q.answer = null;
		} catch (CloneNotSupportedException ex) {
			q = null;
		}

		return q;
	}

	public String getString() {
		String str = "";
		str = "ID : " + UniqueId + " Title : " + title + " text : " + text
				+ "\nAnswer : " + answer.getString();
		return str;
	}
}

/**
 * $Log: Question.java,v $ Revision 1.1 2005/08/02 19:36:02 samag Initial
 * checkin
 * 
 * Revision 1.18 2004/04/11 00:24:48 admin Fixing headers
 * 
 * Revision 1.17 2004/04/11 00:17:13 admin Improving display of Alter Prompt
 * questions from Applet UI Interviews
 * 
 * Revision 1.16 2004/04/06 20:29:22 admin First pass as supporting interactive
 * applet linking interviews
 * 
 * Revision 1.15 2004/04/06 14:56:02 admin Work to integrate with Applet Linking
 * UI
 * 
 * Revision 1.14 2004/04/01 15:11:16 admin Completing Original UI work
 * 
 * Revision 1.13 2004/03/29 16:13:38 admin Fixed bug calculating statable
 * questions
 * 
 * Revision 1.12 2004/03/29 00:35:09 admin Downloading Interviews Fixing some
 * bugs creating Interviews from Data Objects
 * 
 * Revision 1.11 2004/03/28 17:31:31 admin More error handling when uploading
 * study to server Server URL selection dialog for upload
 * 
 * Revision 1.10 2004/03/22 00:00:34 admin Extended text entry area Started work
 * on importing studies from server
 * 
 * Revision 1.9 2004/03/21 14:00:38 admin Cleaned up Question Panel Layout using
 * FOAM
 * 
 * Revision 1.8 2004/03/10 14:32:39 admin Adding client library cleaning up code
 * 
 * Revision 1.7 2004/02/10 20:10:42 admin Version 2.0 beta 3
 * 
 * Revision 1.6 2004/01/23 13:36:07 admin Updating Libraries Allowing upload to
 * web server
 * 
 * Revision 1.5 2003/12/18 19:30:05 admin Small mods to support EJB persistence
 * 
 * Revision 1.4 2003/12/09 16:17:00 admin Fixing bug reading in adjacency
 * selections Clearing identity diagonal of Weighted Adjacency Matrix
 * 
 * Revision 1.3 2003/12/08 15:57:50 admin Modified to generate matrix files on
 * survey completion or summarization Extracted statistics models
 * 
 * Revision 1.2 2003/12/05 19:15:43 admin Extracting Study
 * 
 * Revision 1.1 2003/12/04 15:14:08 admin Merging EgoNet and EgoClient projects
 * so that they can share some common classes more easily.
 * 
 * Revision 1.2 2003/11/25 19:25:43 admin Warn before closing window
 * 
 * Revision 1.1.1.1 2003/06/08 15:09:40 admin Egocentric Network Survey
 * Authoring Module
 * 
 * Revision 1.16 2002/08/30 16:50:27 admin Using Selections
 * 
 * Revision 1.15 2002/08/30 09:35:38 admin Using Selection Class
 * 
 * Revision 1.14 2002/08/11 22:26:05 admin Final Statistics window, new file
 * handling
 * 
 * Revision 1.13 2002/08/08 17:07:25 admin Preparing to change file system
 * 
 * Revision 1.12 2002/07/25 14:54:24 admin Question Links
 * 
 * Revision 1.11 2002/07/24 14:17:51 admin new files
 * 
 * Revision 1.9 2002/07/18 14:43:06 admin New Alter Prompt Panel, packages
 * 
 * Revision 1.8 2002/06/30 15:59:18 admin Moving questions in lists, between
 * lists Better category input
 * 
 * Revision 1.7 2002/06/26 15:43:43 admin More selection dialog work File
 * loading fixes
 * 
 * Revision 1.6 2002/06/26 00:10:48 admin UI Work including base question
 * coloring and category selections
 * 
 * Revision 1.5 2002/06/25 15:41:02 admin Lots of UI work
 * 
 * Revision 1.4 2002/06/21 21:52:50 admin Many changes to event handling, file
 * handling
 * 
 * Revision 1.3 2002/06/19 01:57:04 admin Much UI work done
 * 
 * Revision 1.2 2002/06/16 17:53:10 admin Working with files
 * 
 * Revision 1.1 2002/06/15 14:19:51 admin Initial Checkin of question and survey
 * General file system work
 * 
 */


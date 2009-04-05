package org.egonet.io;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Shared.QuestionType;
import com.lowagie.text.*;
import com.lowagie.text.List;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.*;
import java.util.*;

import org.egonet.util.listbuilder.Selection;

public class PDFWriter {

	private boolean includeResponses;
	private Study study;
	private Interview interview;
	
	public PDFWriter(Study study, Interview interview) {
		super();
		this.study = study;
		this.interview = interview;
		this.includeResponses = (interview != null);
	}
	
	public PDFWriter(Study study) {
		this(study, null);
	}
	
	public boolean isIncludeResponses() {
		return includeResponses;
	}
	public void setIncludeResponses(boolean includeResponses) {
		this.includeResponses = includeResponses;
	}
	public Study getStudy() {
		return study;
	}
	public void setStudy(Study study) {
		this.study = study;
	}
	public Interview getInterview() {
		return interview;
	}
	public void setInterview(Interview interview) {
		this.interview = interview;
	}
	
	/**
	 * Eventually, this separate method could be used for output settings that the user could choose
	 */
	public void configureDocument(Document document, File outputfile) throws FileNotFoundException, DocumentException {
		document.setPageSize(PageSize.LETTER);
		PdfWriter.getInstance(document, new FileOutputStream(outputfile));
	}

	public void write(File outputFile) throws IOException {
		try {
			Document document = new Document();
			configureDocument(document, outputFile);

			document.open();
			writeStudy(document);
			document.close();
		} catch (DocumentException ex) {
			throw new IOException(ex);
		}
	}

	private void writeStudy(Document document) throws DocumentException {
		// document metadata
		document.addTitle(study.getStudyName());
		document.addCreator("Egonet (http://egonet.sf.net)");

		
		document.add(new Paragraph(study.getStudyName()));
		writeLine(document);
		
		// STUDY_CONFIG("Study", "Study questions"),
	    // EGO("Ego", "Questions About You"),
	    // ALTER_PROMPT("Alter Prompt", "Whom do you know?"),
	    // ALTER("Alter", "<html><p>Questions About <nobr><b>$$1</b></nobr></p></html>"),
	    // ALTER_PAIR("Alter Pair", "<html><p>Questions About <nobr><b>$$1</b></nobr> and <nobr><b>$$2</b></nobr></p></html>")
	    
		for(QuestionType qT : QuestionType.values()) {
			document.add(new Paragraph("Questions of type: " + qT.niceName));
			writeLine(document);
			
			Iterator<Long> it = study.getQuestionIterator(qT);
			while(it.hasNext()) {
				Long id = it.next();
				Question question = study.getQuestion(id);
				writeQuestion(document, question);
			}
			
		}
	}
	
	private void writeLine(Document document) throws DocumentException {
		
		Chunk c = new Chunk();
		for(float i = 0; i < 120; i++)
			c.append(" ");
		
		c.setUnderline(new Color(0x00, 0x00, 0x00),
			0.0f, 0.1f, 0.0f, 0.2f,
			PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
		document.add(c);
		document.add(new Paragraph());
	}
	
	private void writeQuestion(Document document, Question question) throws DocumentException {
		document.add(new Paragraph("Title: " + question.title));
		document.add(new Paragraph("Unique ID: " + question.UniqueId));
		document.add(new Paragraph("Text: " + question.text));
		document.add(new Paragraph());
		
		List selections = new List();
		for(Selection sel : question.getSelections()) {
			selections.add(sel.getString() + " (index="+sel.getIndex()+",value="+sel.getValue()+")");
		}
		document.add(new Paragraph("Response type: " + question.answerType));
		document.add(new Paragraph());
		
		document.add(new Paragraph("Possible selections: " + question.text));
		document.add(selections);
		document.add(new Paragraph());
		
		Answer answer = question.answer;
		
		document.add(new Paragraph("Answered: " + answer.answered));
		if(answer.answered) {
			document.add(new Paragraph("Answer Index: " + answer.getIndex()));
			document.add(new Paragraph("Answer Value: " + answer.getValue()));

			if(answer.getAlters() != null && answer.getAlters().size() > 0) {
				document.add(new Paragraph("Answer Alters: " + answer.getAlters()));
			}
			document.add(new Paragraph("String answer: " + answer.string));
		}
		document.add(new Paragraph());
		
		writeLine(document);
	}
	
}

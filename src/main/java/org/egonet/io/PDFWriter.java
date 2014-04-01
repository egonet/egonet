package org.egonet.io;


import com.lowagie.text.*;
import com.lowagie.text.List;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.*;
import java.util.*;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.model.Interview;
import org.egonet.model.Shared;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.Question;
import org.egonet.model.question.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.egonet.model.question.*;

public class PDFWriter {
	private Study study;
	private Interview interview;
	
	final private static Logger logger = LoggerFactory.getLogger(PDFWriter.class);
	private int indexPromptQuestion = 0;
        
	public PDFWriter(Study study, Interview interview) throws CorruptedInterviewException {
		super();
		this.study = study;
		this.interview = interview;
	}
	
	public PDFWriter(Study study, String name) throws CorruptedInterviewException {
		this(study, new Interview(study, name));
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
		
		for(Class<? extends Question> qT : Shared.questionClasses) {
			document.add(new Paragraph("Questions of type: " + Question.getNiceName(qT)));
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
		if(question instanceof AlterQuestion) {
			
		}
		else if(question instanceof AlterPairQuestion) {
			
		}
		else if(question instanceof AlterPromptQuestion) {
			
			String[] alterList = interview.getAlterQuestionPromptAnswers()[indexPromptQuestion];
						
                        Paragraph p1 = new Paragraph("Title: " + question.title);
                        Font f1 = p1.getFont(); f1.setStyle(Font.UNDERLINE); p1.setFont(f1);
                        document.add(p1);
                        indexPromptQuestion++;
                        
                        // we have a list of alters
			if(alterList.length > 0) {
				for(int i = 0; i < alterList.length ;i++) {
					String entry = "("+(i+1)+") " + alterList[i] + " ";
					while(entry.length() < 25) entry += " "; // pad
					
					Paragraph p = new Paragraph(entry);
					Font f = p.getFont(); f.setStyle(Font.UNDERLINE); p.setFont(f);
					document.add(p);
				}
			}
			else {
				// we don't know the list yet, but the study is 
                                // in limited mode, so we know how many alters 
                                // we will have as maximum.
                                if(!study.isUnlimitedAlterMode())
                                {
                                    for(int i = 0; i < study.getMaximumNumberOfAlters(); i++) {
                                            String entry = "("+(i+1)+") ";
                                            while(entry.length() < 25) entry += " ";

                                            Paragraph p = new Paragraph(entry);
                                            Font f = p.getFont(); f.setStyle(Font.UNDERLINE); p.setFont(f);
                                            document.add(p);
                                    }
                                }   
			}
			
			return;
		}
		else if(question instanceof EgoQuestion) {
			
		}
		
		java.util.List<Answer> answers = interview.getAnswersByUniqueId(question.UniqueId);

		List answeredQuestions = new List();
			
		for(Answer answer : answers) {

			writeLine(document);
			
			document.add(new Paragraph("Title: " + question.title + " (Unique ID: " + question.UniqueId + ", Response type: " + question.answerType + ")"));
			document.add(new Paragraph());
			document.add(new Paragraph());

			if(!answer.isAnswered())
				document.add(new Paragraph("Answered: " + answer.isAnswered()));

			String qText = question.text;
			qText = qText.replaceAll("\\$\\$[^0-9]+", "\\$\\$1");
			qText = qText.replaceAll("\\$\\$$", "\\$\\$1");
			
			
			if(answer.getAlters() != null && answer.getAlters().size() > 0) {
				
				String names = "";
				java.util.List<Integer> alters = answer.getAlters();
				for(int i = 0; i < alters.size() ;i++) {
					int num = alters.get(i);
					
					String thisName = "";
					if(interview.getAlterList().length > num)
						thisName = interview.getAlterList()[num] + " ("+(num+1)+")";
					else
						thisName = "("+(num+1)+") ______________";

					qText = qText.replaceAll("\\$\\$"+(i+1), thisName);
					names += thisName;
					
					if(i < alters.size()-1)
						names += ", ";
				}
				
				logger.info("Answer alters: " + Arrays.asList(names));
				// document.add(new Paragraph("Answer Alters: " + names));
			}
			
			document.add(new Paragraph("Text: " + qText));
			
			if(question.answerType.equals(NumericalAnswer.class)) {
				if(answer.isAnswered() && answer.getValue() != -1)
					document.add(new Paragraph("Answer Value: " + answer.getValue() + ", Answer Index: " + answer.getIndex()));
				else
					document.add(new Paragraph("Answer Value: _________________ "));
			}
			else if(question.answerType.equals(TextAnswer.class)) {
				if(answer.isAnswered() && answer.string != null && !answer.string.equals(""))
					document.add(new Paragraph("String answer: " + answer.string));
				else {
					document.add(new Paragraph("Answer Value: "));
					document.add(new Paragraph(""));
					document.add(new Paragraph(""));
					document.add(new Paragraph(""));
				}
			}
			else if(question.answerType.equals(CategoricalAnswer.class)) {
				if(answer.isAnswered() && answer.getIndex() > -1) {
					Selection sel = question.getSelections().get(answer.getIndex());
					document.add(new Paragraph(sel.getString() + " (Answer Value: " + answer.getValue() + ", Answer Index: " + answer.getIndex() + ")"));
				}
				else {
					List selections = new List();
					for(Selection sel : question.getSelections()) {
						selections.add(sel.getString() + " (index="+sel.getIndex()+",value="+sel.getValue()+")");
					}
					
					document.add(new Paragraph("Possible selections: "));
					document.add(selections);
					document.add(new Paragraph());
				}
			}

			document.add(answeredQuestions);
		}
		document.add(new Paragraph());
		
		writeLine(document);
	}

}

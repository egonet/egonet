package org.egonet.tests.functional;

import java.io.*;

import org.egonet.io.InterviewReader;
import org.egonet.io.PDFWriter;
import org.egonet.io.StudyReader;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

public class CreatePdf {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		StudyReader sr = new StudyReader(new File("C:/Documents and Settings/Martin/My Documents/EgoNet/EgoNet Study/Sample Study.ego"));
		Study study = sr.getStudy();
		
		File fInterview = new File("C:/Documents and Settings/Martin/My Documents/EgoNet/EgoNet Study/Interviews/martin_smith.int");
		InterviewReader ir = new InterviewReader(study, fInterview);
		Interview interview = ir.getInterview();
		
		String path = "C:/Documents and Settings/Martin/Desktop/";
		
		File completeFile = new File(path+"complete.pdf");
		PDFWriter pdfWriter1 = new PDFWriter(study, interview);
		pdfWriter1.write(completeFile);
		
		File incompleteFile = new File(path+"incomplete.pdf");
		PDFWriter pdfWriter2 = new PDFWriter(study, interview.getIntName());
		pdfWriter2.write(incompleteFile);
	}

}

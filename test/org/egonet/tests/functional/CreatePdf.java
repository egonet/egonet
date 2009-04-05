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
		
		InterviewReader ir = new InterviewReader(study, new File("C:/Documents and Settings/Martin/My Documents/EgoNet/EgoNet Study/Interviews/martin_smith.int"));
		Interview interview = ir.getInterview();
		
		File outputFile = new File("C:/Documents and Settings/Martin/Desktop/output.pdf");
		PDFWriter pdfWriter = new PDFWriter(study, interview);
		pdfWriter.write(outputFile);
	}

}

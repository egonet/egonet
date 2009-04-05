package org.egonet.tests.functional;

import java.io.*;

import org.egonet.io.PDFWriter;
import org.egonet.io.StudyReader;

import com.endlessloopsoftware.egonet.Study;

public class CreatePdf {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String studyPath = "C:/Documents and Settings/Martin/My Documents/EgoNet/EgoNet Study/Sample Study.ego";
		File studyFile = new File(studyPath);
		
		StudyReader sr = new StudyReader(studyFile);
		Study study = sr.getStudy();
		
		File outputFile = new File("C:/Documents and Settings/Martin/Desktop/output.pdf");
		PDFWriter pdfWriter = new PDFWriter(study);
		pdfWriter.write(outputFile);
	}

}

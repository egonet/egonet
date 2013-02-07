package org.egonet.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.egonet.exceptions.CorruptedInterviewException;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.rtf.RtfWriter2;

public class RTFWriter extends PDFWriter {

	public RTFWriter(Study study, String name) throws CorruptedInterviewException {
		super(study, name);
	}

	public RTFWriter(Study study, Interview interview) throws CorruptedInterviewException {
		super(study, interview);
	}

	@Override
	public void configureDocument(Document document, File outputfile) throws FileNotFoundException, DocumentException {
		document.setPageSize(PageSize.LETTER);
		RtfWriter2.getInstance(document, new FileOutputStream(outputfile));
	}
}

package com.endlessloopsoftware.ego.client;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class xmlTest {

	public static void main(String[] args) throws Exception{
//		try {
			// step 1: Create document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// step 2: Create xml tree
			// step 2.1: create root and add to document
			Element root = doc.createElement("root");
			doc.appendChild(root);

			// step 2.2: create comment and put in root
			Comment comment = doc.createComment("Just a thought");
			root.appendChild(comment);

			// step 2.3: create child attribute and add to root
			Element child = doc.createElement("child");
			child.setAttribute("name", "value");
			root.appendChild(child);
			// step 2.4: Write some text
			Text text = doc
					.createTextNode("Sample text");
			child.appendChild(text);

			// step 3: Output to xml
			// step 3.1: set up transformer
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();

			// step 3.2: Create output file
			File file = new File(
					"/home/sonam/Desktop/EgonetStudies/xml/test.xml");

			// step 3.3: Write the DOM document to the file
			Source src = new DOMSource(doc);
			Result dest = new StreamResult(System.out);
			aTransformer.transform(src, dest);

//		} catch (Exception ex) {
//			ex.printStackTrace();
//			System.exit(0);
//		}
	}

	/*
	 * public static void writeXmlFile(Document document, String filename) { }
	 */
}

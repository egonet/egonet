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

			// step 3.3: Write the DOM document to the file
			Source src = new DOMSource(doc);
			Result dest = new StreamResult(System.out);
			aTransformer.transform(src, dest);

//		} catch (Exception ex) {
//			System.exit(0);
//		}
	}
}

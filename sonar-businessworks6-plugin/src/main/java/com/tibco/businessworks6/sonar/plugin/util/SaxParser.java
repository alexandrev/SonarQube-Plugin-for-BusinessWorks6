/*
 * SonarQube XML Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tibco.businessworks6.sonar.plugin.util;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Parse XML files and add linenumbers in the document.
 *
 * @author Kapil Shivarkar
 */
public final class SaxParser extends AbstractParser {


	/**
	 * From http://will.thestranathans.com/post/1026712315/getting-line-numbers-from-xpath-in-java
	 */
	private static final class LocationRecordingHandler extends DefaultHandler {

		private final Document doc;
		private Locator locator;
		private Element current;

		private final Map<String, String> prefixMappings = new HashMap<>();

		// The docs say that parsers are "highly encouraged" to set this
		public LocationRecordingHandler(Document doc) {
			this.doc = doc;
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		// This just takes the location info from the locator and puts
		// it into the provided node
		private void setLocationData(Node n) {
			if (locator != null) {
				n.setUserData(KEY_LINE_NO, locator.getLineNumber(), null);
				n.setUserData(KEY_COLUMN_NO, locator.getColumnNumber(), null);
			}
		}

		// Admittedly, this is largely lifted from other examples
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) {
			Element e = null;
			if (localName != null && !"".equals(localName)) {
				e = doc.createElementNS(uri, localName);
			} else {
				e = doc.createElement(qName);
			}

			// But this part isn't lifted ;)
			setLocationData(e);

			if (current == null) {
				doc.appendChild(e);
			} else {
				current.appendChild(e);
			}
			current = e;

			for (Entry<String, String> entry : prefixMappings.entrySet()) {
				Attr attr = doc.createAttribute("xmlns:" + entry.getKey());
				attr.setValue(entry.getValue());
				current.setAttributeNodeNS(attr);
			}
			prefixMappings.clear();

			// For each attribute, make a new attribute in the DOM, append it
			// to the current element, and set the column and line numbers.
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					final Attr attr;
					if (attrs.getLocalName(i) != null && !"".equals(attrs.getLocalName(i))) {
						attr = doc.createAttributeNS(attrs.getURI(i), attrs.getLocalName(i));
						current.setAttributeNodeNS(attr);
					} else {
						attr = doc.createAttribute(attrs.getQName(i));
						current.setAttributeNode(attr);
					}
					attr.setValue(attrs.getValue(i));
					setLocationData(attr);
				}
			}
		}
	}

	private static final String KEY_LINE_NO = "saxParser.lineNumber";

	private static final String KEY_COLUMN_NO = "saxParser.columnNumber";

	/**
	 * Gets the LineNumber of a node.
	 */
	public static int getLineNumber(Node node) {
		Integer lineNumber = (Integer) node.getUserData("lineNumber");
		return lineNumber == null ? 0 : lineNumber;
	}

	/* private void parse(InputStream input, DefaultHandler handler, boolean namespaceAware) throws IOException, SAXException {
    SAXParser parser = newSaxParser(namespaceAware);
    // read comments too, so use lexical handler.
    parser.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    parser.parse(input, handler);
  }*/

	private void parse(File input, DefaultHandler handler) throws IOException, SAXException {
		SAXParser parser = newSaxParser(false);
		// read comments too, so use lexical handler.
		parser.parse(input, handler);
	}

	
	/*public Document parseDocument(InputStream input, boolean namespaceAware) {
    try {
     // Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      //LocationRecordingHandler handler = new LocationRecordingHandler(document);
      //parse(input, handler, namespaceAware);
    	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    	Document document = builder.parse(input);
    	LocationRecordingHandler handler = new LocationRecordingHandler(document);
        parse(input, handler, namespaceAware);
      return document;
    } catch (Exception e) {
      LOG.warn("Unable to analyse input stream");
      LOG.warn("Cause: {}", e.toString());
      return null;
    }
  }*/

	public Document parseDocument(File input, boolean namespaceAware) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(input);
			LocationRecordingHandler handler = new LocationRecordingHandler(document);
			parse(input, handler);
			return document;
		} catch (Exception e) {
			return null;
		}
	}
	
}

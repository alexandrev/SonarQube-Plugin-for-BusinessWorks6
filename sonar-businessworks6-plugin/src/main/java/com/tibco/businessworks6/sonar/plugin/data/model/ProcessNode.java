package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public abstract class ProcessNode {

        Document document;
	protected String type;
	protected String name;
	protected Node node;

        
        public int getLine(){
            return XmlHelper.getLineNumber(node);
        }
        

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

    /**
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;
    }

	
	
}

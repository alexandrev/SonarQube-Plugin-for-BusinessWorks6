package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import java.io.File;
import org.w3c.dom.Document;

import org.apache.log4j.Logger;
import org.sonar.api.batch.fs.InputFile;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Checks and analyzes report measurements, violations and other findings in
 * WebSourceCode.
 *
 * @author Developer
 */
public class BwXmlResource  {

    private String code;

    private Document document;
        
    private InputFile resource;
    
    private File file;
    
    private static final Logger LOG = Logger.getLogger(BwXmlResource.class);
    

    public BwXmlResource(InputFile file) {
        this.resource = file;
        this.file = file.file();
        code = "";
        document = XmlHelper.getDocument(file.file());
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return getResource().absolutePath();
    }

    /**
     * Metodo que extrae un valor a partir de un arbol DOM.
     *
     * @param el root del arbol.
     * @param namespace namespace del elemetno.
     * @param tagName nombre de la etiqueta.
     * @return valor de la etiqueta.
     */
    public String extractValue(Element el, String namespace, String tagName) {
        String out = "";
        if (el != null && XmlHelper.isFilled(namespace) && XmlHelper.isFilled(tagName)) {
            NodeList list = el.getElementsByTagNameNS(namespace, tagName);
            if (list != null && list.getLength() > 0) {
                Node item = list.item(0);
                if (item != null && item.getFirstChild() != null && item.getFirstChild().getNodeValue() != null) {
                    out = item.getFirstChild().getNodeValue();
                }
            }
        }
        return out;
    }
    
    public Node extractNode(Element el, String namespace, String tagName) {
       Node out = null;
        if (el != null && XmlHelper.isFilled(namespace) && XmlHelper.isFilled(tagName)) {
            NodeList list = el.getElementsByTagName(tagName);
            if (list != null && list.getLength() > 0) {
                Node item = list.item(0);
                if (item != null){
                    out = item;
                }
                
            }
        }
        return out;
    }

    /**
     * Metodo que extrae un valor a partir de un arbol DOM.
     *
     * @param el root del arbol.
     * @param tagName nombre de la etiqueta.
     * @return valor de la etiqueta.
     */
    public String extractValueNoNS(Element el, String tagName) {
        String out = "";
        if (el != null && XmlHelper.isFilled(tagName)) {
            NodeList list = el.getElementsByTagName(tagName);
            if (list != null && list.getLength() > 0) {
                Node item = list.item(0);
                if (item != null && item.getFirstChild() != null && item.getFirstChild().getNodeValue() != null) {
                    out = item.getFirstChild().getNodeValue();
                }

            }
        }
        return out;
    }

    public String extractValueAttr(Element element, String name) {
        return element.getAttribute(name);
    }

    public boolean hasElementsChild(Node item) {
        boolean out = false;
        if (item != null) {
            NodeList tmpList = item.getChildNodes();
            if (tmpList != null) {
                for (int i = 0; i < tmpList.getLength(); i++) {
                    if (tmpList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        out = true;
                    }
                }
            }
        }
        return out;
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

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the resource
     */
    public InputFile getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(InputFile resource) {
        this.resource = resource;
    }

}

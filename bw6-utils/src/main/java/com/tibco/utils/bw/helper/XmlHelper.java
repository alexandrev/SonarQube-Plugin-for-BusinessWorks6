package com.tibco.utils.bw.helper;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlHelper {
    
    

    public static Element firstChildElement(Element element,
            String childNameSpace, String childName)
            throws NoSuchElementException {
        NodeList childCandidateList;
        if (childNameSpace == null || childNameSpace.isEmpty()) {
            childCandidateList = element.getElementsByTagName(childName);
        } else {
            /*childCandidateList = element.getElementsByTagNameNS(childNameSpace,
					childName);*/
            childCandidateList = element.getElementsByTagNameNS(childNameSpace, childName);
        }
        if (childCandidateList.getLength() > 0) {
            Element firstChild = (Element) childCandidateList.item(0);
            return firstChild;
        } else {
            throw new NoSuchElementException(
                    "No child candidate found in this element");
        }
    }

    public static Element firstChildElement(Element element, String childName)
            throws NoSuchElementException {
        return firstChildElement(element, null, childName);
    }

    public static Node evaluateXpathNode(Node rootNode,
            String xPathQuery) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.compile(xPathQuery).evaluate(rootNode, XPathConstants.NODE);
    }

    public static boolean existsXPath(Element xmlDocument, String config) {
        NodeList result = evaluateXPath(xmlDocument, config);
        return result != null && result.getLength() > 0;
    }

    public static NodeList evaluateXPath(Document document, String instanceInfoPropertynameversion) {
        return evaluateXPath(document.getDocumentElement(), instanceInfoPropertynameversion);
    }

    private XmlHelper() {

    }

    public static String extractValue(Element el, String namespace, String tagName) {
        String out = "";
        if (el != null && isFilled(namespace) && isFilled(tagName)) {
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

    public static String extractValueNoNS(Element el, String tagName) {
        String out = "";
        if (el != null && isFilled(tagName)) {
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

    public static String getXPath(Node node) {
        Node parent = node.getParentNode();
        if (parent == null) {
            return "";
        }

        String nodeName = node.getNodeName();
        if (nodeName != null && !nodeName.isEmpty() && nodeName.indexOf(':') != -1) {
            nodeName = nodeName.split(":")[1];
        }

        return getXPath(parent) + "/" + nodeName;
    }

    public static String extractValueAttr(Element element, String name) {
        return element.getAttribute(name);
    }

    public static Element evalueXPathSingleElement(Element document, String expression) {
        NodeList node = evaluateXPath(document, expression);
        if (node != null && node.getLength() > 0) {
            return (Element) node.item(0);
        }
        return null;
    }

    public static NodeList evaluateXPath(Element document, String expression) {
        NodeList out = null;
        if (document != null) {
            try {
                XPathExpression tmp = getXPathExpressionForDocument(expression);
                if (tmp != null) {
                    out = (NodeList) tmp.evaluate(document, XPathConstants.NODESET);
                }
            } catch (XPathExpressionException ex) {
               
            }
        }
        return out;
    }

    private static XPathExpression getXPathExpressionForDocument(String expression) {
        if (expression != null) {
            try {
                XPath xpath = XPathFactory.newInstance().newXPath();
                if (xpath != null) {
                    return xpath.compile(expression);
                }
            } catch (XPathExpressionException ex) {
               
            }
        }
        return null;
    }

    public static Document getDocument(File file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            return doc;

        } catch (IOException ex) {
        
        } catch (ParserConfigurationException ex) {

        } catch (SAXException ex) {
 
        }
        return null;
    }

    public static int getLineNumber(Node node) {
        if (node != null) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName = attributes.item(i).getNodeName();
                if (attrName != null && "lineNumber".equals(attrName)) {
                    return Integer.parseInt(attributes.item(i).getTextContent());
                }
            }
        }
        return 1;
    }

    public static NodeList evaluateXPath(Node node, String expression) {
        NodeList out = null;
        try {
            out = (NodeList) getXPathExpressionForDocument(expression).evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
         
        }
        return out;
    }

    public static String getNodeNameNoNS(Node node) {
        String out = node.getLocalName();
        if (out == null || out.length() == 0) {
            out = node.getNodeName();
        }
        return out;
    }
    
     public static boolean isFilled(String str){
        return str != null && !"".equals(str);
    }
}

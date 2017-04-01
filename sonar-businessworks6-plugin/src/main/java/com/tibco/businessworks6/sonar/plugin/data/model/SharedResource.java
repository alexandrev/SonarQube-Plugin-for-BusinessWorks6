package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPath;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.sonar.api.batch.fs.InputFile;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class SharedResource  {

    private String type;

    private String name;

    private List<ResourceParameter> parameters;
    
    protected XPath xPath;
    
    private BwProject project;
    
    private InputFile resource;

    private static final Logger LOG = Logger.getLogger(SharedResource.class);

    public SharedResource() {
        parameters = new ArrayList<>();
    }

    public boolean isParameterRequired(String parameterName) {
        boolean out = false;
        if (parameterName != null) {
            ResourceParameter parameter = getParameterByName(parameterName);
            if (parameter != null) {
                return parameter.isRequired();
            }
        }
        return out;
    }

    public boolean isParameterGlobalVariableScoped(String parameterName) {
        boolean out = false;
        if (parameterName != null) {
            ResourceParameter parameter = getParameterByName(parameterName);
            if (parameter != null) {
                return parameter.isGlobalVariable();
            }
        }
        return out;
    }

    public void parse(File file) {
        Document xmlDocument = XmlHelper.getDocument(file);
        if (xmlDocument != null) {
            parseName(xmlDocument, file);
            parseType(xmlDocument, file);
            parseConfigurationData(xmlDocument, file);
        }
    }

    protected void parseName(Document xmlDocument, File file) throws DOMException {
        Element resourceNode = XmlHelper.evalueXPathSingleElement(xmlDocument.getDocumentElement(), "/namedResource");
        if (resourceNode != null) {
            name = XmlHelper.extractValueAttr(resourceNode, "name");
        }

        if (name == null || name.isEmpty()) {
            name = FilenameUtils.getBaseName(file.getAbsolutePath());
        }
    }

    protected void parseType(Document xmlDocument, File file) throws DOMException {
        Element resourceNode = XmlHelper.evalueXPathSingleElement(xmlDocument.getDocumentElement(), "/namedResource");
        if (resourceNode != null) {
            type = XmlHelper.extractValueAttr(resourceNode, "type");
        }

        if (type == null || type.isEmpty()) {
            type = FilenameUtils.getExtension(file.getAbsolutePath());
        }
    }

    protected void parseConfigurationData(Document xmlDocument, File file) {
        if (xmlDocument != null) {
            Element configuration = XmlHelper.evalueXPathSingleElement(xmlDocument.getDocumentElement(), "/namedResource/configuration");
            if(configuration != null){
                NamedNodeMap attributes = configuration.getAttributes();
                if(attributes != null){
                    for(int i=0;i<attributes.getLength();i++){
                        String attrName = attributes.item(i).getNodeName();
                        String attrValue = attributes.item(i).getNodeValue();
                        addParameter(attrName, attrValue);
                    }
                }
            }
            
            NodeList configurationItemList = XmlHelper.evaluateXPath(xmlDocument.getDocumentElement(), "/namedResource/configuration/substitutionBindings");
            if(configurationItemList != null){
                for(int i=0;i<configurationItemList.getLength();i++){
                    Element el = (Element) configurationItemList.item(i);
                    if(el != null){
                        
                        String template = XmlHelper.extractValueAttr(el, "template");
                        String propName = XmlHelper.extractValueAttr(el, "propName");
                        addParameterWithProperty(template, propName);
                    }
                }
            }
        }
    }

    public void addParameter(String name, String value) {
        ResourceParameter returnParameter = getParameterByName(name);
        if (returnParameter != null) {
            returnParameter.setValue(value);
        } else {
            returnParameter = new ResourceParameter();
            returnParameter.setName(name);
            returnParameter.setValue(value);
            getParameters().add(returnParameter);
        }

    }

    public ResourceParameter getParameterByName(String name) {
        if (name != null) {
            for (ResourceParameter parameter : getParameters()) {
                if (name.equals(parameter.getName())) {
                    return parameter;
                }
            }
        }
        return null;
    }

    public List<ResourceParameter> getParameters() {
        return parameters;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<ResourceParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Name: ").append(name).append(";Type: ").append(type).append(";");
        for (ResourceParameter parameter : parameters) {
            out.append("{").append(parameter.getName()).append(",").append(parameter.getValue()).append(",").append(parameter.isRequired()).append(",").append(parameter.isGlobalVariable()).append("}");
        }
        out.append(";");
        return out.toString();
    }

 
    

    public boolean isValid(ResourceParameter parameter) {
        boolean isValid = true;

        if (parameter != null) {
           
            if (parameter.getDependencies() != null) {
                for (Map.Entry<String, String> dependency : parameter.getDependencies().entrySet()) {
                    String dependencyKey = dependency.getKey();
                    if (dependencyKey != null && !dependencyKey.isEmpty()) {
                         ResourceParameter param = getParameterByName(dependencyKey);
                        if (param != null) {
                            if (param.getValue() != null) {
                                String patternValue = dependency.getValue();
                                if (patternValue != null) {
                                    isValid = isValid && param.getValue().matches(patternValue);
                                }
                            }
                        }
                    }
                }

            }
        }
        return isValid;
    }

    private void addParameterWithProperty(String template, String name) {
         ResourceParameter returnParameter = getParameterByName(template);
        if (returnParameter != null) {
            returnParameter.setValue(name);
        } else {
            returnParameter = new ResourceParameter();
            returnParameter.setName(template);
            returnParameter.setValue(name);
            getParameters().add(returnParameter);
        }
        returnParameter.setGlobalVariable(true);
    }

    public String getFullName() {
        return getName();
    }

    void setProject(BwProject aThis) {
        this.project = aThis;
    }

    public void setResource(InputFile resource) {
        this.resource = resource;
    }

    /**
     * @return the resource
     */
    public InputFile getResource() {
        return resource;
    }

    /**
     * @return the project
     */
    public BwProject getProject() {
        return project;
    }

    public void setProperties(SharedResourceProperties configuration) {
        if (configuration != null) {
            for (SharedResourceProperties.SharedResource sharedResource : configuration.getSharedResourceList()) {
                LOG.debug("Resource:" + this.name);
                if (type != null && type.equals(sharedResource.getType())) {
                    for (SharedResourceProperties.SharedResource.Property property : sharedResource.getPropertyList()) {
                        LOG.debug("Adding property: " + property.getName() + ", mandatory=" + property.getRequired() + ", Binding=" + property.getBinding());
                        ResourceParameter parameter = getParameterByName(property.getName());
                        if (parameter == null) {
                            addParameter(property.getName(), "");
                            parameter = getParameterByName(property.getName());
                        }
                        if(parameter != null){
                            parameter.setBinding(property.getBinding());
                            parameter.setRequired(property.getRequired());

                            if (property.getDependencyList() != null) {
                                for (SharedResourceProperties.SharedResource.Property.Dependency dependency : property.getDependencyList()) {
                                    parameter.addDependency(dependency.getField(), dependency.getValue());
                                }
                            }
                        }
                    }
                }
            }

        }
                System.err.println("Data: "+toString());
    }
}

package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import org.apache.commons.lang.WordUtils;
import org.w3c.dom.Node;


public class BwTransition {

    private String type;

    private String description;

    private String xpath;

    private BwActivity originalActivity;

    private BwActivity finalActivity;

    private Node node;
    
    private String name;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getLineNumber() {
        return XmlHelper.getLineNumber(node);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the originalActivity
     */
    public BwActivity getOriginalActivity() {
        return originalActivity;
    }

    /**
     * @param originalActivity the originalActivity to set
     */
    public void setOriginalActivity(BwActivity originalActivity) {
        this.originalActivity = originalActivity;
    }

    /**
     * @return the finalActivity
     */
    public BwActivity getFinalActivity() {
        return finalActivity;
    }

    /**
     * @param finalActivity the finalActivity to set
     */
    public void setFinalActivity(BwActivity finalActivity) {
        this.finalActivity = finalActivity;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

   
    public String getText() {
        if ("SUCCESSWITHCONDITION".equals(type)) {
            if ((description != null) && (!description.isEmpty())) {
                return description;
            } else {
                return xpath;
            }
        } else {
            return WordUtils.capitalize(type);
        }
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

    public void compute() {
        if(finalActivity != null && originalActivity != null){
            if(!finalActivity.getPreviousActivities().contains(originalActivity)){
                finalActivity.addPreviousActivity(originalActivity);
            }
            if(!originalActivity.getPostActivities().contains(finalActivity)){
                originalActivity.addPostActivity(finalActivity);
            }
        }
        
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

}

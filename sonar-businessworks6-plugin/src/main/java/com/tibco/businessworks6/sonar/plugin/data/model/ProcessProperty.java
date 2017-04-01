/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibco.businessworks6.sonar.plugin.data.model;

/**
 *
 * @author alexandrev
 */
public class ProcessProperty {
    
    private String name;
    
    private boolean isLiteral;
    
    private String value;
    
    private String type;

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
     * @return the isLiteral
     */
    public boolean isIsLiteral() {
        return isLiteral;
    }

    /**
     * @param isLiteral the isLiteral to set
     */
    public void setIsLiteral(boolean isLiteral) {
        this.isLiteral = isLiteral;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
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
    
    
          
}

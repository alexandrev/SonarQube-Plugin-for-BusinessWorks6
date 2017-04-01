/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibco.businessworks6.sonar.plugin.data.model;

/**
 *
 * @author alexa
 */
public class BwSharedVariable {
    
    private String name;
    
    private String type;
    
    private String initialValue;

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

    /**
     * @return the initialValue
     */
    public String getInitialValue() {
        return initialValue;
    }

    /**
     * @param initialValue the initialValue to set
     */
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * @return the scope
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public enum Scope {JOB,MODULE};
    
    private Scope scope;
    
    
    
    
    
    
}


package com.tibco.businessworks6.sonar.plugin.data.model;

import java.util.HashMap;
import java.util.Map;

public class ResourceParameter {
    
    private String name;
    
    private String value;
    
    private boolean required;
    
    private boolean globalVariable;
    
    private boolean binding;
    
    private Map<String,String> dependencies;

    
    public ResourceParameter(){
        dependencies = new HashMap<String, String>();
        required = false;
        globalVariable= false;
    }
    
    
    public void addDependency(String name, String value){
        getDependencies().put(name, value);
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
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return the globalVariable
     */
    public boolean isGlobalVariable() {
        return globalVariable;
    }

    /**
     * @param globalVariable the globalVariable to set
     */
    public void setGlobalVariable(boolean globalVariable) {
        this.globalVariable = globalVariable;
    }


    /**
     * @return the dependencies
     */
    public Map<String,String> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(Map<String,String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * @return the binding
     */
    public boolean isBinding() {
        return binding;
    }

    /**
     * @param binding the binding to set
     */
    public void setBinding(boolean binding) {
        this.binding = binding;
    }
    
    
    
    
    
}

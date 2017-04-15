
package com.tibco.businessworks6.sonar.plugin.data.model;



public class BwConfigurationParameter {

    private String name;
    
    private String value;

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

    public boolean isUsingGlobalVariable() {
        return value != null &&  value.contains("%%");
    }
    
    
}

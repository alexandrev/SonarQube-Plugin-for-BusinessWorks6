package com.tibco.businessworks6.sonar.plugin.data.model;

import java.io.File;


public class BwVariable {
    
    private String fullPath;
    
    private String name;
    
    private String value;
    
    private boolean internal;
    
    private boolean property;
    
    private boolean source;
    
    private org.sonar.api.resources.File resource;

    
    

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
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
    
    
     public void setFullPath(File fTmp){
        if(fTmp != null){
            String tmpFullPath = fTmp.getAbsolutePath();
            int indexOf = tmpFullPath.indexOf("defaultVars");
            if(indexOf > 0){
                this.setFullPath(tmpFullPath.substring(indexOf+12));

                if(this.getFullPath().contains("\\")){ 
                    // Windows
                    this.setFullPath(this.getFullPath().replace('\\', '/'));
                }
                
                indexOf = this.getFullPath().lastIndexOf("/");
                
                if(indexOf > 0){
                    this.setFullPath(this.getFullPath().substring(0, indexOf+1) + this.getName());
                } else {
                    this.setFullPath(this.getName());
                }
                 
            }
            
        }
    }

    public String getFullName(){
        return "%%"+this.getFullPath()+"%%";
    }
    
    public String getFullMappingName() {
        // See if ns[0-9]* should be changed by [\i-[:]][\c-[:]]* (NCName)
        //
        return "\\$_globalVariables/ns[0-9]*:GlobalVariables/"+this.getFullPath();
    }

    public void setResource(org.sonar.api.resources.File resource) {
        this.resource = resource;
    }

    /**
     * @return the resource
     */
    public org.sonar.api.resources.File getResource() {
        return resource;
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
     * @return the internal
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * @param internal the internal to set
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    /**
     * @return the property
     */
    public boolean isProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(boolean property) {
        this.property = property;
    }

    /**
     * @return the source
     */
    public boolean isSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(boolean source) {
        this.source = source;
    }
    
    
    
    
}

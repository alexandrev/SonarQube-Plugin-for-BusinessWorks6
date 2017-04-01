package com.tibco.businessworks6.sonar.plugin.data.model;

import java.io.File;
import java.util.Date;


public class BwModuleProperty {
    
   
    private String name;
    
    private String value;
    
    private boolean deploymentSettable;
    
    private boolean serviceSettable;
    
    private boolean override;
    
    private String type;
    
    private Date modTime;
    
    private File resource;


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
     * @return the deploymentSettable
     */
    public boolean isDeploymentSettable() {
        return deploymentSettable;
    }

    /**
     * @param deploymentSettable the deploymentSettable to set
     */
    public void setDeploymentSettable(boolean deploymentSettable) {
        this.deploymentSettable = deploymentSettable;
    }

    /**
     * @return the serviceSettable
     */
    public boolean isServiceSettable() {
        return serviceSettable;
    }

    /**
     * @param serviceSettable the serviceSettable to set
     */
    public void setServiceSettable(boolean serviceSettable) {
        this.serviceSettable = serviceSettable;
    }

    /**
     * @return the override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * @param override the override to set
     */
    public void setOverride(boolean override) {
        this.override = override;
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
     * @return the modTime
     */
    public Date getModTime() {
        return modTime;
    }

    /**
     * @param modTime the modTime to set
     */
    public void setModTime(Date modTime) {
        this.modTime = modTime;
    }

    /**
     * @return the resource
     */
    public File getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(File resource) {
        this.resource = resource;
    }



    
   
    
    
    
    
}

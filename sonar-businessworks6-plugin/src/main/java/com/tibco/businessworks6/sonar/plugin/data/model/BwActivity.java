package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.w3c.dom.Node;

public class BwActivity extends ProcessNode {

    private List<BwActivity> previousActivities;

    private List<BwActivity> postActivities;

    private List<BwConfigurationParameter> config;
    
    private List<BwConfigurationParameter> binding;

    private Collection<BwTransition> transitions;

    public BwActivity() {
        previousActivities = new ArrayList<>();
        postActivities = new ArrayList<>();
        config = new ArrayList<>();
        binding = new ArrayList<>();
        transitions = new ArrayList<>();
    }

 
    public int getLineNumber() {
        return XmlHelper.getLineNumber(getNode());
    }

    public void addPreviousActivity(BwActivity activity) {
        if (!previousActivities.contains(activity)) {
            getPreviousActivities().add(activity);
        }
    }

    public void addPostActivity(BwActivity activity) {
        if (!postActivities.contains(activity)) {
            getPostActivities().add(activity);
        }
    }

    public int getPreviousLinks() {
        return getPreviousActivities().size();
    }

    public int getPostLinks() {
        return getPostActivities().size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isGroup() {
        return false;
    }

    public String getId() {
        return name.trim();
    }

    public void addConfigData(String name, String value) {
        BwConfigurationParameter param = getConfigurationParameter(name);
        if (param == null) {
            param = new BwConfigurationParameter();
            param.setName(name);
            getConfig().add(param);
        }
        param.setValue(value);

    }
    
    public void addBindingData(String name, String value) {
        BwConfigurationParameter param = getBindingParameter(name);
        if (param == null) {
            param = new BwConfigurationParameter();
            param.setName(name);
            getConfig().add(param);
        }
        param.setValue(value);

    }

    public String getConfigurationAsString() {
        String out = "";
        for (BwConfigurationParameter param : getConfig()) {
            out += param.getName() + " : " + param.getValue() + "\r\n";
        }
        return out;
    }

    private BwConfigurationParameter getConfigurationParameter(String name) {
        if (getConfig() != null) {
            for (BwConfigurationParameter param : getConfig()) {
                if (param.getName().equals(name)) {
                    return param;
                }
            }
        }
        return null;
    }
    
    private BwConfigurationParameter getBindingParameter(String name) {
        if (getBinding() != null) {
            for (BwConfigurationParameter param : getBinding()) {
                if (param.getName().equals(name)) {
                    return param;
                }
            }
        }
        return null;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return the previousActivities
     */
    public List<BwActivity> getPreviousActivities() {
        return previousActivities;
    }

    /**
     * @param previousActivities the previousActivities to set
     */
    public void setPreviousActivities(List<BwActivity> previousActivities) {
        this.previousActivities = previousActivities;
    }

    /**
     * @return the postActivities
     */
    public List<BwActivity> getPostActivities() {
        return postActivities;
    }

    /**
     * @param postActivities the postActivities to set
     */
    public void setPostActivities(List<BwActivity> postActivities) {
        this.postActivities = postActivities;
    }

    /**
     * @return the config
     */
    public List<BwConfigurationParameter> getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(List<BwConfigurationParameter> config) {
        this.config = config;
    }

    /**
     * @return the transitions
     */
    public Collection<BwTransition> getTransitions() {
        return transitions;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(Collection<BwTransition> transitions) {
        this.transitions = transitions;
    }

    public String getConfig(String parameterName) {
        if (parameterName != null) {
            for (BwConfigurationParameter parameter : getConfig()) {
                if (parameter.getName().equals(parameterName)) {
                    return parameter.getValue();
                }
            }
        }
        return null;
    }
    
    public String getBinding(String parameterName) {
        if (parameterName != null) {
            for (BwConfigurationParameter parameter : getBinding()) {
                if (parameter.getName().equals(parameterName)) {
                    return parameter.getValue();
                }
            }
        }
        return null;
    }

    /*public boolean isEnd() {
     BwHelper helper = BwHelper.getInstance();
     return helper.isEnd(type);
     }

     public boolean isStart() {
     BwHelper helper = BwHelper.getInstance();
     return helper.isStarter(type);
     }*/

    /**
     * @return the binding
     */
    public List<BwConfigurationParameter> getBinding() {
        return binding;
    }

    /**
     * @param binding the binding to set
     */
    public void setBinding(List<BwConfigurationParameter> binding) {
        this.binding = binding;
    }
}

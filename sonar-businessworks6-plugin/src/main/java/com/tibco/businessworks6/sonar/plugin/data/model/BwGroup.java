package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.BwpParser;
import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BwGroup extends BwActivity implements BwFlow {

    private static final Logger LOG = Logger.getLogger(BwGroup.class);

    private Collection<BwActivity> activityList;

    private Collection<BwVariable> variables;

    private Collection<BwTransition> transitionList;

    protected Collection<BwGroup> groups;
    
    private BwProcess process;


    public int countAllSubGroups() {
        int result = 0;
        result += getGroups().size();
        for (BwGroup g : getGroups()) {
            result += g.countAllSubGroups();
        }
        return result;
    }

    	public Collection<BwGroup> getGroups() {
		return groups;
	}

	public void setGroups(Collection<BwGroup> groups) {
		this.groups = groups;
	}
    
    public int countAllSubActivities() {
        int result = 0;
        result += getActivityList().size();
        for (BwGroup g : getGroups()) {
            result += g.countAllSubActivities();
        }
        return result;
    }

    public int countAllSubTransitions() {
        int result = 0;
        result += getTransitions().size();
        for (BwGroup g : getGroups()) {
            result += g.countAllSubTransitions();
        }
        return result;
    }

    public BwGroup(BwProcess p) {
        process = p;
        groups = new ArrayList<>();
        activityList = new ArrayList<>();
        transitionList = new ArrayList<>();
        variables = new ArrayList<>();
    }

    public void parseFlow(Element documentElement) {
        activityList = new ArrayList<>();
        transitionList = new ArrayList<>();
        variables = new ArrayList<>();
        if (documentElement != null) {
            NodeList scopeList = XmlHelper.evaluateXPath(documentElement, "*[local-name()!='targets' and local-name() != 'sources' and local-name() != 'variables'][1]/bpws:scope");
            if (scopeList != null && scopeList.getLength() > 0) {
                Element rootFlow = (Element) scopeList.item(0);
                BwpParser.calculateBasicActivity(this, rootFlow);
                BwpParser.calculateExtendedActivities(process,this, rootFlow);
                BwpParser.calculateVariables(this, rootFlow);
                LOG.debug("Parsing transitions..");
                BwpParser.parseTransitions(this, rootFlow);
                LOG.debug("Parsed transitions..");
            }
        }
    }

    public BwGroup(BwActivity activity) {
        this.groups = new ArrayList<>();
        setName(activity.getName());
        setNode(activity.getNode());
        setConfig(activity.getConfig());
        setType(activity.getType());
        setTransitionList(activity.getTransitions());
        setPreviousActivities(activity.getPreviousActivities());
        setPostActivities(activity.getPostActivities());
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    public BwActivity getActivityFromName(String name) {
        if (name != null) {
            for (BwActivity tmp : getActivityList()) {
                if (name.equalsIgnoreCase(tmp.getName())) {
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * @return the activityList
     */
    @Override
    public Collection<BwActivity> getActivityList() {
        return activityList;
    }

    /**
     * @param activityList the activityList to set
     */
    public void setActivityList(Collection<BwActivity> activityList) {
        this.activityList = activityList;
    }

    /**
     * @return the transitionList
     */
    public Collection<BwTransition> getTransitionList() {
        return transitionList;
    }

    /**
     * @param transitionList the transitionList to set
     */
    public void setTransitionList(Collection<BwTransition> transitionList) {
        this.transitionList = transitionList;
    }

    /**
     * @return the variables
     */
    @Override
    public Collection<BwVariable> getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(Collection<BwVariable> variables) {
        this.variables = variables;
    }

    public Collection<BwActivity> getFullActivityList() {
        List<BwActivity> actList = new ArrayList<BwActivity>();
        for (BwActivity act : activityList) {
            actList.add(act);
            if (act instanceof BwGroup) {
                actList.addAll(((BwGroup) act).getFullActivityList());
            }
        }
        return actList;

    }

}

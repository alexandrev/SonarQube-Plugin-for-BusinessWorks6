package com.tibco.businessworks6.sonar.plugin.data.model;

public class EventSource extends BwActivity {

    public EventSource() {
        super();
        this.type = "start";
        this.name = "Start";
    }

    public EventSource(BwActivity activity) {
        setConfig(activity.getConfig());
        setDocument(activity.getDocument());
        setName(activity.getName());
        setNode(activity.getNode());
        setPostActivities(activity.getPostActivities());
        setPreviousActivities(activity.getPreviousActivities());
        setTransitions(activity.getTransitions());
        setType(activity.getType());
    }

}

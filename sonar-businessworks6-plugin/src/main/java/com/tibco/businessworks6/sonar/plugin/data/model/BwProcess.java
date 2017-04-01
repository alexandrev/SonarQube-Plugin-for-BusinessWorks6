package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.BwpParser;
import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sonar.api.batch.fs.InputFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BwProcess extends ProcessNode implements Loggable, BwFlow {

    protected boolean isSubProcess;
    protected List<EventSource> eventSources = new ArrayList<>();
    protected List<BwGroup> groups = new ArrayList<>();
    protected Map<String, BwService> services = new HashMap<>();
    protected Map<String, BwService> processReferenceServices = new HashMap<>();
    protected Map<String, String> synonymsGroupMapping = new HashMap<>();
    private List<BwProcessProperty> processProperty = new ArrayList<>();

    protected Document processXmlDocument;
    private NamedNodeMap namedNodeMap;
    protected BwProject project;
    private ProcessSource source;
    private InputFile resource;

    private static final Logger LOG = Logger.getLogger(BwProcess.class);

    protected boolean stateless;

    protected boolean singleton;

    protected boolean scalable;

    protected String productVersion;

    protected String description;

    protected String processName;

    protected Collection<BwActivity> activityList;

    protected Collection<BwVariable> variables;

    protected Collection<BwTransition> transitionList;

    private File file;

    public BwProcess(File fTmp) {
        super();
        file = fTmp;
        activityList = new ArrayList<>();
        transitionList = new ArrayList<>();
        variables = new ArrayList<>();

        init();
    }

    /**
     * @return the processName
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * @param processName the processName to set
     */
    public void setProcessName(String processName) {
        this.processName = processName;
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
    @Override
    public Collection<BwTransition> getTransitionList() {
        return transitionList;
    }

    /**
     * @param transitionList the transitionList to set
     */
    public void setTransitionList(Collection<BwTransition> transitionList) {
        this.transitionList = transitionList;
    }

    public List<BwActivity> getFullActivityList() {
        List<BwActivity> actList = new ArrayList<>();
        for (BwActivity act : activityList) {
            actList.add(act);
            if (act instanceof BwGroup) {
                actList.addAll(((BwGroup) act).getFullActivityList());
            }
        }
        return actList;
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

    private void init() {
        LOG.debug("Initializing the BwProcess");
        document = XmlHelper.getDocument(file);
        Element process = XmlHelper.evalueXPathSingleElement(getDocument().getDocumentElement(), "//process/scope");
        if (process != null) {
              
            parseFlow(process);
        }
        LOG.debug("Initialized the BwProcess");
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

    public void parseFlow(Element documentElement) {
        
        BwpParser.processProcessData(this, getDocument().getDocumentElement());
        BwpParser.calculateVariables(this, documentElement);
        BwpParser.calculateBasicActivity(this, documentElement);
        BwpParser.calculateExtendedActivities(this, this, documentElement);
        LOG.debug("Parsing transitions..");
        BwpParser.parseTransitions(this, documentElement);
        LOG.debug("Parsed transitions..");
        //BwpParser.parseProcessStarterActivity(this, documentElement);
        
    }

    /**
     * @return the stateless
     */
    public boolean isStateless() {
        return stateless;
    }

    /**
     * @param stateless the stateless to set
     */
    public void setStateless(boolean stateless) {
        this.stateless = stateless;
    }

    /**
     * @return the singleton
     */
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * @param singleton the singleton to set
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * @return the scalable
     */
    public boolean isScalable() {
        return scalable;
    }

    /**
     * @param scalable the scalable to set
     */
    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
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

    public Collection<BwActivity> getActivityFromType(String type) {
        Collection<BwActivity> out = new ArrayList<>();
        if (type != null) {
            for (BwActivity tmp : getActivityList()) {
                if (type.equalsIgnoreCase(tmp.getName())) {
                    out.add(tmp);
                }
            }
        }
        return out;
    }

    public Collection<BwGroup> getGroupList() {
        Collection<BwGroup> out = new ArrayList<>();
        for (BwActivity act : activityList) {
            if (act instanceof BwGroup) {
                out.add((BwGroup) act);
            }
        }
        return out;
    }

    public Collection<BwFlow> getFlows() {
        Collection<BwFlow> out = new ArrayList<>();
        out.addAll(getGroupList());
        out.add(this);
        return out;
    }

    public String getFullName() {
        String out = "";
        String tmp = getName();
        if (tmp != null) {
            int idx = tmp.lastIndexOf("/");
            if (idx > 0) {
                String str = tmp.substring(10, idx + 1);
                String finalName = str.replaceAll("/", ".");
                out = finalName + getProcessName();
            }

        }
        return out;
    }

    public Map<String, String> getSynonymsGroupMapping() {
        return synonymsGroupMapping;
    }



    public boolean isSubProcess() {
        return isSubProcess;
    }

    public void setSubProcess(boolean isSubProcess) {
        this.isSubProcess = isSubProcess;
    }

    public Map<String, BwService> getProcessReferenceServices() {
        return processReferenceServices;
    }

    public void setProcessReferenceServices(Map<String, BwService> onlyReferenceServices) {
        this.processReferenceServices = onlyReferenceServices;
    }

    public Map<String, BwService> getServices() {
        return services;
    }

    public void setServices(Map<String, BwService> services) {
        this.services = services;
    }

    public BwActivity getActivityByName(String activityName) {
        for (BwActivity activity : getFullActivityList()) {
            if (activity.getName().equals(activityName)) {
                return activity;
            }
        }
        return null;
    }

    public EventSource getEventSourceByName(String eventsource) {
        for (EventSource evSource : eventSources) {
            if (evSource.getName().equals(eventsource)) {
                return evSource;
            }
        }
        return null;
    }

    public BwGroup getGroupByName(String groupName) {
        for (BwGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    public List<BwGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<BwGroup> groups) {
        this.groups = groups;
    }

    public Document getProcessXmlDocument() {
        return processXmlDocument;
    }

    public BwProcess setProcessXmlDocument(Document processXmlDocument) {
        this.processXmlDocument = processXmlDocument;
        return this;
    }


    public String findActualReferenceServiceName(String referenceService) {
        String partnerLinks = "/process/partnerLinks/partnerLink";

            NodeList children = (NodeList) XmlHelper.evaluateXPath(processXmlDocument, partnerLinks);
            if (children != null) {
                for (int i = 0; i < children.getLength(); i++) {
                    if (children.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(referenceService)) {
                        NodeList nodeList = children.item(i).getChildNodes();
                        for (int j = 0; j < nodeList.getLength(); j++) {
                            if (nodeList.item(j).getNodeName().equals("tibex:ReferenceWire")) {
                                referenceService = nodeList.item(j).getAttributes().getNamedItem("serviceName").getNodeValue();
                                break;
                            }
                        }
                    }
                }
            }

        return referenceService;
    }

    public String findReferenceServiceInline(String referenceService) {
        String partnerLinks = "/process/partnerLinks/partnerLink";
        NodeList children = (NodeList) XmlHelper.evaluateXPath(processXmlDocument, partnerLinks);
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(referenceService)) {
                    NodeList nodeList = children.item(i).getChildNodes();
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (nodeList.item(j).getNodeName().equals("tibex:ReferenceWire")) {
                            referenceService = nodeList.item(j).getAttributes().getNamedItem("inline").getNodeValue();
                            break;
                        }
                    }
                }
            }
        }

        return referenceService;
    }

    public String findActualReferenceServiceProcessName(String referenceService) {
        String partnerLinks = "/process/partnerLinks/partnerLink";
        NodeList children = (NodeList) XmlHelper.evaluateXPath(processXmlDocument, partnerLinks);
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(referenceService)) {
                    NodeList nodeList = children.item(i).getChildNodes();
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (nodeList.item(j).getNodeName().equals("tibex:ReferenceWire")) {
                            referenceService = nodeList.item(j).getAttributes().getNamedItem("processName").getNodeValue();
                            break;
                        }
                    }
                }
            }
        }

        return referenceService;
    }

    public void populateProcessReferencedService(String referencedServiceName, String calledOperation, String namespacePrefix) {
        BwService referencedService = processReferenceServices.get(referencedServiceName);
        if (referencedService == null) {
            String actualReferencedServiceName = findActualReferenceServiceName(referencedServiceName);
            referencedService = new BwService(actualReferencedServiceName);
            referencedService.setNamespace(getNameNodeMap().getNamedItem("xmlns:" + namespacePrefix).getNodeValue());
            referencedService.setImplementationProcess(findActualReferenceServiceProcessName(referencedServiceName));
            referencedService.setInline(findReferenceServiceInline(referencedServiceName));
            HashMap<String, Operation> operations = referencedService.getOperations();
            operations.put(calledOperation, new Operation(calledOperation));
            processReferenceServices.put(actualReferencedServiceName, referencedService);
        } else {
            HashMap<String, Operation> operations = referencedService.getOperations();
            if (operations.get(calledOperation) == null) {
                operations.put(calledOperation, new Operation(calledOperation));
            }
        }
    }

    /*
     public void parseTranstionFromToGroups(Node parent) {
     NodeList children = parent.getChildNodes();
     for (int i = 0; i < children.getLength(); i++) {
     NodeList transitions = children.item(i).getChildNodes();
     if (children.item(i).getNodeName().equals("bpws:targets")) {
     for (int j = 0; j < transitions.getLength(); j++) {
     if (transitions.item(j).getNodeName().equals("bpws:target")) {
     String grouptransition = transitions.item(j).getAttributes().getNamedItem("linkName").getNodeValue();
     String grouptransition2 = synonymsGroupMapping.get(grouptransition);
     if (grouptransition.contains("GroupEnd")) {
     BwTransition transition = transitionMap.get(grouptransition2);
     grouptransition2 = grouptransition2.substring(grouptransition2.indexOf("To") + 2);
     transition.setTo(grouptransition2);
     } else if (grouptransition.contains("GroupStart")) {
     BwTransition transition = transitionMap.get(grouptransition2);
     grouptransition2 = grouptransition2.substring(0, grouptransition2.indexOf("To"));
     transition.setFrom(grouptransition2);
     } else {
     BwTransition transition = transitionMap.get(transitions.item(j).getAttributes().getNamedItem("linkName").getNodeValue());
     transition.setTo(parent.getAttributes().getNamedItem("name").getTextContent());
     }
     }
     }
     } else if (children.item(i).getNodeName().equals("bpws:sources")) {
     for (int j = 0; j < transitions.getLength(); j++) {
     if (transitions.item(j).getNodeName().equals("bpws:source")) {
     String grouptransition = transitions.item(j).getAttributes().getNamedItem("linkName").getNodeValue();
     String grouptransition2 = synonymsGroupMapping.get(grouptransition);
     if (grouptransition.contains("GroupStart")) {
     BwTransition transition = transitionMap.get(grouptransition2);
     grouptransition2 = grouptransition2.substring(0, grouptransition2.indexOf("To"));
     transition.setFrom(grouptransition2);
     } else if (grouptransition.contains("GroupEnd")) {
     BwTransition transition = transitionMap.get(grouptransition2);
     grouptransition2 = grouptransition2.substring(grouptransition2.indexOf("To") + 2);
     transition.setTo(grouptransition2);
     } else {
     BwTransition transition = transitionMap.get(transitions.item(j).getAttributes().getNamedItem("linkName").getNodeValue());
     transition.setFrom(parent.getAttributes().getNamedItem("name").getTextContent());
     }
     }
     }
     }
     }
     }

     public void parseTransitions(Node parent, Deque<BwGroup> groupsstack) {
     NodeList children = parent.getChildNodes();
     for (int i = 0; i < children.getLength(); i++) {
     if (children.item(i).getNextSibling() != null) {
     if (children.item(i).getNextSibling().getNodeName().equals("bpws:link")) {
     BwTransition transition = new BwTransition();
     //int lineNumber = SaxParser.getStartLineNumber(children.item(i).getNextSibling());
     //transition.setLineNumber(lineNumber);
     String transitionName = children.item(i).getNextSibling().getAttributes().getNamedItem("name").getTextContent();
     if (transitionName.contains("GroupStart")) {
     String synonymsKey = transitionName;
     transitionName = transitionName.substring(transitionName.indexOf("To") + 2);
     //String from = parent.getParentNode().getAttributes().getNamedItem("name").getNodeValue();
     String from = groupsstack.peekLast().getName();
     transition.setFrom(from);
     transition.setTo(transitionName);
     transitionName = from + "To" + transitionName;
     synonymsGroupMapping.put(synonymsKey, transitionName);
     }
     if (transitionName.contains("GroupEnd")) {
     String synonymsKey = transitionName;
     transitionName = transitionName.substring(0, transitionName.indexOf("To"));
     String to = groupsstack.peekLast().getName();
     transition.setTo(to);
     transition.setFrom(transitionName);
     transitionName = transitionName + "To" + to;
     synonymsGroupMapping.put(synonymsKey, transitionName);
     }
     if (children.item(i).getNextSibling().getAttributes().getNamedItem("tibex:label") != null) {
     transition.setLabel(children.item(i).getNextSibling().getAttributes().getNamedItem("tibex:label").getTextContent());
     }
     if (children.item(i).getNextSibling().getAttributes().getNamedItem("tibex:linkType") != null) {
     transition.setConditionType(children.item(i).getNextSibling().getAttributes().getNamedItem("tibex:linkType").getTextContent());
     }
     transition.setName(transitionName);
     transitionMap.put(transitionName, transition);

     }
     }
     }

     }
     */
    public int countAllGroups() {
        int result = 0;
        result += getGroups().size();
        for (BwGroup g : getGroups()) {
            result += g.countAllSubGroups();
        }
        return result;
    }

    public int getEventSourcesCount() {
        return eventSources.size();
    }

    public List<EventSource> getEventSources() {
        return eventSources;
    }

    public void setProject(BwProject aThis) {
        this.project = aThis;
    }

    /**
     * @return the source
     */
    public ProcessSource getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(ProcessSource source) {
        this.source = source;
    }

    public void setResource(InputFile resource) {
        this.resource = resource;
    }

    /**
     * @return the resource
     */
    public InputFile getResource() {
        return resource;
    }

    /**
     * @return the processProperty
     */
    public List<BwProcessProperty> getProcessProperty() {
        return processProperty;
    }

    /**
     * @param processProperty the processProperty to set
     */
    public void setProcessProperty(List<BwProcessProperty> processProperty) {
        this.processProperty = processProperty;
    }

    @Override
    public String getID() {
        return getName();
    }

    @Override
    public Node getNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the namedNodeMap
     */
    public NamedNodeMap getNameNodeMap() {
        return namedNodeMap;
    }

    

    public void setNameNodeMap(NamedNodeMap attributes) {
       this.namedNodeMap = attributes;
    }

}

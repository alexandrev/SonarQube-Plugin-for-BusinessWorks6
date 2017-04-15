package com.tibco.businessworks6.sonar.plugin.data.model.helper;

import com.tibco.businessworks6.sonar.plugin.data.model.*;
import java.util.Collection;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BwpParser {

    private static final Logger LOG = Logger.getLogger(BwpParser.class);

    public static BwTransition getTransitionFromName(BwFlow process, String targetLinkName) {
        Collection<BwTransition> bwTransitionList = process.getTransitionList();
        if (bwTransitionList != null) {
            for (BwTransition bwTrans : bwTransitionList) {
                if (targetLinkName != null && targetLinkName.equals(bwTrans.getName())) {
                    return bwTrans;
                }
            }
        }
        return null;
    }

    public static void calculateExtendedActivities(BwProcess process, BwFlow flow, Element root) {
        LOG.debug("Parsing Extended activities...");
        NodeList nodes = (NodeList) XmlHelper.evaluateXPath(root, "flow/extensionActivity/*[position()=1]");
        if (nodes != null) {
            LOG.debug("Acitivities detected: " + nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                BwActivity activity = new BwActivity();
                activity.setNode(nodes.item(i));
                String name = XmlHelper.extractValueAttr((Element) nodes.item(i), "name");
                LOG.debug("Name value: " + name);
                activity.setName(name);

                //Parsing Configuration Items 
                //TODO Hid the attributes filled by the XML Parser (lineNumber...)
                NodeList configList = (NodeList) XmlHelper.evaluateXPath(root, "flow/extensionActivity/*[position()=1][@name='" + name + "']//activityConfig/properties/value");
                if (configList != null && configList.getLength() > 0) {
                    LOG.debug("Configuration list size: " + configList.getLength());
                    NamedNodeMap attributes = configList.item(0).getAttributes();
                    LOG.debug("Configuration items size: " + attributes.getLength());
                    for (int j = 0; j < attributes.getLength(); j++) {
                        String configName = attributes.item(j).getNodeName();
                        LOG.debug("Config Name: " + configName);
                        String configValue = attributes.item(j).getTextContent();
                        LOG.debug("Config Value: " + configValue);

                        if ("xsi:type".equals(configName)) {
                            activity.setType(configValue);
                        }

                        activity.addConfigData(configName, configValue);
                    }

                    String eventSourceTest = "";
                    Node target = configList.item(0);
                    for (int j = 0; j < 4 && target.getParentNode() != null; j++) {
                        target = target.getParentNode();
                    }
                    eventSourceTest = target.getNodeName();

                    if (eventSourceTest.contains("eventSource")) {
                        process.getEventSources().add(new EventSource(activity));
                    }
                }

                NodeList bindingList = (NodeList) XmlHelper.evaluateXPath(root, "flow/extensionActivity/*[position()=1][@name='" + name + "']//inputBinding");
                if (bindingList != null && bindingList.getLength() > 0) {
                    LOG.debug("Binding list size: " + bindingList.getLength());
                    NamedNodeMap attributes = bindingList.item(0).getAttributes();
                    LOG.debug("Binding items size: " + bindingList.getLength());
                    for (int j = 0; j < attributes.getLength(); j++) {
                        String bindingName = attributes.item(j).getNodeName();
                        LOG.debug("Binding Name: " + bindingName);
                        String bindingValue = attributes.item(j).getTextContent();
                        LOG.debug("Binding Value: " + bindingValue);
                        activity.addBindingData(bindingName, bindingValue);
                    }

                    String eventSourceTest = "";
                    Node target = configList.item(0);
                    if (target != null) {
                        for (int j = 0; j < 4 && target.getParentNode() != null; j++) {
                            target = target.getParentNode();
                        }
                        eventSourceTest = target.getNodeName();

                        if (eventSourceTest.contains("eventSource")) {
                            process.getEventSources().add(new EventSource(activity));
                        }
                    }
                }

                //Parsing Links 
                NodeList sourcesList = (NodeList) XmlHelper.evaluateXPath(root, "flow/extensionActivity/*[position()=1][@name='" + name + "']/sources/source");
                if (sourcesList != null && sourcesList.getLength() > 0) {
                    LOG.debug("Source links list size: " + sourcesList.getLength());
                    for (int j = 0; j < sourcesList.getLength(); j++) {
                        NamedNodeMap attributes = sourcesList.item(j).getAttributes();
                        if (attributes != null && attributes.getNamedItem("linkName") != null) {
                            String sourceLinkName = attributes.getNamedItem("linkName").getNodeValue();
                            if (sourceLinkName != null) {
                                BwTransition sourceTransition = getTransitionFromName(flow, sourceLinkName);
                                if (sourceTransition == null) {
                                    sourceTransition = new BwTransition();
                                    sourceTransition.setName(sourceLinkName);
                                    flow.getTransitionList().add(sourceTransition);
                                }
                                activity.getTransitions().add(sourceTransition);
                                sourceTransition.setOriginalActivity(activity);
                                sourceTransition.compute();

                            }
                        }
                    }
                }

                NodeList targetList = (NodeList) XmlHelper.evaluateXPath(root, "flow/extensionActivity/*[position()=1][@name='" + name + "']/targets/target");
                if (targetList != null && targetList.getLength() > 0) {
                    LOG.debug("Target links list size: " + targetList.getLength());
                    for (int j = 0; j < targetList.getLength(); j++) {
                        NamedNodeMap attributes = targetList.item(j).getAttributes();
                        if (attributes != null && attributes.getNamedItem("linkName") != null) {
                            String targetLinkName = attributes.getNamedItem("linkName").getNodeValue();
                            if (targetLinkName != null) {
                                BwTransition targetTransition = getTransitionFromName(flow, targetLinkName);
                                if (targetTransition == null) {
                                    targetTransition = new BwTransition();
                                    targetTransition.setName(targetLinkName);
                                    flow.getTransitionList().add(targetTransition);
                                }
                                activity.getTransitions().add(targetTransition);
                                targetTransition.setFinalActivity(activity);
                                targetTransition.compute();
                            }
                        }
                    }
                }

                flow.getActivityList().add(activity);

            }

        }
        LOG.debug("Parsed Bw activities...");
    }

    public static void parseTransitions(BwFlow process, Element root) throws NumberFormatException {
        NodeList transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "flow/links/link");
        if (transitionNodeList != null) {
            for (int j = 0; j < transitionNodeList.getLength(); j++) {
                String name = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "name");
                String conditionType = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "tibex:linkType");
                String description = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "tibex:label");

                BwTransition transition = getTransitionFromName(process, name);
                if (transition == null) {
                    transition = new BwTransition();
                }
                transition.setNode(transitionNodeList.item(j));
                transition.setType(conditionType);
                transition.setDescription(description);

                LOG.debug("Added Transtion: " + name + ", condition: " + conditionType + " , description: " + description);
            }
        }
    }

    public static void calculateServicesAndReferences(BwProcess process, Element root) {
        NodeList nodes = (NodeList) XmlHelper.evaluateXPath(root, "//parnerLink");
        if (nodes != null && nodes.getLength() > 0) {
            LOG.debug("Services/Reference Detected: " + nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                BwActivity activity = new BwActivity();
                activity.setNode(nodes.item(i));
                activity.setType(nodes.item(i).getNodeName());
                String name = XmlHelper.extractValueAttr((Element) nodes.item(i), "service");
                if (name != null && !"".equals(name)) {
                    BwService service = process.getServices().get(name);
                    if (service == null) {
                        service = new BwService(name);
                        process.getServices().put(name, service);
                    }
                } else {
                    //BwReference reference = process.getReference
                }

            }
        }
    }

    public static void calculateBasicActivity(BwFlow process, Element root) {

        NodeList nodes = (NodeList) XmlHelper.evaluateXPath(root, "flow/*[local-name() != 'extensionActivity' and local-name() != 'links']");
        if (nodes != null && nodes.getLength() > 0) {
            LOG.debug("Starter nodes detected: " + nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                BwActivity activity = new BwActivity();
                activity.setNode(nodes.item(i));
                activity.setType(nodes.item(i).getNodeName());

                String name = XmlHelper.extractValueAttr((Element) nodes.item(i), "name");
                activity.setName(name);

                //Parsin configuration
                NamedNodeMap attributes = nodes.item(i).getAttributes();
                if (attributes != null) {
                    for (int j = 0; j < attributes.getLength(); j++) {
                        String attrName = attributes.item(j).getNodeName();
                        String attrValue = attributes.item(j).getNodeValue();
                        activity.addConfigData(attrName, attrValue);
                    }
                }

                //Parsing Links 
                NodeList sourcesList = (NodeList) XmlHelper.evaluateXPath(root, "flow/*[local-name() != 'extensionActivity' and local-name() != 'links'][@name='" + name + "']/sources/source");
                if (sourcesList != null && sourcesList.getLength() > 0) {
                    LOG.debug("Source links list size: " + sourcesList.getLength());
                    for (int j = 0; j < sourcesList.getLength(); j++) {
                        NamedNodeMap attributesSource = sourcesList.item(j).getAttributes();
                        if (attributesSource != null && attributesSource.getNamedItem("linkName") != null) {
                            String sourceLinkName = attributesSource.getNamedItem("linkName").getNodeValue();
                            if (sourceLinkName != null) {
                                BwTransition sourceTransition = getTransitionFromName(process, sourceLinkName);
                                if (sourceTransition == null) {
                                    sourceTransition = new BwTransition();
                                    sourceTransition.setName(sourceLinkName);
                                    process.getTransitionList().add(sourceTransition);
                                }
                                activity.getTransitions().add(sourceTransition);
                                sourceTransition.setOriginalActivity(activity);
                                sourceTransition.compute();
                            }
                        }
                    }
                }

                NodeList targetList = (NodeList) XmlHelper.evaluateXPath(root, "flow/*[local-name() != 'extensionActivity' and local-name() != 'links'][@name='" + name + "']/targets/target");
                if (targetList != null && targetList.getLength() > 0) {
                    LOG.debug("Target links list size: " + targetList.getLength());
                    for (int j = 0; j < targetList.getLength(); j++) {
                        NamedNodeMap attributesTarget = targetList.item(j).getAttributes();
                        if (attributesTarget != null && attributesTarget.getNamedItem("linkName") != null) {
                            String targetLinkName = attributesTarget.getNamedItem("linkName").getNodeValue();
                            if (targetLinkName != null) {
                                BwTransition targetTransition = getTransitionFromName(process, targetLinkName);
                                if (targetTransition == null) {
                                    targetTransition = new BwTransition();
                                    targetTransition.setName(targetLinkName);
                                    process.getTransitionList().add(targetTransition);
                                }
                                activity.getTransitions().add(targetTransition);
                                targetTransition.setFinalActivity(activity);
                                targetTransition.compute();
                            }
                        }
                    }
                }

                if ("scope".equals(activity.getType())) {
                    activity = new BwGroup(activity);
                    if (activity instanceof BwGroup) {
                        ((BwGroup) activity).parseFlow((Element) activity.getNode());
                    }
                }

                process.getActivityList().add(activity);
            }
        }

    }

    public static void calculateVariables(BwFlow process, Element root) {
        NodeList transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "../variables/variable|../../variables/variable");
        if (transitionNodeList != null) {
            for (int j = 0; j < transitionNodeList.getLength(); j++) {
                BwVariable var = new BwVariable();
                String name = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "name");
                String internal = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "sca-bpel:internal");
                String privateProperty = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "sca-bpel:privateProperty");
                String source = XmlHelper.extractValueAttr((Element) transitionNodeList.item(j), "tibex:propertySource");
                var.setName(name);
                var.setInternal(Boolean.parseBoolean(internal));
                var.setProperty(Boolean.parseBoolean(privateProperty));
                if (source != null && !source.isEmpty()) {
                    var.setSource(true);
                    var.setValue(source);
                } else {
                    Element result = XmlHelper.evalueXPathSingleElement((Element) transitionNodeList.item(j), "from/literal");
                    if (result != null) {
                        var.setValue(result.getTextContent());
                    }
                }
                LOG.debug("Variable name: " + var.getName() + " and value: " + var.getValue() + " source: " + var.isSource());
                process.getVariables().add(var);
            }
        }
    }

    public static void parseServices(BwProcess process, Element root) {
        NodeList transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "../partnerLinks/partnerLink");
        if (transitionNodeList != null) {
            for (int i = 0; i < transitionNodeList.getLength(); i++) {
                Node elReceive = transitionNodeList.item(i);
                String serviceName = elReceive.getAttributes().getNamedItem("name").getNodeValue();
                if (process.getServices().get(serviceName) == null) {
                    BwService service = new BwService(serviceName);
                    service.setIsService("Service".equals(XmlHelper.extractValueAttr((Element) elReceive, "sca-bpel:service")));
                    process.getServices().put(serviceName, service);
                }
            }
        }

        transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "flow/receive");
        if (transitionNodeList != null) {
            for (int i = 0; i < transitionNodeList.getLength(); i++) {
                Node elReceive = transitionNodeList.item(i);
                String serviceName = elReceive.getAttributes().getNamedItem("partnerLink").getNodeValue();
                String operationName = elReceive.getAttributes().getNamedItem("operation").getNodeValue();
                if (process.getServices().get(serviceName) == null) {
                    String namespacePrefix = elReceive.getAttributes().getNamedItem("portType").getNodeValue();
                    namespacePrefix = namespacePrefix.substring(0, namespacePrefix.indexOf(":"));
                    BwService service = new BwService(serviceName);
                    service.setIsService(true);
                    service.setNamespace(process.getNameNodeMap().getNamedItem("xmlns:" + namespacePrefix).getNodeValue());
                    service.setImplementationProcess(process.getName());
                    Operation operation = new Operation(operationName);
                    service.getOperations().put(operationName, operation);
                    process.getServices().put(serviceName, service);
                } else {
                    BwService service = process.getServices().get(serviceName);
                    String namespacePrefix = elReceive.getAttributes().getNamedItem("portType").getNodeValue();
                    namespacePrefix = namespacePrefix.substring(0, namespacePrefix.indexOf(":"));
                    service.setNamespace(process.getNameNodeMap().getNamedItem("xmlns:" + namespacePrefix).getNodeValue());
                    service.setImplementationProcess(process.getName());

                    Operation operation = new Operation(operationName);
                    service.getOperations().put(operationName, operation);
                }
            }
        }

    }
    
    public static void parseHandlers(BwProcess process, Element root) {
        NodeList transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "//onEvent/scope");
        if (transitionNodeList != null) {
            for (int i = 0; i < transitionNodeList.getLength(); i++) {
                BwEventHandler handler = new BwEventHandler(process);
                handler.setName(XmlHelper.extractValueAttr((Element) transitionNodeList.item(i),"name"));
                process.getEventHandlerList().add(handler);
                calculateBasicActivity(handler, (Element) transitionNodeList.item(i));
            }
        }
        
        transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "//catch/scope");
        if (transitionNodeList != null) {
            for (int i = 0; i < transitionNodeList.getLength(); i++) {
                BwCatchHandler handler = new BwCatchHandler(process);
                handler.setName(XmlHelper.extractValueAttr((Element) transitionNodeList.item(i),"name"));
                process.getCatchList().add(handler);
                calculateBasicActivity(handler, (Element) transitionNodeList.item(i));
            }
        }
        
        transitionNodeList = (NodeList) XmlHelper.evaluateXPath(root, "//catchAll/scope");
        if (transitionNodeList != null) {
            for (int i = 0; i < transitionNodeList.getLength(); i++) {
                BwCatchHandler handler = new BwCatchHandler(process);
                handler.setName(XmlHelper.extractValueAttr((Element) transitionNodeList.item(i),"name"));
                handler.setCatchAll(true);
                process.getCatchList().add(handler);          
                calculateBasicActivity(handler, (Element) transitionNodeList.item(i));
            }
        }
    }

    public static void processProcessData(BwProcess process, Element documentElement) {
        if (documentElement != null) {
            process.setName(FilenameUtils.getBaseName(process.getFile().getName()));
            Element processInfo = XmlHelper.evalueXPathSingleElement(documentElement, "/process/ProcessInfo");
            if (processInfo != null) {
                process.setDescription(XmlHelper.extractValueAttr(processInfo, "description"));
                process.setProductVersion(XmlHelper.extractValueAttr(processInfo, "productVersion"));
                process.setScalable(Boolean.parseBoolean(XmlHelper.extractValueAttr(processInfo, "scalable")));
                process.setSingleton(Boolean.parseBoolean(XmlHelper.extractValueAttr(processInfo, "singleton")));
                process.setStateless(Boolean.parseBoolean(XmlHelper.extractValueAttr(processInfo, "stateless")));
                process.setSubProcess(Boolean.parseBoolean(XmlHelper.extractValueAttr(processInfo, "callable")));
            }
            process.setNameNodeMap(documentElement.getAttributes());
        }
    }

}

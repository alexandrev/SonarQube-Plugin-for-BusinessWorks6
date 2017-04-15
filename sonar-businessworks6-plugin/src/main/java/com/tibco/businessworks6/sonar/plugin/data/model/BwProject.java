package com.tibco.businessworks6.sonar.plugin.data.model;

import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.sonar.api.batch.fs.FileSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BwProject {

    private static final Logger LOG = Logger.getLogger(BwProject.class);

    private List<BwModuleProperty> globalVariables;

    protected List<BwSharedResource> sharedResourcesList;

    private Collection<BwSharedVariable> sharedVariables;
    
    private Collection<BwXmlResource> descriptors;
    
    private Collection<BwXmlResource> schemas;

    private List<BwProcess> process;

    private String encoding;

    protected String version;

    private File bwmFile = new File(System.getProperty("user.dir") + "/META-INF/module.bwm");

    private BwSharedResource versionNode;

    private static BwProject INSTANCE;

    private File file;

    private FileSystem fileSystem;

    public static BwProject getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new BwProject();
        }

        return INSTANCE;
    }

    private BwProject() {

    }

    public Collection<BwSharedResource> getResourceFromType(BwSharedResource.BWResources type) {
        ArrayList<BwSharedResource> tmp = new ArrayList<>();
        for (BwSharedResource shared :sharedResourcesList ) {
            if(type != null && type.equals(shared.getResourceType())){
                tmp.add(shared);
            }
            
        }
        return tmp;
    }

    /**
     * @return the globalVariables
     */
    public List<BwModuleProperty> getModulProperties() {
        return globalVariables;
    }

    /**
     * @param globalVariables the globalVariables to set
     */
    public void setBwModuleProperty(List<BwModuleProperty> globalVariables) {
        this.globalVariables = globalVariables;
    }

    /**
     * @return the sharedResourcesList
     */
    public List<BwSharedResource> getSharedResourcesList() {
        return sharedResourcesList;
    }

    /**
     * @param sharedResourcesList the sharedResourcesList to set
     */
    public void setSharedResourcesList(List<BwSharedResource> sharedResourcesList) {
        this.sharedResourcesList = sharedResourcesList;
    }

    /**
     * @return the enconding
     */
    public String getEnconding() {
        return getEncoding();
    }

    /**
     * @param enconding the enconding to set
     */
    public void setEnconding(String enconding) {
        this.setEncoding(enconding);
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the process
     */
    public List<BwProcess> getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(List<BwProcess> process) {
        this.process = process;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the versionNode
     */
    public BwSharedResource getVersionNode() {
        return versionNode;
    }

    /**
     * @param versionNode the versionNode to set
     */
    public void setVersionNode(BwSharedResource versionNode) {
        this.versionNode = versionNode;
    }

    public int getVersionSuite() {
        return 6;
    }

    public void init(File file) {
        this.file = file;
        LOG.debug("Initializing the BwProject: " + getFile().getAbsolutePath());
        globalVariables = new ArrayList<BwModuleProperty>();
        sharedResourcesList = new ArrayList<BwSharedResource>();
        process = new ArrayList<BwProcess>();
        parseGlobalVariables();
        setSharedVariables(parseSharedVariables("jsv", "//jobSharedVariable", BwSharedVariable.Scope.JOB));
        getSharedVariables().addAll(parseSharedVariables("msv", "//moduleSharedVariable", BwSharedVariable.Scope.MODULE));
        LOG.debug("Initialized the BwProject: " + getFile().getAbsolutePath());

    }

    private void parseGlobalVariables() {
        Collection<File> files = FileUtils.listFiles(getFile(), new String[]{"substvar"}, true);
        if (files != null) {
            LOG.debug("Global variable files recovered: " + files.size());
            for (File fFile : files) {
                LOG.debug("Recovering: " + fFile.getAbsolutePath());
                Document document = XmlHelper.getDocument(fFile);
                if (document != null) {
                    NodeList nodeList = XmlHelper.evaluateXPath(document, "/*[1]/*[1]/*");
                    if (nodeList != null) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element node = (Element) nodeList.item(i);
                            if (node != null) {
                                LOG.debug("Global variable extracted");
                                BwModuleProperty bVar = new BwModuleProperty();
                                String name = XmlHelper.extractValueNoNS(node, "name");
                                bVar.setName(name);
                                LOG.debug("BW GVAR Name: " + name);
                                String value = XmlHelper.extractValueNoNS(node, "value");
                                bVar.setValue(value);
                                LOG.debug("BW GVAR Value: " + value);
                                String deploymentSettable = XmlHelper.extractValueNoNS(node, "deploymentSettable");
                                bVar.setDeploymentSettable(Boolean.parseBoolean(deploymentSettable));
                                LOG.debug("BW GVAR Deployment Settable: " + deploymentSettable);

                                String serviceSettable = XmlHelper.extractValueNoNS(node, "serviceSettable");
                                bVar.setServiceSettable(Boolean.parseBoolean(serviceSettable));
                                LOG.debug("BW GVAR Service Settable: " + serviceSettable);

                                String type = XmlHelper.extractValueNoNS(node, "type");
                                bVar.setType(type);
                                LOG.debug("BW GVAR Type: " + type);

                                String isOverride = XmlHelper.extractValueNoNS(node, "isOverride");
                                bVar.setOverride(Boolean.parseBoolean(isOverride));
                                LOG.debug("BW GVAR isOverride: " + isOverride);

                                String modTime = XmlHelper.extractValueNoNS(node, "modTime");
                                LOG.debug("BW GVAR modTime: " + modTime);
                                if (modTime != null && !modTime.isEmpty()) {
                                    bVar.setModTime(new Date(Long.parseLong(modTime)));
                                }

                                globalVariables.add(bVar);
                            }
                        }
                    }

                }
            }
        }

    }

    private Collection<BwSharedVariable> parseSharedVariables(String extension, String xpath, BwSharedVariable.Scope scope) {
        Collection<File> files = FileUtils.listFiles(getFile(), new String[]{extension}, true);
        Collection<BwSharedVariable> out = new ArrayList<BwSharedVariable>();
        if (files != null) {
            LOG.debug("Global variable files recovered: " + files.size());
            for (File fFile : files) {
                LOG.debug("Recovering: " + fFile.getAbsolutePath());
                Document document = XmlHelper.getDocument(fFile);
                if (document != null) {
                    NodeList nodeList = XmlHelper.evaluateXPath(document, xpath);
                    if (nodeList != null) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element node = (Element) nodeList.item(i);
                            if (node != null) {
                                LOG.debug("Shared variable extracted");
                                BwSharedVariable bVar = new BwSharedVariable();
                                String name = XmlHelper.extractValueAttr(node, "name");
                                bVar.setName(name);
                                LOG.debug("BW SHARED VAR Name: " + name);
                                String value = XmlHelper.extractValueAttr(node, "initialValueMode");
                                bVar.setInitialValue(value);
                                LOG.debug("BW  SHARED VAR Value: " + value);

                                String type = XmlHelper.extractValueAttr(node, "type");
                                bVar.setType(type);
                                LOG.debug("BW GVAR Type: " + type);

                                bVar.setScope(scope);

                                out.add(bVar);
                            }
                        }
                    }

                }
            }
        }
        return out;
    }

    /**
     * @return the sharedVariables
     */
    public Collection<BwSharedVariable> getSharedVariables() {
        return sharedVariables;
    }

    /**
     * @param sharedVariables the sharedVariables to set
     */
    public void setSharedVariables(Collection<BwSharedVariable> sharedVariables) {
        this.sharedVariables = sharedVariables;
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

    public void addProcess(BwProcess process) {
        this.process.add(process);
        process.setProject(this);
    }

    public void addResource(BwSharedResource resource) {
        this.sharedResourcesList.add(resource);
        resource.setProject(this);
    }

    /**
     * @return the bwmFile
     */
    public File getBwmFile() {
        return bwmFile;
    }

    /**
     * @param bwmFile the bwmFile to set
     */
    public void setBwmFile(File bwmFile) {
        this.bwmFile = bwmFile;
    }

    /**
     * @return the fileSystem
     */
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * @param fileSystem the fileSystem to set
     */
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * @return the descriptors
     */
    public Collection<BwXmlResource> getDescriptors() {
        return descriptors;
    }

    /**
     * @param descriptors the descriptors to set
     */
    public void setDescriptors(Collection<BwXmlResource> descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * @return the schemas
     */
    public Collection<BwXmlResource> getSchemas() {
        return schemas;
    }

    /**
     * @param schemas the schemas to set
     */
    public void setSchemas(Collection<BwXmlResource> schemas) {
        this.schemas = schemas;
    }

}

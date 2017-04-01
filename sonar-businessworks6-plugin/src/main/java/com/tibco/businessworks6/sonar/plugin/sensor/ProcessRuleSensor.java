/*
 * SonarQube XML Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tibco.businessworks6.sonar.plugin.sensor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import com.tibco.businessworks6.sonar.plugin.check.AbstractCheck;
import com.tibco.businessworks6.sonar.plugin.check.project.DeadLockCheck;
import com.tibco.businessworks6.sonar.plugin.language.ProcessLanguage;
import com.tibco.businessworks6.sonar.plugin.metric.BusinessWorksMetrics;
import com.tibco.businessworks6.sonar.plugin.rulerepository.ProcessRuleDefinition;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.source.ResourcesSource;
import com.tibco.businessworks6.sonar.plugin.source.Source;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.data.model.ModuleProperties;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * XmlSensor provides analysis of xml files.
 *
 * @author Kapil Shivarkar
 */
public class ProcessRuleSensor extends AbstractRuleSensor {

    private Resource processFileResource;
    private static final Logger LOG = Logger.getLogger(ProcessRuleSensor.class);

    public ProcessRuleSensor(RulesProfile profile, FileSystem fileSystem,
            ResourcePerspectives resourcePerspectives, CheckFactory checkFactory) {
        super(profile, fileSystem, resourcePerspectives,
                ProcessRuleDefinition.REPOSITORY_KEY, ProcessLanguage.KEY, checkFactory);
    }

    private boolean isProcess(InputFile resource) {
        return resource.file().getAbsolutePath().endsWith(".bwp");
    }

    private boolean isResource(InputFile resource) {
        String ext = FilenameUtils.getExtension(resource.file().getName());
        return !Arrays.asList(ProcessLanguage.NON_RESOURCES_SUFFIXES).contains(ext);
    }

    private final BwProject bwProject = BwProject.getInstance();

    public enum BWResources {
        HTTPClient,
        XMLAuthentication,
        WSSAuthentication,
        TrustProvider,
        ThrealPool,
        TCPConnection,
        SubjectProvider,
        SSLServerConfiguration,
        SSLClientConfiguration,
        SMTPResource,
        RendezvousTransport,
        ProxyConfiguration,
        LDAPAuthentication,
        KeystoreProvider,
        JNDIConfiguration,
        JMSConnection,
        JDBCConnection,
        JavaGlobalInstance,
        IdentityProvider,
        HTTPConnector,
        FTPConnection,
        FTLRealmServerConnection,
        DataFormat,
        SQLFile
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void analyseFile(java.io.File file) {
        InputFile resource = fs.inputFile(fs.predicates().is(file));
        Source sourceCode = null;
        //Check if this is a process definition file
        if (isProcess(resource)) {
            sourceCode = new ProcessSource(file);
            ProcessSource processSource = (ProcessSource) sourceCode;
            BwProcess process = processSource.getProcessModel();
            process.setResource(resource);
            bwProject.addProcess(process);

            //Check if is a resource definition file
        } else if (isResource(resource)) {
            sourceCode = new ResourcesSource(file);
            ResourcesSource resourceSource = (ResourcesSource) sourceCode;
            BwSharedResource sharedResource = resourceSource.getResourceModel();
            sharedResource.setResource(resource);
            bwProject.addResource(sharedResource);
        }

        if (sourceCode != null && sourceCode.parseSource(fileSystem.encoding())) {
            for (AbstractCheck check : abstractCheck) {
                if (!(check instanceof DeadLockCheck)) {
                    RuleKey ruleKey = checks.ruleKey(check);
                    check.setRuleKey(ruleKey);
                    check.setRule(profile.getActiveRule(ruleKey.repository(), ruleKey.rule()).getRule());
                    sourceCode = check.validate(sourceCode);
                }
            }
            saveIssues(sourceCode, resource);
        }

    }

    @Override
    protected void analyseDeadLock(Iterable<File> filesIterable) {
        //TODO CALL!
    }

    @Override
    public void processMetrics() {
        int moduleProperties = getPropertiesCount(".bwm");
        int groupsProcess = 0;
        int activitiesProcess = 0;
        int transitionsProcess = 0;
        int processStarters = 0;
        int catchBlocks = 0;
        int noOfProcesses = bwProject.getProcess().size();
        int sharedResources = bwProject.getSharedResourcesList().size();
        int jobSharedVariable = getPropertiesCount(".jsv");
        int moduleSharedVariable = getPropertiesCount(".msv");
        int eventHandlers = 0;
        int services = 0;
        int subprocesscount = 0;
        int subservice = 0;
        int subreference = 0;

        for (Iterator<BwProcess> iterator = bwProject.getProcess().iterator(); iterator.hasNext();) {
            BwProcess process = iterator.next();
            groupsProcess += process.getGroupList().size();
            activitiesProcess += process.getFullActivityList().size();
            transitionsProcess += process.getTransitionList().size();
            processStarters += process.getEventSourcesCount();
            catchBlocks += 1; //process.getCatchcount();
            eventHandlers += 1; //process.getEventHandler();
            services += process.getServices().size();
            if (process.isSubProcess()) {
                subprocesscount++;
                subservice += process.getServices().size();
                subreference += process.getProcessReferenceServices().size();
            }
        }

        noOfProcesses = noOfProcesses - subprocesscount;
        if (sensorContext.getMeasure(BusinessWorksMetrics.BWLANGUAGEFLAG) == null) {
            saveMeasure(BusinessWorksMetrics.BWLANGUAGEFLAG, (double) 1);
        }
        //processFileResource = sensorContext.getResource(ProcessFileResource.fromIOFile(file, project));
        saveMeasure(BusinessWorksMetrics.PROCESSES, (double) noOfProcesses);
        saveMeasure(BusinessWorksMetrics.SUBPROCESSES, (double) subprocesscount);
        saveMeasure(BusinessWorksMetrics.GROUPS, (double) groupsProcess);
        saveMeasure(BusinessWorksMetrics.ACTIVITIES, (double) activitiesProcess);
        saveMeasure(BusinessWorksMetrics.TRANSITIONS, (double) transitionsProcess);
        saveMeasure(BusinessWorksMetrics.PROCESSSTARTER, (double) processStarters);
        saveMeasure(BusinessWorksMetrics.GLOBALVARIABLES, (double) moduleProperties);
        saveMeasure(BusinessWorksMetrics.BWRESOURCES, (double) sharedResources);
        saveMeasure(BusinessWorksMetrics.JOBSHAREDVARIABLES, (double) jobSharedVariable);
        saveMeasure(BusinessWorksMetrics.MODULESHAREDVARIABLES, (double) moduleSharedVariable);
        saveMeasure(BusinessWorksMetrics.CATCHBLOCK, (double) catchBlocks);
        saveMeasure(BusinessWorksMetrics.EVENTHANDLER, (double) eventHandlers);
        saveMeasure(BusinessWorksMetrics.SERVICES, (double) services);
        saveMeasure(BusinessWorksMetrics.SUBSERVICES, (double) subservice);
        saveMeasure(BusinessWorksMetrics.SUBREFERENCE, (double) subreference);
        saveMeasure(BusinessWorksMetrics.PROJECTCOMPLEXITY, "MEDIUM");
        saveMeasure(BusinessWorksMetrics.CODEQUALITY, "AVERAGE");
        Metric[] metric = BusinessWorksMetrics.resourceMetrics();
        BWResources bwresource;
        for (int i = 0; i < metric.length; i++) {
            bwresource = BWResources.valueOf(metric[i].getName().replaceAll("\\s", ""));
            switch (bwresource) {
                case HTTPClient:
                    saveMeasure(BusinessWorksMetrics.BWRESOURCES_HTTP_CONNECTION_FLAG, (double) 1);
                    break;
                case XMLAuthentication:
                    saveMeasure(BusinessWorksMetrics.XML_AUTHENTICATION_FLAG, (double) 1);
                    break;
                case WSSAuthentication:
                    saveMeasure(BusinessWorksMetrics.WSS_Authentication_FLAG, (double) 1);
                    break;
                case TrustProvider:
                    saveMeasure(BusinessWorksMetrics.Trust_Provider_Flag, (double) 1);
                    break;
                case ThrealPool:
                    saveMeasure(BusinessWorksMetrics.Threal_Pool_Flag, (double) 1);
                    break;
                case TCPConnection:
                    saveMeasure(BusinessWorksMetrics.TCP_Connection_Flag, (double) 1);
                    break;
                case SubjectProvider:
                    saveMeasure(BusinessWorksMetrics.Subject_Provider_Flag, (double) 1);
                    break;
                case SSLServerConfiguration:
                    saveMeasure(BusinessWorksMetrics.SSL_Server_Configuration_Flag, (double) 1);
                    break;
                case SSLClientConfiguration:
                    saveMeasure(BusinessWorksMetrics.SSL_Client_Configuration_Flag, (double) 1);
                    break;
                case SMTPResource:
                    saveMeasure(BusinessWorksMetrics.SMTP_Resource_Flag, (double) 1);
                    break;
                case RendezvousTransport:
                    saveMeasure(BusinessWorksMetrics.RVTRANSPORTFLAG, (double) 1);
                    break;
                case ProxyConfiguration:
                    saveMeasure(BusinessWorksMetrics.Proxy_Configuration_Flag, (double) 1);
                    break;
                case LDAPAuthentication:
                    saveMeasure(BusinessWorksMetrics.LDAP_Authentication_Flag, (double) 1);
                    break;
                case KeystoreProvider:
                    saveMeasure(BusinessWorksMetrics.Keystore_Provider_Flag, (double) 1);
                    break;
                case JNDIConfiguration:
                    saveMeasure(BusinessWorksMetrics.JNDI_Configuration_Flag, (double) 1);
                    break;
                case JMSConnection:
                    saveMeasure(BusinessWorksMetrics.BWRESOURCES_JMS_CONNECTION_FLAG, (double) 1);
                    break;
                case JDBCConnection:
                    saveMeasure(BusinessWorksMetrics.BWRESOURCES_JDBC_CONNECTION_FLAG, (double) 1);
                    break;
                case JavaGlobalInstance:
                    saveMeasure(BusinessWorksMetrics.Java_Global_Instance_Flag, (double) 1);
                    break;
                case IdentityProvider:
                    saveMeasure(BusinessWorksMetrics.Identity_Provider_Flag, (double) 1);
                    break;
                case HTTPConnector:
                    saveMeasure(BusinessWorksMetrics.BWRESOURCES_HTTP_CONNECTOR_FLAG, (double) 1);
                    break;
                case FTPConnection:
                    saveMeasure(BusinessWorksMetrics.FTP_Connection_Flag, (double) 1);
                    break;
                case FTLRealmServerConnection:
                    saveMeasure(BusinessWorksMetrics.FTL_Realm_Server_Connection_Flag, (double) 1);
                    break;
                case DataFormat:
                    saveMeasure(BusinessWorksMetrics.Data_Format_Flag, (double) 1);
                    break;
                case SQLFile:
                    saveMeasure(BusinessWorksMetrics.SQL_File_Flag, (double) 1);
                    break;
                default:
                    break;
            }
            saveMeasure(metric[i], (double) foundResources.get(metric[i].getName()));
        }
    }

    private void saveMeasure(Metric metric, double value) {
        sensorContext.saveMeasure(processFileResource, new Measure(metric, value));
    }

    private void saveMeasure(Metric metric, String value) {
        sensorContext.saveMeasure(processFileResource, new Measure(metric, value));
    }

    public static Map<String, Integer> foundResources = new HashMap<>();

    

    public int getPropertiesCount(final String fileExtension) {
        File dir = new File(System.getProperty("user.dir") + "/META-INF");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(fileExtension);
            }
        });
        ModuleProperties moduleprops = new ModuleProperties(files[0]);
        if (fileExtension.equals(".jsv")) {
            return moduleprops.getPropertiesCount("jobSharedVariable");
        } else if (fileExtension.equals(".bwm")) {
            return moduleprops.getPropertiesCount("sca:property");
        } else {
            return moduleprops.getPropertiesCount("moduleSharedVariable");
        }
    }

    public int getModulePropertiesCount() {
        File dir = new File(System.getProperty("user.dir") + "/META-INF");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".bwm");
            }
        });
        ModuleProperties moduleprops = new ModuleProperties(files[0]);
        return moduleprops.getPropertiesCount("sca:property");
    }

    /**
     * This sensor only executes on projects with active XML rules.
     */
    @Override
    public boolean shouldExecuteOnProject(Project project) {
        /*return !fileSystem.files(FileQuery.onSource().onLanguage(ProcessLanguage.KEY))
				.isEmpty();*/
        return fileSystem.files(fileSystem.predicates().hasLanguage(languageKey)).iterator().hasNext();

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}

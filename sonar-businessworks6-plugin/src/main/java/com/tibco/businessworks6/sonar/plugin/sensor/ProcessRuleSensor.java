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

import com.tibco.businessworks6.sonar.plugin.BwConstants;
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
import com.tibco.businessworks6.sonar.plugin.metric.BusinessWorksMetrics;
import com.tibco.businessworks6.sonar.plugin.rulerepository.ProcessRuleDefinition;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.source.ResourcesSource;
import com.tibco.businessworks6.sonar.plugin.source.Source;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.data.model.ModuleProperties;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwXmlResource;
import com.tibco.businessworks6.sonar.plugin.language.BusinessWorks6Language;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.sonar.api.config.Settings;

/**
 * XmlSensor provides analysis of xml files.
 *
 * @author Kapil Shivarkar
 */
public class ProcessRuleSensor extends AbstractRuleSensor {

    private Resource processFileResource;
    private static final Logger LOG = Logger.getLogger(ProcessRuleSensor.class);

    public ProcessRuleSensor(Settings settings,RulesProfile profile, FileSystem fileSystem,
            ResourcePerspectives resourcePerspectives, CheckFactory checkFactory) {
        super(settings,profile, fileSystem, resourcePerspectives,
                BwConstants.REPOSITORY_KEY, BwConstants.LANGUAGE_KEY, checkFactory);
    }

    private boolean isProcess(InputFile resource) {
        return resource.file().getAbsolutePath().endsWith(".bwp");
    }

    private boolean isResource(InputFile resource) {
        String ext = FilenameUtils.getExtension(resource.file().getName());
        return !Arrays.asList(BusinessWorks6Language.INSTANCE.getNonResourceSuffixes()).contains(ext);
    }
    
    public boolean isDescriptor(BwXmlResource resource) {
        boolean out = false;
        if(resource != null){
            File file = resource.getFile();
            if(file != null){
                out = file.getName().toLowerCase().endsWith(".wsdl");
            }
        }
        return out;
    }

    public boolean isSchema(BwXmlResource resource) {
        boolean out = false;
        if(resource != null){
            File file = resource.getFile();
            if(file != null){
                out = file.getName().toLowerCase().endsWith(".xsd");
            }
        }
        return out;
    
    }

    private final BwProject bwProject = BwProject.getInstance();

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
        } else {
            BwXmlResource xmlResource = new BwXmlResource(resource);
            if (isDescriptor(xmlResource)) {
                bwProject.getDescriptors().add(xmlResource);
            } else if (isSchema(xmlResource)) {
                bwProject.getSchemas().add(xmlResource);
            }
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
    public void processMetrics() {
        LOG.debug("processMetrics START");
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

        LOG.debug("Adding metrics for each process in the project");
        for (Iterator<BwProcess> iterator = bwProject.getProcess().iterator(); iterator.hasNext();) {
            BwProcess process = iterator.next();
            LOG.debug("Metrics from: " + process.getName());
            groupsProcess += process.getGroupList().size();
            activitiesProcess += process.getFullActivityList().size();
            transitionsProcess += process.getTransitionList().size();
            processStarters += process.getEventSourcesCount();
            catchBlocks += process.getCatchList().size();
            eventHandlers += process.getEventHandlerList().size();
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
        //TODO CALCULATE
        saveMeasure(BusinessWorksMetrics.PROJECTCOMPLEXITY, "MEDIUM");
        saveMeasure(BusinessWorksMetrics.CODEQUALITY, "AVERAGE");

        LOG.debug("Get resource metrics");
        Metric[] metric = BusinessWorksMetrics.BWRESOURCES_METRICS_LIST;

        LOG.debug("Resouce Metrics Size: " + metric.length);
        for (int i = 0; i < metric.length; i++) {
            BwSharedResource.BWResources bwresource = BwSharedResource.BWResources.valueOf(metric[i].getKey());
            LOG.debug("BW Resource: " + bwresource);
            int size = bwProject.getResourceFromType(bwresource).size();
            saveMeasure(metric[i], (double) size);
            LOG.debug("BW Resource Count: " + size);
        }
        LOG.debug("processMetrics END");
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

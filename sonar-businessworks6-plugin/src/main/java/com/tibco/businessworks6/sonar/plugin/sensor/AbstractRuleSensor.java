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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sonar.api.batch.SensorContext;
//import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
//import org.sonar.api.checks.AnnotationCheckFactory;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

import com.google.common.annotations.VisibleForTesting;
import com.tibco.businessworks6.sonar.plugin.check.AbstractCheck;
import com.tibco.businessworks6.sonar.plugin.check.AbstractProjectCheck;
import com.tibco.businessworks6.sonar.plugin.check.other.BwCustomXPathFilter;
import com.tibco.businessworks6.sonar.plugin.check.process.BwTransitionXPathNoOtherwiseCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterHttpCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterJDBCÇheck;
import com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterRESTCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.CheckpointInTransation;
import com.tibco.businessworks6.sonar.plugin.check.process.ChoiceOtherwiseCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.CriticalSectionCheck;
import com.tibco.businessworks6.sonar.plugin.check.project.DeadLockCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.ForEachGroupCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.ForEachMappingCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.JDBCHardCodeCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.JDBCWildCardCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.JMSAcknowledgementModeCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.JMSHardCodeCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.LogOnlyInSubprocessCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.MultipleTransitionCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.NoDescriptionCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.NumberofActivitiesCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.NumberofServicesCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.ProcessVersionCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.SubProcessInlineCheck;
import com.tibco.businessworks6.sonar.plugin.check.process.TransitionLabelCheck;
import com.tibco.businessworks6.sonar.plugin.check.project.BwModulePropertyNotUsed;
import com.tibco.businessworks6.sonar.plugin.check.project.BwSharedVariableNotUsed;
import com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceNotUsed;
import com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceParameterRequired;
import com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceUsingModuleProperty;
import com.tibco.businessworks6.sonar.plugin.source.Source;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.source.ProjectSource;
import java.io.File;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.rule.RuleKey;

/**
 * XmlSensor provides analysis of xml files.
 *
 * @author Kapil Shivarkar
 */
public abstract class AbstractRuleSensor extends AbstractSensor {

    //protected AnnotationCheckFactory annotationCheckFactory;
    protected Collection<AbstractCheck> abstractCheck;
    protected FileSystem fs;
    protected Checks checks;
    protected RulesProfile profile;

    @SuppressWarnings("rawtypes")
    protected AbstractRuleSensor(RulesProfile profile,
            FileSystem fileSystem,
            ResourcePerspectives resourcePerspectives, String repositoryKey,
            String languageKey, CheckFactory checkFactory) {
        super(fileSystem, resourcePerspectives, languageKey, checkFactory);
        this.fs = fileSystem;
        this.profile = profile;
        /*this.annotationCheckFactory = AnnotationCheckFactory.create(profile,
				repositoryKey, list);*/
        checks = checkFactory.create(repositoryKey);
        List<Class> allChecks = new ArrayList<>();
        allChecks.add(NoDescriptionCheck.class);
        allChecks.add(NumberofActivitiesCheck.class);
        allChecks.add(TransitionLabelCheck.class);
        allChecks.add(ChoiceOtherwiseCheck.class);
        allChecks.add(JDBCWildCardCheck.class);
        allChecks.add(JDBCHardCodeCheck.class);
        allChecks.add(MultipleTransitionCheck.class);
        allChecks.add(DeadLockCheck.class);
        allChecks.add(LogOnlyInSubprocessCheck.class);
        allChecks.add(JMSHardCodeCheck.class);
        allChecks.add(ForEachMappingCheck.class);
        allChecks.add(ForEachGroupCheck.class);
        allChecks.add(NumberofServicesCheck.class);
        allChecks.add(CheckpointAfterRESTCheck.class);
        allChecks.add(CheckpointAfterJDBCÇheck.class);
        allChecks.add(CheckpointAfterHttpCheck.class);
        allChecks.add(CheckpointInTransation.class);
        allChecks.add(JMSAcknowledgementModeCheck.class);
        allChecks.add(CriticalSectionCheck.class);
        allChecks.add(SubProcessInlineCheck.class);
        allChecks.add(BwTransitionXPathNoOtherwiseCheck.class);
        allChecks.add(ProcessVersionCheck.class);
        allChecks.add(BwModulePropertyNotUsed.class);
        allChecks.add(BwSharedVariableNotUsed.class);
        allChecks.add(BwModulePropertyNotUsed.class);
        allChecks.add(BwSharedResourceNotUsed.class);
        allChecks.add(BwSharedResourceUsingModuleProperty.class);
        allChecks.add(BwSharedResourceParameterRequired.class);
        allChecks.add(BwCustomXPathFilter.class);

        checks.addAnnotatedChecks(allChecks);
    }

    /**
     * Analyze the XML files.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void analyse(Project project, SensorContext sensorContext) {
        this.abstractCheck = checks.all();
        //this.abstractCheck = annotationCheckFactory.getChecks();
        this.project = project;
        BwProject bwProject = BwProject.getInstance();
        bwProject.setFileSystem(fileSystem);
        File fTmp = new File(System.getProperty("user.dir"));
        bwProject.init(fTmp);
        ProjectSource source = new ProjectSource(bwProject.getFile());
        InputDir resource = fs.inputDir(fTmp);

        super.analyse(project, sensorContext);

        for (AbstractCheck check : (Collection<AbstractCheck>) checks.all()) {
            if (check instanceof AbstractProjectCheck) {
                RuleKey ruleKey = checks.ruleKey(check);
                check.setRuleKey(ruleKey);
                check.setRule(profile.getActiveRule(ruleKey.repository(), ruleKey.rule()).getRule());
                ((AbstractProjectCheck) check).validate(source);
            }
        }

        saveIssues(source, resource);

    }

    @SuppressWarnings("rawtypes")
    @VisibleForTesting
    protected void saveIssues(Source source, InputPath resource) {
        for (Violation issue : source.getViolations()) {
            Issuable issuable = resourcePerspectives.as(Issuable.class,
                    resource);
            int lineNumber = 1;
            if (issue.getLine() != 0) {
                lineNumber = issue.getLine();
            }

            System.out.println("############# " + resource.absolutePath());

            if (resource instanceof InputFile) {
                issuable.addIssue(issuable.newIssueBuilder()
                        .ruleKey(issue.getRule().ruleKey())
                        .line(lineNumber)
                        .message(issue.getMessage()).build());
            } else if (resource instanceof InputDir) {
                issuable.addIssue(issuable.newIssueBuilder()
                        .ruleKey(issue.getRule().ruleKey())
                        .message(issue.getMessage()).build());
            }
        }
    }

    @Override
    protected abstract void processMetrics();
}

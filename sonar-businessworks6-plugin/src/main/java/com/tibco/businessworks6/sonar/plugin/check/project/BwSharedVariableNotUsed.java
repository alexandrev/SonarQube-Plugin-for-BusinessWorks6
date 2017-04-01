package com.tibco.businessworks6.sonar.plugin.check.project;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProjectCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ProjectSource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedVariable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.log4j.Logger;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(
        key = BwSharedVariableNotUsed.RULE_KEY,
        name = "Shared variable not used",
        description = "Shared variable not used",
        priority = Priority.MINOR)

@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)

public class BwSharedVariableNotUsed extends AbstractProjectCheck{

    public static final String RULE_KEY = "BwSharedVariableNotUsed";

    private static final Logger LOG = Logger.getLogger(BwSharedVariableNotUsed.class);

    @Override
    public void validate(ProjectSource xmlProjectSource) {
        BwProject bwProject = xmlProjectSource.getResourceModel();
        LOG.debug("Started rule: " + this.getClass());
            LOG.debug("Checking project files");
            FileSystem fileSystem = bwProject.getFileSystem();
            if (fileSystem != null) {
                Iterable<File> files = fileSystem.files(fileSystem.predicates().all());
                if (files != null) {
                    for(BwSharedVariable variable : bwProject.getSharedVariables()){
                        boolean found = false;
                        for (File file : files) {
                            LOG.debug("Analyzing file: " + file.getAbsolutePath());
                            if (file.getName().endsWith(".bwp")) {
                                try {
                                    Scanner s = new Scanner(file, "UTF-8");
                                    if (null != s.findWithinHorizon("variableName=\""+ variable.getName() +"\"", 0)) {
                                        found = found || true;
                                    }
                                    s.close();
                                } catch (FileNotFoundException ex) {
                                    LOG.warn("File not found", ex);
                                } catch (NullPointerException ex) {
                                    LOG.warn("Catching NullPointerException", ex);
                                }
                            }
                        }

                        if (!found) {
                            LOG.warn("BW Shared Resource not used in the project scope");
                            addError(1,l10n.format(LocalizationMessages.SONAR_BW_SHARED_VARIABLE_NOT_USED_TEXT_ISSUE),xmlProjectSource);
                        }
                    }
                }
            }
        
        LOG.debug("Finished rule: " + this.getClass());
    }


}

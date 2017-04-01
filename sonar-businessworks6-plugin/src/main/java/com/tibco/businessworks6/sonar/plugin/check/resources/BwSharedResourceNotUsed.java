package com.tibco.businessworks6.sonar.plugin.check.resources;

import com.tibco.businessworks6.sonar.plugin.check.AbstractResourceCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ResourcesSource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.log4j.Logger;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;


@Rule(
        key = BwSharedResourceNotUsed.RULE_KEY,
        name = "Shared Resource not used",
        description = "Shared Resource not used",
        priority = Priority.MINOR)

@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class BwSharedResourceNotUsed extends AbstractResourceCheck{

    public static final String RULE_KEY = "SharedResourcesNotUsed";
 
    private static final Logger LOG = Logger.getLogger(BwSharedResourceNotUsed.class);

    @Override
    public void validate(ResourcesSource resourceXml) {
        LOG.debug("Started rule: " + this.getClass());

            BwSharedResource resource = (BwSharedResource) resourceXml.getResourceModel();
            LOG.debug("Checking project files");
            FileSystem fileSystem = resource.getProject().getFileSystem();
            if (fileSystem != null) {
                Iterable<File> files = fileSystem.files(fileSystem.predicates().all());
                if (files != null) {
                    boolean found = false;
                    for (File file : files) {
                        LOG.debug("Analyzing file: " + file.getAbsolutePath());
                        if (!isDefinitionFile(resource, file)) {
                            try {
                                Scanner s = new Scanner(file, "UTF-8");
                                if (null != s.findWithinHorizon(resource.getFullName(), 0)) {
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
                        addError(1, l10n.format(LocalizationMessages.SONAR_BW_SHARED_RESOURCE_NOT_USED_TEXT_ISSUE), resourceXml);
                    }

                }
            }
        
        LOG.debug("Finished rule: " + this.getClass());
    }



    private boolean isDefinitionFile(BwSharedResource resource, File file) {
        boolean out = false;
        if (resource != null && file != null) {
            String name = resource.getName();
            int lstIdxOf = name.lastIndexOf('.');
            if (lstIdxOf > 0) {
                name = name.substring(lstIdxOf + 1);
                String fileName = file.getName();
                lstIdxOf = fileName.indexOf('.');
                if (lstIdxOf > 0) {
                    fileName = fileName.substring(0, lstIdxOf);
                    if (fileName.equals(name)) {
                        out = true;
                    }
                }
            }
        }
        return out;
    }


}

package com.tibco.businessworks6.sonar.plugin.check.process;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(
        name = "ProcessVersionCheck",
        description = "ProcessVersionCheck",
        key = ProcessVersionCheck.RULE_KEY,
        priority = Priority.MINOR)

@BelongsToProfile(
        title = ProcessSonarWayProfile.defaultProfileName,
        priority = Priority.MINOR)

public class ProcessVersionCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ProcessVersionCheck";
    
    private static final int TECHNICAL_DEBT_MINUTES = 2;

    @RuleProperty(
            key = "versionPattern",
            type = "TEXT",
            defaultValue = "6\\..*")
    private String versionPattern;

    @Override
    public void validate(ProcessSource resource) {

        BwProcess process = (BwProcess) resource.getProcessModel();
        if (process != null) {
            debug(process, "Started rule: " + this.getClass());
            String version = process.getProductVersion();
            LOG.debug("Project version: " + version);
            if (version != null) {
                if (version.isEmpty() || (!version.isEmpty() && (!version.matches(versionPattern)))) {
                    LOG.warn("The project version is not allowed: " + version);
                    addError(1, l10n.format(LocalizationMessages.SONAR_BW_PROJECT_VERSION_TEXT_ISSUE) + version, resource);
                } else {
                    LOG.debug("The project version is allowed");
                }
            }
            debug(process, "Finished rule: " + this.getClass());
        }

    }

    public double getTechnicalDebt() {
        return TECHNICAL_DEBT_MINUTES;
    }

    /**
     * @return the versionPattern
     */
    public String getVersionPattern() {
        return versionPattern;
    }

    /**
     * @param versionPattern the versionPattern to set
     */
    public void setVersionPattern(String versionPattern) {
        this.versionPattern = versionPattern;
    }

}

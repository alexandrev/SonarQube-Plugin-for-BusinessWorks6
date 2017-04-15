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
        name = "ProcessName",
        description = "ProcessName",
        key = ProcessNameCheck.RULE_KEY,
        priority = Priority.MINOR)

@BelongsToProfile(
        title = ProcessSonarWayProfile.defaultProfileName,
        priority = Priority.MINOR)

public class ProcessNameCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ProcessName";
    
    private static final int TECHNICAL_DEBT_MINUTES = 2;


    private String namePattern= "[A-Za-z0-9_\\-]+\\.bwp";

    @Override
    public void validate(ProcessSource resource) {

        BwProcess process = (BwProcess) resource.getProcessModel();
        if (process != null) {
            debug(process, "Started rule: " + this.getClass());
            String name = process.getFile().getName();
            debug(process,"Process name: " + name);
            if (name != null) {
                if (name.isEmpty() || (!name.isEmpty() && (!name.matches(namePattern)))) {
                    LOG.warn("Process name is not allowed: " + name);
                    addError(1, l10n.format(LocalizationMessages.SONAR_BW_PROJECT_NAME_TEXT_ISSUE) + name, resource);
                } else {
                    debug(process,"The project version is allowed");
                }
            }
            debug(process, "Finished rule: " + this.getClass());
        }

    }

    public double getTechnicalDebt() {
        return TECHNICAL_DEBT_MINUTES;
    }

    /**
     * @return the namePattern
     */
    public String getVersionPattern() {
        return namePattern;
    }

    /**
     * @param versionPattern the namePattern to set
     */
    public void setVersionPattern(String versionPattern) {
        this.namePattern = versionPattern;
    }

}

package com.tibco.businessworks6.sonar.plugin.check.process;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import org.sonar.check.RuleProperty;

@Rule(key = NumberofServicesCheck.RULE_KEY, name = "Number of Exposed Services Check", priority = Priority.MAJOR, description = "This rule checks the number of exposed services within a process. It is a good design practice to construct not more than 5 services in the same process.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class NumberofServicesCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "NumberOfExposedServices";

    @RuleProperty(
            key = "maxServices",
            type = "INTEGER",
            defaultValue = "5")
    private int maxServices;

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            if (process.getServices() != null) {
                debug(process, "Number of services: " + process.getServices().size());
                if (process.getServices().size() > maxServices) {

                    info(process, "Maximum number of services reached. Violation is raised");
                    Violation violation = new DefaultViolation(getRule(),
                            1,
                            //" has too many services exposed, this reduces the process readablity and is not a good design pattern.
                            l10n.format(LocalizationMessages.SONAR_BW_NUMBER_SERVICES_CHECK_LABEL));
                    processSource.addViolation(violation);
                }
            }
            debug(process, "End: " + this.getClass().getName());
        }
    }

}

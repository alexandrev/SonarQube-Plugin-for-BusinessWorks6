package com.tibco.businessworks6.sonar.plugin.check.process;


import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwActivity;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import java.util.Collection;

@Rule(key = LogOnlyInSubprocessCheck.RULE_KEY, name = "Log Only in Subprocess Check", priority = Priority.MAJOR, description = "If there is logging or auditing required at multiple points in your project, its advised to write logging and auditing code in a sub process and invoke this process from any point where this functionality is required. This rule checks whether LOG activity is used in sub process.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class LogOnlyInSubprocessCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "LogSubprocess";


    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            if (!process.isSubProcess()) {
                Collection<BwActivity> list = process.getFullActivityList();
                for (BwActivity activity : list) {
                    if (activity.getType() != null && activity.getType().equals("bw.generalactivities.log")) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                //"Log activity should be preferrably used in a sub process."
                                l10n.format(LocalizationMessages.SONAR_BW_LOG_ONLY_SUBPROCESS_CHECK_LABEL));
                        processSource.addViolation(violation);
                    }
                }
            }
            debug(process, "End: " + this.getClass().getName());

        }
    }

}

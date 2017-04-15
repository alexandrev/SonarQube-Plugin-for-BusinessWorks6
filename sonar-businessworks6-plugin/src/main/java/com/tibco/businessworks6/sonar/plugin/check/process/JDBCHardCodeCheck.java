package com.tibco.businessworks6.sonar.plugin.check.process;

import java.util.List;

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

@Rule(key = JDBCHardCodeCheck.RULE_KEY, name = "JDBC HardCoded Check", priority = Priority.MAJOR, description = "This rule checks JDBC activities for hardcoded values for fields Timeout and MaxRows. Use Process property or Module property.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class JDBCHardCodeCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "JDBCHardCoded";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            List<BwActivity> activities = process.getFullActivityList();
            for (BwActivity activity : activities) {
                if (activity.getType() != null && activity.getType().contains("bw.jdbc.")) {
                    if (activity.getConfig("maxRows") != null) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                //"Setting in the JDBC activity is assigned a hardcoded value. It should be defined as Process property or Module property."
                                "maxRows " + l10n.format(LocalizationMessages.SONAR_BW_JDBC_HARD_CODE_CHECK_LABEL));
                        processSource.addViolation(violation);
                    }
                    if (activity.getConfig("timeout") != null) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                "timeout " + l10n.format(LocalizationMessages.SONAR_BW_JDBC_HARD_CODE_CHECK_LABEL));
                        processSource.addViolation(violation);
                    }
                }
            }
        }
    }
}

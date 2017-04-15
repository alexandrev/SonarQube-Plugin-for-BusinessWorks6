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

@Rule(key = CheckpointAfterJDBCÇheck.RULE_KEY, name = "Checkpoint after JDBC Query Activity Check", priority = Priority.MAJOR, description = "This rule checks the placement of a Checkpoint activity within a process. Do not place checkpoint after or in a parallel flow of Query activities or idempotent activities. Database operations such as Update, Insert and Delete are considered non-idempotent operations. You should always place a checkpoint immediately after any database insert or update activity to persist the response. However, for queries, there is no need to place checkpoints")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class CheckpointAfterJDBCÇheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "CheckpointAfterJDBCÇheck";
 
    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            Collection<BwActivity> checkpointList = process.getActivityFromType("bw.internal.checkpoint");
            if (checkpointList != null) {
                for (BwActivity activity : checkpointList) {
                    if (activity.getPreviousActivities() != null) {
                        check(activity, processSource);
                    }
                }
            }
        }
    }

    private void check(BwActivity activity, ProcessSource processSource) {
        for (BwActivity prev : activity.getPreviousActivities()) {
            if (prev.getType() != null &&  prev.getType().contains("bw.jdbc.JDBCQuery")) {
                Violation violation = new DefaultViolation(getRule(),
                        activity.getLine(),
                        //"The process has a Checkpoint activity placed after a JDBC Query activity."
                        l10n.format(LocalizationMessages.SONAR_BW_CHECKPOINT_AFTER_JDBC_CHECK_LABEL));
                processSource.addViolation(violation);
                return;
            }
            if (prev.getPreviousActivities() != null && prev.getPreviousActivities().size() > 0) {
                for (BwActivity old : prev.getPreviousActivities()) {
                    check(old, processSource);
                }
            }
        }

    }
}

package com.tibco.businessworks6.sonar.plugin.check.process;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwActivity;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import java.util.Collection;

@Rule(key = CheckpointAfterHttpCheck.RULE_KEY, name = "Checkpoint after HTTP Activities Check", priority = Priority.CRITICAL, description = "This rule checks the placement of a Checkpoint activity within a process. When placing your checkpoint in a process, be careful with certain types of process starters or incoming events, so that a recovered process instance does not attempt to access resources that no longer exist. For example, consider a process with an HTTP process starter that takes a checkpoint after receiving a request but before sending a response. In this case, when the engine restarts after a crash, the recovered process instance cannot respond to the request since the HTTP socket is already closed. As a best practice, do not place Checkpoint activity right after or in parallel path to HTTP activities.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class CheckpointAfterHttpCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "CheckpointAfterHttpCheck";

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
            if (prev.getType() != null && prev.getType().contains("bw.http.")) {
                Violation violation = new DefaultViolation(getRule(),
                        activity.getLine(),
                        //"The process has a Checkpoint activity placed after HTTP activity or in a parallel flow to a HTTP activity."
                        l10n.format(LocalizationMessages.SONAR_BW_CHECKPOINT_AFTER_HTTP_CHECK_LABEL));
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

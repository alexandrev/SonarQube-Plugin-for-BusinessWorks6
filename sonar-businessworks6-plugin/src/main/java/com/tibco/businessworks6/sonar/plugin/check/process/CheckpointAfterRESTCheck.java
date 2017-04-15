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
import org.apache.log4j.Logger;

@Rule(key = CheckpointAfterRESTCheck.RULE_KEY, name = "Checkpoint after REST Webservice Call Check", priority = Priority.MAJOR, description = "This rule checks the placement of a Checkpoint activity within a process. Do not place checkpoint after or in a parallel flow of REST webservice call.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class CheckpointAfterRESTCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "CheckpointAfterRESTCheck";

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
            if (prev.getType() != null &&  prev.getType().contains("bw.restjson.Rest")) {
                Violation violation = new DefaultViolation(getRule(),
                        activity.getLine(),
                        //"The process has a Checkpoint activity placed after a REST webservice call or in a parallel flow to a REST webservice call."
                        l10n.format(LocalizationMessages.SONAR_BW_CHECKPOINT_AFTER_REST_CHECK_LABEL));
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

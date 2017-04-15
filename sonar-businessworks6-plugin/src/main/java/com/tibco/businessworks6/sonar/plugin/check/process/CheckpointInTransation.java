package com.tibco.businessworks6.sonar.plugin.check.process;

import java.util.List;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.BwActivity;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwGroup;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;

@Rule(key = CheckpointInTransation.RULE_KEY, name = "Checkpoint inside Transaction Group Check", priority = Priority.CRITICAL, description = "This rule checks the placement of a Checkpoint activity within a process. Do not place checkpoint within or in parallel to a Transaction Group or a Critical Section Group. Checkpoint activities should be placed at points that are guaranteed to be reached before or after the transaction group is reached.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class CheckpointInTransation extends AbstractProcessCheck {
    
    public static final String RULE_KEY = "CheckpointInTransation";

    
    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            List<BwGroup> groups = process.getGroups();
            for (BwGroup group : groups) {
                if ("localTX".equals(group.getType()) || "critical".equals(group.getType())) {
                    for (BwActivity activity : process.getActivityList()) {
                        if (activity.getType() != null && activity.getType().equals("bw.internal.checkpoint")) {
                            DefaultViolation violation = new DefaultViolation(getRule(),
                                    activity.getLine(),
                                    //"Checkpoint should not be placed within or in parallel flow to a transaction or critical section."
                                    l10n.format(LocalizationMessages.SONAR_BW_CHECKPOINT_AFTER_TRANSACTION_CHECK_LABEL));
                            processSource.addViolation(violation);
                            
                        }
                    }
                }
            }
        }
    }
}



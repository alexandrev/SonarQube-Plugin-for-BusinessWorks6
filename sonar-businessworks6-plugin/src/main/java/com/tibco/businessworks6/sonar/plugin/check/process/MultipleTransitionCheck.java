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

@Rule(key = MultipleTransitionCheck.RULE_KEY, name = "Multiple Transitions Check", priority = Priority.MAJOR, description = "EMPTY activity should be used if you want to join multiple transition flows. For example, there are multiple transitions out of an activity and each transition takes a different path in the process. In this scenario you can create a transition from the activity at the end of each path to an Empty activity to resume a single flow of execution in the process. This rule checks whether multiple transitions from an activity in a parallel flow merge into EMPTY activity")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class MultipleTransitionCheck extends AbstractProcessCheck {
    
    public static final String RULE_KEY = "MultipleTransitions";
    
    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            Collection<BwActivity> activityList = process.getFullActivityList();
            if (activityList != null) {
                for (BwActivity t : activityList) {
                    int incomingLinks = t.getPreviousActivities().size();
                    if (incomingLinks > 1 && t.getType() != null) {
                        Violation violation = new DefaultViolation(getRule(),
                                t.getLine(),
                                //"There are multiple transitions converging into activity. "
                                l10n.format(LocalizationMessages.SONAR_BW_MULTIPLE_TRANSITION_CHECK_LABEL));
                        processSource.addViolation(violation);
                    }
                }
            }
            
            debug(process, "End: " + this.getClass().getName());
            
        }
        
    }
    
}

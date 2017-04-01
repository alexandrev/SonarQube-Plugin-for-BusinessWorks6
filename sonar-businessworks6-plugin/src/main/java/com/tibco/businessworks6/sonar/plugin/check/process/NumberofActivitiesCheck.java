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

@Rule(key = NumberofActivitiesCheck.RULE_KEY, name = "Number of Activities Check", priority = Priority.MINOR, description = "This rule checks the number of activities within a process, too many activities reduces the process readability")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MINOR)
public class NumberofActivitiesCheck extends AbstractProcessCheck {

    @RuleProperty(
            key = "maxActivities",
            type = "INTEGER",
            defaultValue = "25")
    private int maxActivities;

    public static final String RULE_KEY = "NumberOfActivities";
    

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process,"Start: "+this.getClass().getName());
            int activityCount = process.getFullActivityList().size();
            debug(process,"Number of activities: " + activityCount);
            if (activityCount > maxActivities) {
                info(process,"Maximum of activities reached. Violation raised");
                Violation violation = new DefaultViolation(getRule(),
                        1,
                        //"Process has too many activities reducing process readablity"
                        l10n.format(LocalizationMessages.SONAR_BW_NUMBER_ACTIVITIES_CHECK_LABEL));
                processSource.addViolation(violation);
            }
            debug(process,"End: "+this.getClass().getName());
        }
    }
}

package com.tibco.businessworks6.sonar.plugin.check.process;

import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.BwGroup;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import java.util.Collection;
import org.apache.log4j.Logger;

@Rule(key = ForEachGroupCheck.RULE_KEY, name = "For-Each Group Check", priority = Priority.INFO, description = "This rule checks the ForEach group. It is recommended to use For-Each activity input mapping instead of using For-Each/Iteration Group wherever possible. Do not use iteration groups just for mapping repeating elements.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.INFO)
public class ForEachGroupCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ForEachGroup";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            Collection<BwGroup> groups = process.getGroupList();
            for (BwGroup group : groups) {
                if ("for-each".equals(group.getType())) {
                    //TODO Check if only mapper inside
                    Violation violation = new DefaultViolation(getRule(),
                            group.getLine(),
                            "For-Each group is used in process.");
                    processSource.addViolation(violation);
                }
            }
        }
    }
}

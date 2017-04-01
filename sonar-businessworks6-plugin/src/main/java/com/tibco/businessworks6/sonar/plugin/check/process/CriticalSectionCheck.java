package com.tibco.businessworks6.sonar.plugin.check.process;

import com.google.common.collect.ImmutableList;
import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwActivity;
import com.tibco.businessworks6.sonar.plugin.data.model.BwGroup;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import java.util.Collection;
import java.util.List;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(key = "CriticalSection", name = "Activities in Critical Section Check", priority = Priority.CRITICAL, description = "Critical section groups cause multiple concurrently running process instances to wait for one process instance to execute the activities in the group. As a result, there may be performance implications when using these groups. This rules checks that the Critical Section group does not include any activities that wait for incoming events or have long durations, such as Request/Reply activities, Wait For (Signal-In) activities, Sleep activity, or other activities that require a long time to execute.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class CriticalSectionCheck
        extends AbstractProcessCheck {

    public static final String RULE_KEY = "CriticalSection";
    public static final ImmutableList<String> CONSTANTS = ImmutableList.of("bw.http.waitForHTTPRequest", "bw.file.wait", "bw.generalactivities.sleep", "bw.jms.signalin", "bw.rv.waitforRVMessage", "bw.tcp.waitfortcp", "bw.http.sendHTTPRequest", "bw.ftl.requestreply", "bw.jms.requestreply", "bw.rv.sendRVRequest");

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        List<BwGroup> groups = (List<BwGroup>) process.getGroups();
        for (final BwGroup group : groups) {
            if (group.getType().equals("critical")) {
                Collection<BwActivity> activities = group.getActivityList();
                if (activities != null) {
                    for (BwActivity activity : activities) {
                        if (activity.getType() != null && CriticalSectionCheck.CONSTANTS.contains((Object) activity.getType())) {
                            final Violation violation = new DefaultViolation(
                                    this.getRule(),
                                    activity.getLine(),
                                    "Activity should not be used within Critical Section group.");
                            processSource.addViolation(violation);
                        }
                    }
                }
            }
        }

    }
}


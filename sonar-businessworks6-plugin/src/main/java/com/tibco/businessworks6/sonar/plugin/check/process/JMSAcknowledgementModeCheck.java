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

@Rule(key = JMSAcknowledgementModeCheck.RULE_KEY, name = "JMS Acknowledgement Mode Check", priority = Priority.INFO, description = "This rule checks the acknowledgement mode used in JMS activities. Avoid using Auto Acknowledgement to minimize the risk of data loss.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.INFO)
public class JMSAcknowledgementModeCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "JMSAcknowledgementMode";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            List<BwActivity> activities = process.getFullActivityList();
            for (BwActivity activity : activities) {
                if (activity.getType() != null &&  activity.getType().contains("bw.jms.getmsg")) {
                    if (activity.getConfig("ackMode") == null || "".equals(activity.getConfig("ackMode"))) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                //"Auto Acknowledgement mode is set in the JMS activity."
                                l10n.format(LocalizationMessages.SONAR_BW_JMS_AUTOACK_CHECK_LABEL)
                        );
                        processSource.addViolation(violation);
                    }
                }
            }
        }
    }
}
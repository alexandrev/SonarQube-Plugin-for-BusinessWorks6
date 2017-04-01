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
import com.tibco.businessworks6.sonar.plugin.data.model.BwConfigurationParameter;

@Rule(key = JMSHardCodeCheck.RULE_KEY, name = "JMS HardCoded Check", priority = Priority.MAJOR, description = "This rule checks JMS activities for hardcoded values for fields Timeout, Destinaton, Reply to Destination, Message Selector, Polling Interval. Use Process property or Module property.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class JMSHardCodeCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "JMSHardCoded";

    @Override
    protected void validate(ProcessSource processSource) {
        com.tibco.businessworks6.sonar.plugin.data.model.BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            List<BwActivity> activities = process.getFullActivityList();
            for (BwActivity activity : activities) {
                if (activity.getType() != null &&  activity.getType().contains("bw.jms.")) {
                    for (BwConfigurationParameter param : activity.getConfig()) {
                        if ("destinationName".equals(param.getName()) && param.getValue() != null) {
                            Violation violation = new DefaultViolation(getRule(),
                                    activity.getLine(),
                                    "The Destination Name setting in the JMS activity is assigned a hardcoded value. It should be defined as Process property or Module property.");
                            processSource.addViolation(violation);
                        }
                        if (("replyToDestination".equals(param.getName())) && param.getValue() != null) {
                            {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Reply to Destination setting in the JMS activity  is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }
                            if (("nullreplyToQueue".equals(param.getName())) && param.getValue() != null) {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Reply to Destination setting in the JMS activity  is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }
                            if ("maxSessions".equals(param.getName()) && param.getValue() != null) {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Max Sessions setting in the JMS activity is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }

                            if ("destinationName".equals(param.getName()) && param.getValue() != null) {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Destination Name setting in the JMS Event Source activity  is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }
                            if ("messageSelector".equals(param.getName()) && param.getValue() != null) {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Message Selector setting in the JMS Event Source activity is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }
                            if ("receiveTimeout".equals(param.getName()) && param.getValue() != null) {
                                Violation violation = new DefaultViolation(getRule(),
                                        activity.getLine(),
                                        "The Polling Interval setting in the JMS Event Source activity is assigned a hardcoded value. It should be defined as Process property or Module property.");
                                processSource.addViolation(violation);
                            }
                        }
                    }

                }
            }

            debug(process, "End: " + this.getClass().getName());

        }
    }

}

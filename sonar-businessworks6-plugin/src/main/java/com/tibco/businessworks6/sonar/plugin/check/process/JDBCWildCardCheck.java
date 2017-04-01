package com.tibco.businessworks6.sonar.plugin.check.process;

import java.util.Iterator;
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

@Rule(key = JDBCWildCardCheck.RULE_KEY, name = "JDBC WildCard Check", priority = Priority.MAJOR, description = "This rule checks whether JDBC activities are using wildcards in the query. As a good coding practice, never use wildcards in JDBC queries.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class JDBCWildCardCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "JDBCWildcards";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            List<BwActivity> activities = process.getFullActivityList();
            if (activities != null) {
                for (BwActivity activity : activities) {
                    boolean raise = false;
                    if (activity.getType() != null && activity.getType().contains("jdbc")) {
                        if (activity.getConfig("sqlStatement") != null && activity.getConfig("sqlStatement").contains("*")) {
                            raise = true;
                        }
                        if (activity.getBinding("expression") != null && activity.getBinding("expression").contains("*")) {
                            raise = true;
                        }
                    }
                    if (raise) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                "WildCards should not be used in a JDBC Query. Use correct colomn names in JDBC query for activity " + activity.getName() + " from process " + processSource.getProcessModel().getName());
                        processSource.addViolation(violation);
                    }
                }
            }

        }
        

    }
}

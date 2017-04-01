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

@Rule(key = ChoiceOtherwiseCheck.RULE_KEY, name = "Choice Condition with No Otherwise Check", priority = Priority.MAJOR, description = "This rule checks all activities input mapping for choice statement. As a coding best practice, the choice statement should always include the option otherwise.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class ChoiceOtherwiseCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ChoiceWithNoOtherwise";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            List<BwActivity> activities = process.getFullActivityList();
            for (BwActivity activity : activities) {
                String expr = activity.getConfig("expression");
                if (expr != null) {
                    if (expr.contains("xsl:choose") && !expr.contains("xsl:otherwise")) {
                        Violation violation = new DefaultViolation(getRule(),
                                activity.getLine(),
                                //"Choice statement does not include the option otherwise"
                                l10n.format(LocalizationMessages.SONAR_BW_CHOICE_NO_OTHERWISE_CHECK_LABEL)
                        );
                        processSource.addViolation(violation);
                    }
                }
            }
            debug(process, "End: " + this.getClass().getName());

        }
    }
}

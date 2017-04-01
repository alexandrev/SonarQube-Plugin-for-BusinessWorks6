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
import com.tibco.businessworks6.sonar.plugin.data.model.BwTransition;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import java.util.Collection;

@Rule(key = TransitionLabelCheck.RULE_KEY, name = "Transition Labels Check", priority = Priority.MAJOR, description = "This rule checks whether the transitions with the type 'Success With Condition' (XPath) have a proper label. This will improve code readability")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class TransitionLabelCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "TransitionLabels";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start: " + this.getClass().getName());
            Collection<BwTransition> transitions = process.getTransitionList();
            for (BwTransition t : transitions) {
                if ("SUCCESSWITHCONDITION".equals(t.getType()) && (t.getDescription() == null || "".equals(t.getDescription().trim()))) {
                    info(process, "The transition doesn't have a proper label");
                    Violation violation = new DefaultViolation(getRule(),
                            t.getLineNumber(),
                            //"The transition doesn't have a proper label"
                            l10n.format(LocalizationMessages.SONAR_BW_TRANSITION_XPATH_NO_DESCRIPTION_TEXT_ISSUE)
                            );
                    processSource.addViolation(violation);
                }
            }
            debug(process, "End: " + this.getClass().getName());
        }
    }
}

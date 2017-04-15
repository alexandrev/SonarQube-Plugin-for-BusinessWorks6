package com.tibco.businessworks6.sonar.plugin.check.process;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.data.model.BwTransition;
import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(
        name = "TransitionsConditionalWithoutOtherwiseCheck",
        description = "TransitionsConditionalWithoutOtherwiseCheck",
        key = BwTransitionXPathNoOtherwiseCheck.RULE_KEY,
        priority = Priority.MAJOR)

@BelongsToProfile(
        title = ProcessSonarWayProfile.defaultProfileName,
        priority = Priority.MAJOR)

public class BwTransitionXPathNoOtherwiseCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "TransitionsConditionalWithoutOtherwiseCheck";
    private static final int TECHNICAL_DEBT_MINUTES = 10;

    @Override
    public void validate(ProcessSource resource) {
        LOG.debug("Started rule: " + this.getClass());
        BwProcess process = resource.getProcessModel();
        if (process != null) {
            for (BwTransition transition : process.getTransitionList()) {
                boolean otherwise = false;
                boolean xpath = false;
                if ("SUCCESSWITHCONDITION".equals(transition.getType())) {
                    xpath = true;
                } else if ("SUCCESSWITHNOCONDITION".equals(transition.getType())) {
                    otherwise = true;
                }

                if (xpath && !otherwise) {
                    addError(transition.getLineNumber(),
                            l10n.format(LocalizationMessages.SONAR_BW_TRANSITION_XPATH_NO_OTHERWISE_TEXT_ISSUE),
                            resource);
                }
            }

        }
        LOG.debug("Finisehd rule: " + this.getClass());
    }

    public double getTechnicalDebt() {
        return TECHNICAL_DEBT_MINUTES;
    }
}

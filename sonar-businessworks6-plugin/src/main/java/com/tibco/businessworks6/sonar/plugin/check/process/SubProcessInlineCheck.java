package com.tibco.businessworks6.sonar.plugin.check.process;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.data.model.BwService;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import java.util.Map;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(key = "SubProcessInlineCheck", name = "Data Availability to Inline SubProcess Check", priority = Priority.INFO, description = "This rule checks if there is large set of data being passed everytime to Inline SubProcess. Use of Job Shared Variable is recommended in this scenario to increase performance.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.INFO)
public class SubProcessInlineCheck
        extends AbstractProcessCheck {

    public static final String RULE_KEY = "SubProcessInlineCheck";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process, "Start " + this.getClass().getName());
            Map<String, BwService> referenceServices = process.getProcessReferenceServices();
            for (String referenceService : referenceServices.keySet()) {
                BwService serv = (BwService) referenceServices.get(referenceService);
                if (serv.getInline().equals("true")) {
                    debug(process,"Reference Inline Detected: "+ serv.getName());
                    Violation violation = new DefaultViolation(getRule(),
                            1,
                            //"It is recommended to use Job Shared Variable instead of passing a large set of data"
                            l10n.format(LocalizationMessages.SONAR_BW_SUBPROCESS_IN_LINE_CHECK_LABEL));
                    processSource.addViolation(violation);
                }
            }
            debug(process, "End " + this.getClass().getName());
        }
    }
}

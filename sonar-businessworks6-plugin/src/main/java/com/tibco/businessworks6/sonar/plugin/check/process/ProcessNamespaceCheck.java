package com.tibco.businessworks6.sonar.plugin.check.process;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProcessCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@Rule(
        name = "ProcessNamespaceCheck",
        description = "ProcessNamespaceCheck",
        key = ProcessNamespaceCheck.RULE_KEY,
        priority = Priority.MINOR)

@BelongsToProfile(
        title = ProcessSonarWayProfile.defaultProfileName,
        priority = Priority.MINOR)

public class ProcessNamespaceCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ProcessNamespaceCheck";

    @Override
    public void validate(ProcessSource resource) {

        BwProcess process = (BwProcess) resource.getProcessModel();
        if (process != null) {
            debug(process, "Started rule: " + this.getClass());
            NamedNodeMap nodeMap = process.getNameNodeMap();
            if (nodeMap != null) {
                int size = nodeMap.getLength();
                for (int i = 0; i < size; i++) {
                    Node node = nodeMap.item(i);
                    if (node != null) {
                        String value = node.getNodeValue();
                        if (value != null && !"".equals(value)) {
                            if (value.contains("example.com")) {
                                LOG.warn("Default namespace used: " + value);
                                addError(1, l10n.format(LocalizationMessages.SONAR_BW_PROJECT_NAME_TEXT_ISSUE) + value, resource);

                            }
                        }
                    }
                }

            }
            debug(process, "Finished rule: " + this.getClass());
        }

    }

}

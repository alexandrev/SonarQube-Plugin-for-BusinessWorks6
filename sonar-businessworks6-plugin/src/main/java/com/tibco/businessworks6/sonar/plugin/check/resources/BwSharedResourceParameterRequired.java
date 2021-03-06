package com.tibco.businessworks6.sonar.plugin.check.resources;

import com.tibco.businessworks6.sonar.plugin.check.AbstractResourceCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ResourcesSource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import com.tibco.businessworks6.sonar.plugin.data.model.ResourceParameter;
import org.apache.log4j.Logger;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(key = BwSharedResourceParameterRequired.RULE_KEY, name = "Resource parameter required", description = "Resource parameter required",priority = Priority.MINOR)
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class BwSharedResourceParameterRequired extends AbstractResourceCheck {

    public static final String RULE_KEY = "BwSharedResourceParameterRequired";
    private static final Logger LOG = Logger.getLogger(BwSharedResourceParameterRequired.class);

    @Override
    public void validate(ResourcesSource resourceXml) {
        LOG.debug("Started rule: " + this.getClass());
        BwSharedResource resource = resourceXml.getResourceModel();
        LOG.debug("Checking project files");
        for (ResourceParameter parameter : resource.getParameters()) {
            if (parameter != null) {
                String value = parameter.getValue();
                if (parameter.isRequired() && !(value != null && !value.isEmpty())) {
                    addError(1, l10n.format(LocalizationMessages.SONAR_BW_SHARED_RESOURCE_NOT_FILLED_TEXT_ISSUE) + parameter.getName(), resourceXml);
                }
            }
        }

        LOG.debug("Finished rule: " + this.getClass());
    }

}

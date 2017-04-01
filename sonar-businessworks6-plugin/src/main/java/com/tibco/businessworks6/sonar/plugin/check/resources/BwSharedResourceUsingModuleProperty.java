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


@Rule(
        key = BwSharedResourceUsingModuleProperty.RULE_KEY,
        name =  "Parameter Resource using Module Property",
        description = "Parameter Resource using Module Property",
        priority = Priority.MINOR)

@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)

public class BwSharedResourceUsingModuleProperty extends AbstractResourceCheck{

    public static final String RULE_KEY = "BwSharedResourceUsingModuleProperty";
    private static final Logger LOG = Logger.getLogger(BwSharedResourceUsingModuleProperty.class);

    @Override
     public void validate(ResourcesSource resourceXml) {
        LOG.debug("Started rule: " + this.getClass());
            BwSharedResource resource =  resourceXml.getResourceModel();
            LOG.debug("Checking project files");
            for(ResourceParameter parameter : resource.getParameters()){
                if(parameter != null){
                    if(parameter.isBinding() && !parameter.isGlobalVariable()){
                        addError(1,l10n.format(LocalizationMessages.SONAR_BW_SHARED_RESOURCE_NOT_USING_GV_TEXT_ISSUE) + parameter.getName(), resourceXml);
                    }
                }
            }
        
        LOG.debug("Finished rule: " + this.getClass());
    }

}

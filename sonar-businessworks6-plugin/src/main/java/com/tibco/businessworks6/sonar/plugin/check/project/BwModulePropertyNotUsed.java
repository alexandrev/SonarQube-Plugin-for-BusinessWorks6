package com.tibco.businessworks6.sonar.plugin.check.project;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProjectCheck;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;
import com.tibco.businessworks6.sonar.plugin.source.ProjectSource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwModuleProperty;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcessProperty;
import com.tibco.businessworks6.sonar.plugin.data.model.ResourceParameter;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;


@Rule(
        name = "Module Property not used",
        description = "Module Property not used",
        key = BwModulePropertyNotUsed.RULE_KEY,
        priority = Priority.MINOR)

@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.CRITICAL)
public class BwModulePropertyNotUsed extends AbstractProjectCheck{

    public static final String RULE_KEY = "BwModulePropertyNotUsed";
  
    private static final Logger LOG = Logger.getLogger(BwModulePropertyNotUsed.class);    

    private static final String[] EXCEPTION_ARRAY = {"BW.APPNODE.NAME","BW.DOMAIN.NAME","BW.HOST.NAME",
        "BW.MODULE.VERSION","BW.DEPLOYMENTUNIT.NAME","BW.DEPLOYMENTUNIT.VERSION","BW.APPSPACE.NAME", 
        "BW.DEPLOYMENTUNIT.TYPE","BW.MODULE.NAME" };


    @RuleProperty(
            key = "exceptionExtraString",
            type = "TEXT",
            defaultValue = "")
    private String exceptionExtraString;

    private final List<String> exceptionList;

    public BwModulePropertyNotUsed() {
        exceptionList = new ArrayList<String>(Arrays.asList(EXCEPTION_ARRAY));
        LOG.debug("ExceptionList: " + StringUtils.join(exceptionList.toArray(), ","));
    }

    @Override
    public void validate(ProjectSource xmlSourceCode) {
        LOG.debug("Started rule: " + this.getClass());
        BwProject project = xmlSourceCode.getResourceModel();
        if(exceptionExtraString != null){
            List<String> extraCollection = Arrays.asList(exceptionExtraString.split(","));
            if(extraCollection != null && !extraCollection.isEmpty()){
                exceptionList.addAll(extraCollection);
            }
        }


        LOG.debug("Analyzing Module Properties");
        for (BwModuleProperty var : project.getModulProperties()) {
            if (!excludeGlobalVariable(var)) {
                String variableName = var.getName();
                LOG.debug("Module Property name: " + variableName);

                if (variableName != null) {
                    List<BwSharedResource> resources = project.getSharedResourcesList();
                    boolean founded = false;
                    LOG.debug("Checking Resources");
                    for (BwSharedResource sharedResource : resources) {
                        LOG.debug("Checking Resource: " + sharedResource.getName());
                        for(ResourceParameter parameter : sharedResource.getParameters()){
                            if(parameter.isGlobalVariable()){
                                String value = parameter.getValue();
                                if(value != null && value.equals(variableName)){
                                    founded = true;
                                }
                            }
                        }
                    }
                    
                    for(com.tibco.businessworks6.sonar.plugin.data.model.BwProcess process :  project.getProcess()){
                        for(BwProcessProperty processVar : process.getProcessProperty()){
                            String value = processVar.getValue();
                            if(value != null && value.equals(variableName)){
                                founded = true;
                            }
                        }
                    }
                    if (!founded) {
                        LOG.warn("Reference to Module Property: " + var.getName() + " not found");
                        addError(1,l10n.format(LocalizationMessages.SONAR_BW_MODULE_PROPERTY_NOT_USED_TEXT_ISSUE) + var.getName(),xmlSourceCode);
                    }
                }
            }
        }
        LOG.debug("Finished rule: " + this.getClass());
    }

    public void setExceptionExtraString(String exceptionExtraString) {
        this.exceptionExtraString = exceptionExtraString;

        if (exceptionExtraString != null) {
            exceptionList.addAll(Arrays.asList(exceptionExtraString.split("\\s*,\\s*")));
        }

        LOG.debug("ExceptionList Updated: " + StringUtils.join(exceptionList.toArray(), ","));
    }

    private boolean excludeGlobalVariable(BwModuleProperty var) {
        boolean out = false;
        if(var != null){
            for(String pattern :  exceptionList){
                Pattern patt = Pattern.compile(pattern);
                Matcher m = patt.matcher(var.getName());
                if(m.matches()){
                    return true;
                }     
            }
        }
        return out;
    }
    
}

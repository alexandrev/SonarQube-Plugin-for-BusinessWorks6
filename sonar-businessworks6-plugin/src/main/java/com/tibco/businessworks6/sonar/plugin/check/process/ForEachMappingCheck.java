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

@Rule(key = ForEachMappingCheck.RULE_KEY, name="For-Each Mapping Check", priority = Priority.INFO, description = "This rule checks the Input mappings of activities. In activity Input mapping for performance reasons, it is recommended ato use Copy-Of instead of For-Each whenever possible.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.INFO)
public class ForEachMappingCheck extends AbstractProcessCheck {
	public static final String RULE_KEY = "ForEachMapping";
     
        
        
	@Override
	protected void validate(ProcessSource processSource) {
            BwProcess process = processSource.getProcessModel();
            if(process != null){
                List<BwActivity> activities = process.getFullActivityList();
                for(BwActivity activity : activities){
                    if(activity.getBinding("expression") != null && activity.getBinding("expression").contains("xsl:for-each")){
                        //TODO Check if the namespaces are the same
				Violation violation = new DefaultViolation(getRule(),
						activity.getLine(),
						"For-Each is used in the Input mapping of activity.");
				processSource.addViolation(violation);
			}
                }
            }
		
	}

}

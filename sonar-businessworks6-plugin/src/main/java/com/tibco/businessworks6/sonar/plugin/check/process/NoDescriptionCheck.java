/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
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
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationMessages;

@Rule(key = NoDescriptionCheck.RULE_KEY, name = "No Process Description Check", priority = Priority.MINOR, description = "This rule checks if there is description specified for a process.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MINOR)
public class NoDescriptionCheck extends AbstractProcessCheck {

    public static final String RULE_KEY = "ProcessNoDescription";
    public static final String DESCRIPTION_ELEMENT_NAME = "documentation";
    public static final String DESCRIPTION_ELEMENT_NAMESPACE = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

    @Override
    protected void validate(ProcessSource processSource) {
        BwProcess process = processSource.getProcessModel();
        if (process != null) {
            debug(process,"Start: "+this.getClass().getName());
            String description = process.getDescription();
            if (description == null
                    || description.isEmpty()) {
                Violation violation = new DefaultViolation(getRule(),
                        process.getLine(),
                        //"Empty description for this process"
                        l10n.format(LocalizationMessages.SONAR_BW_NO_DESCRIPTION_CHECK_LABEL));
                processSource.addViolation(violation);
            }
            debug(process,"End: "+this.getClass().getName());
            
        }
    }

}

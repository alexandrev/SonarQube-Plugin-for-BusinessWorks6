/*
 * SonarQube XML Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tibco.businessworks6.sonar.plugin.check;

import com.tibco.businessworks6.sonar.plugin.check.process.NumberofActivitiesCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.Loggable;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationService;
import com.tibco.businessworks6.sonar.plugin.services.l10n.impl.LocalizationServiceImpl;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;

import com.tibco.businessworks6.sonar.plugin.source.Source;
import com.tibco.businessworks6.sonar.plugin.violation.DefaultViolation;
import com.tibco.businessworks6.sonar.plugin.violation.Violation;
import org.apache.log4j.Logger;

/**
 * Abtract superclass for checks.
 *
 * @author Kapil Shivarkar
 */
public abstract class AbstractCheck {

    protected LocalizationService l10n = LocalizationServiceImpl.getInstance();
    protected Logger LOG = Logger.getLogger(this.getClass());
    protected Rule rule;
    protected RuleKey ruleKey;

    public RuleKey getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(RuleKey ruleKey) {
        this.ruleKey = ruleKey;
    }

    public final void setRule(Rule rule) {
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }

    public abstract <S extends Source> S validate(S source);

    public void addError(int lineNumber, String message, Source source) {
        Violation violation = new DefaultViolation(getRule(),
                lineNumber,
                message);
        source.addViolation(violation);
    }
    
    public void debug(Loggable obj,String message){
        LOG.debug("["+obj.getID()+"] " + message);
    }
    
    public void info(Loggable obj,String message){
        LOG.info("["+obj.getID()+"] " + message);
    }
    

}

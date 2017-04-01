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

import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.source.ProjectSource;
import com.tibco.businessworks6.sonar.plugin.source.ResourcesSource;
import com.tibco.businessworks6.sonar.plugin.source.Source;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssue;

/**
 * Abtract superclass for checks.
 *
 * @author Kapil Shivarkar
 */
public abstract class AbstractProjectCheck extends AbstractCheck{
	
	protected abstract void validate(ProjectSource processSource);

	@Override
	public <S extends Source> S validate(S source) {
		if(source instanceof ProjectSource){
			validate((ProjectSource)source);
		}
		return source;
	}
        

}

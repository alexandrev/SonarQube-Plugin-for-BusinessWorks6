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
package com.tibco.businessworks6.sonar.plugin.sensor;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.resources.Project;
//import org.sonar.api.scan.filesystem.FileQuery;
//import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.config.Settings;

/**
 * XmlSensor provides analysis of xml files.
 * 
 * @author Kapil Shivarkar
 */
public abstract class AbstractMetricSensor extends AbstractSensor {
	
	protected AbstractMetricSensor(Settings settings,FileSystem fileSystem,
			ResourcePerspectives resourcePerspectives, String languageKey, CheckFactory checkFactory) {
		super(settings,fileSystem, resourcePerspectives, languageKey, checkFactory);
	}

	/**
	 * Analyze the XML files.
	 */
	public void analyse(Project project, SensorContext sensorContext) {
		for (java.io.File file : fileSystem.files(fileSystem.predicates().hasLanguage(languageKey))) {
			try {
				analyseFile(file);
			} catch (Exception e) {
				log().error(
						"Could not analyze the file " + file.getAbsolutePath(),
						e);
			}
		}
	}

}

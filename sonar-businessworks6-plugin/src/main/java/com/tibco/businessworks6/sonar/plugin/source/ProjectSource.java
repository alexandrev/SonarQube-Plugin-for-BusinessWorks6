package com.tibco.businessworks6.sonar.plugin.source;

import java.io.File;
import java.nio.charset.Charset;
import com.tibco.businessworks6.sonar.plugin.file.XmlFile;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;

/**
 * Checks and analyzes report measurements, issues and other findings in
 * WebSourceCode.
 * 
 * @author Kapil Shivarkar
 */
public class ProjectSource extends XmlSource {

	private BwProject project;

	public ProjectSource(File file){
		super(file);
		project = BwProject.getInstance();

	}

	public ProjectSource(XmlFile xmlFile) {
		super(xmlFile);
		project = BwProject.getInstance();
	}

	@Override
	public boolean parseSource(Charset charset) {
		boolean result = super.parseSource(charset);
		return result;
	}

	public void setProjectModel(BwProject resource){
		this.project = resource;
	}

	public BwProject getResourceModel(){
		return project;
	}
}

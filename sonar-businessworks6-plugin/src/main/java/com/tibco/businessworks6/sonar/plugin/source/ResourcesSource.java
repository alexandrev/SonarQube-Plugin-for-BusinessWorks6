package com.tibco.businessworks6.sonar.plugin.source;

import java.io.File;
import java.nio.charset.Charset;
import com.tibco.businessworks6.sonar.plugin.file.XmlFile;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import com.tibco.businessworks6.sonar.plugin.data.model.helper.SharedResourceFactory;

/**
 * Checks and analyzes report measurements, issues and other findings in
 * WebSourceCode.
 * 
 * @author Kapil Shivarkar
 */
public class ResourcesSource extends XmlSource {

	private BwSharedResource resource;

	public ResourcesSource(File file){
		super(file);
		this.resource = SharedResourceFactory.getInstance().parse(file);
	}

	public ResourcesSource(XmlFile xmlFile) {
		super(xmlFile);
		this.resource = new BwSharedResource();	
                this.resource.parse(xmlFile.getIOFile());
	}

	/*public ProcessSource(String code) {
		super(code);
		setCode(code);
		this.resource = new Process();
		InputStream is = createInputStream();
		resource.setProcessXmlDocument(new SaxParser().parseDocument(is, true));		
	}*/

	@Override
	public boolean parseSource(Charset charset) {
		boolean result = super.parseSource(charset);
		/*if(result){
			Process proc = resource.setProcessXmlDocument(getDocument(true));
			//proc.parse();
			//proc.myparse();
		}*/
		return result;
	}

	public void setResourceModel(BwSharedResource resource){
		this.resource = resource;
	}

	public BwSharedResource getResourceModel(){
		return resource;
	}
}

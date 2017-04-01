package com.tibco.businessworks6.sonar.plugin.source;

import java.io.File;
import java.nio.charset.Charset;
import com.tibco.businessworks6.sonar.plugin.file.XmlFile;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import com.tibco.businessworks6.sonar.plugin.util.SaxParser;

/**
 * Checks and analyzes report measurements, issues and other findings in
 * WebSourceCode.
 * 
 * @author Kapil Shivarkar
 */
public class ProcessSource extends XmlSource {

	private BwProcess process;

	public ProcessSource(File file){
		super(file);
		this.process = new BwProcess(file);
		process.setProcessXmlDocument(new SaxParser().parseDocument(file, true));	
                process.setSource(this);
	}

	public ProcessSource(XmlFile xmlFile) {
		super(xmlFile);
		this.process = new BwProcess(xmlFile.getIOFile());	
	}

	/*public ProcessSource(String code) {
		super(code);
		setCode(code);
		this.process = new Process();
		InputStream is = createInputStream();
		process.setProcessXmlDocument(new SaxParser().parseDocument(is, true));		
	}*/

	@Override
	public boolean parseSource(Charset charset) {
		boolean result = super.parseSource(charset);
		/*if(result){
			Process proc = process.setProcessXmlDocument(getDocument(true));
			//proc.parse();
			//proc.myparse();
		}*/
		return result;
	}

	public void setProcessModel(BwProcess process){
		this.process = process;
	}

	public BwProcess getProcessModel(){
		return process;
	}
}

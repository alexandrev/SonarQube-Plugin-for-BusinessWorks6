package com.tibco.businessworks6.sonar.plugin;

import java.util.List;
import com.google.common.collect.ImmutableList;


import org.sonar.api.SonarPlugin;

public class BusinessWorksPlugin extends SonarPlugin {
	
	public static final String PROCESS_FILE_SUFFIXES_KEY = "sonar.bw.process.file.suffixes";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
	public List getExtensions() {
		ImmutableList.Builder<Object> builder = ImmutableList.builder();		
		builder.addAll(ProcessExtensions.getExtensions());
		return builder.build();
	}
        
        

}

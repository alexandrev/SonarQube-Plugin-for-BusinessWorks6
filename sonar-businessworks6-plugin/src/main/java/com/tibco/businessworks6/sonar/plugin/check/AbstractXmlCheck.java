/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibco.businessworks6.sonar.plugin.check;

import com.tibco.businessworks6.sonar.plugin.source.Source;
import com.tibco.businessworks6.sonar.plugin.source.XmlSource;

/**
 *
 * @author alexandrev
 */
public abstract class AbstractXmlCheck extends AbstractCheck {

    protected abstract void validate(XmlSource processSource);

    
    @Override
	public <S extends Source> S validate(S source) {
		if(source instanceof XmlSource){
			validate((XmlSource)source);
		}
		return source;
	}
    
}

package com.tibco.businessworks6.sonar.plugin.language;

import com.google.common.collect.Lists;
import org.sonar.api.resources.AbstractLanguage;
import com.tibco.businessworks6.sonar.plugin.BwConstants;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;

/**
 * Defines the language for TIBCO BusinessWorks Plug-in
 *
 */
public class BusinessWorks6Language extends AbstractLanguage {

    public static final BusinessWorks6Language INSTANCE = new BusinessWorks6Language();
    
    protected Settings settings;

    public BusinessWorks6Language() {
        super(BwConstants.LANGUAGE_KEY, BwConstants.LANGUAGE_NAME);
    }
    
    
    public BusinessWorks6Language(Settings settings) {
        super(BwConstants.LANGUAGE_KEY, BwConstants.LANGUAGE_NAME);
        this.settings = settings;
    }


    @Override
    public String[] getFileSuffixes() {
        return new String[]{"substvar", "bwm", "jsv", "msv", "bwp", "*Resource", "xml", "wsdl"};
    }

    public String[] getResourceSuffixes() {
        return new String[]{".*Resource"};
    }

     public String[] getNonResourceSuffixes() {
        return new String[]{"substvar", "bwm", "jsv", "msv", "bwp", "xml", "wsdl"};
    }

    
    protected String getLocale(){
            return settings.getString("plug-in.locale");
        }
	
	protected String[] getFileSuffixes(String fileSuffixesKey, String[] defaultFileSuffixes) {
		String[] suffixes = filterEmptyStrings(settings
				.getStringArray(fileSuffixesKey));
		if (suffixes.length == 0) {
			suffixes = defaultFileSuffixes;
		}
		return suffixes;
	}

	protected String[] filterEmptyStrings(String[] stringArray) {
		List<String> nonEmptyStrings = Lists.newArrayList();
		for (String string : stringArray) {
			if (StringUtils.isNotBlank(string.trim())) {
				nonEmptyStrings.add(string.trim());
			}
		}
		return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
	}
    

}

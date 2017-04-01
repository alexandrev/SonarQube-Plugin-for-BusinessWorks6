
package com.tibco.businessworks6.sonar.plugin.services.l10n;

/**
 *
 * @author Developer
 */
public interface LocalizationService {

    String format(String key);

    void refreshLocale(String locale);
    
}

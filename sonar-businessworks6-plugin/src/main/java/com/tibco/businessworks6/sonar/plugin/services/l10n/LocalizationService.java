
package com.tibco.businessworks6.sonar.plugin.services.l10n;

import java.util.Collection;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

/**
 *
 * @author Developer
 */
public interface LocalizationService {

    String format(String key);

    void refreshLocale(String locale);
    
    String get();
    
    String getLocalizedFolderName(String baseName);

    String getLocalizedFilePath(String orgsonarl10nbw);
    
    void setupExternalNames(Collection<RulesDefinition.NewRule> rules, NewRepository repository);
    
}

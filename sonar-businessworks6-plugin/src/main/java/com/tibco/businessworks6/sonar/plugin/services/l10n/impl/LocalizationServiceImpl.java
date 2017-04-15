package com.tibco.businessworks6.sonar.plugin.services.l10n.impl;

import com.tibco.businessworks6.sonar.plugin.BwConstants;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationService;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewParam;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.server.rule.RulesDefinition.Repository;

public final class LocalizationServiceImpl implements LocalizationService {

    private ResourceBundle resource;

    private static LocalizationServiceImpl instance;

    private static final Logger LOG = Logger.getLogger(LocalizationServiceImpl.class);

    private static final Object LOCK = new Object();

    protected Locale locale;

    private LocalizationServiceImpl() {
        resource = ResourceBundle.getBundle("org.sonar.l10n.bw");
        locale = Locale.getDefault();
    }

    @Override
    public void refreshLocale(String locale) {
        Locale localeObj = null;
        try {
            this.locale = new Locale(locale);
            LOG.debug("Locale object: " + localeObj.toString());
        } catch (Exception ex) {
            LOG.warn("Locate not detected", ex);
            this.locale = Locale.getDefault();
        }

        resource = ResourceBundle.getBundle("org.sonar.l10n.bw", this.locale);
        if (resource != null) {
            LOG.debug("Current locale: " + resource.getLocale().toString());
        }

    }

    public static LocalizationServiceImpl getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new LocalizationServiceImpl();
            }
        }
        return instance;
    }

    @Override
    public String format(String key) {
        String out = key;
        try {
            LOG.debug("Trying to recover the value of the key: " + key);
            out = resource.getString(key);
            LOG.debug("Recovered value: " + out);
        } catch (Exception ex) {
            LOG.warn("Error trying to format the key");
        }
        return out;
    }

    public String printLocale() {
        return resource.getLocale().getLanguage() + " - " + resource.getLocale().getCountry();
    }

    @Override
    public String get() {
        return printLocale();
    }

    public String getLocalizedFolderName(String baseName) {
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);

        String path = control.toBundleName(baseName, locale);
        URL url = LocalizationServiceImpl.class.getResource(path);

        if (url == null) {
            Locale localeWithoutCountry = locale.getCountry() == null ? locale : new Locale(locale.getLanguage());
            path = control.toBundleName(baseName, localeWithoutCountry);
            url = LocalizationServiceImpl.class.getResource(path);

            if (url == null) {
                path = baseName;
                LocalizationServiceImpl.class.getResource(path);
            }
        }
        LOG.info("Localized folder name: " + path);
        return path;
    }

    @Override
    public String getLocalizedFilePath(String baseFilePath) {

        String add = "";
        String language = locale.getLanguage();
        if (language != null && !"en".equals(language.toLowerCase())) {
            add = "_" + language;
        }
        String path = baseFilePath + add + ".properties";
        LOG.info("Localized file name: " + path);
        return path;
    }
    
    public void setupExternalNames(Collection<NewRule> rules, RulesDefinition.NewRepository repository) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.sonar.l10n." + BwConstants.LANGUAGE_KEY, locale);
        for (NewRule rule : rules) {
            String baseKey = "rule." + repository.key() + "." + rule.key();
            String nameKey = baseKey + ".name";
            if (bundle.containsKey(nameKey)) {
                rule.setName(bundle.getString(nameKey));
            }
            for (NewParam param : rule.params()) {
                String paramDescriptionKey = baseKey + ".param." + param.key();
                if (bundle.containsKey(paramDescriptionKey)) {
                    param.setDescription(bundle.getString(paramDescriptionKey));
                }
            }
        }
    }

}

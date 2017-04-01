package com.tibco.businessworks6.sonar.plugin.services.l10n.impl;

import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationService;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public final class LocalizationServiceImpl implements LocalizationService {

    private ResourceBundle resource;

    private static LocalizationServiceImpl instance;

    private static final Logger LOG = Logger.getLogger(LocalizationServiceImpl.class);

    private static final Object LOCK = new Object();

    private LocalizationServiceImpl() {
        resource = ResourceBundle.getBundle("org.sonar.l10n.bw6");
    }
    
    @Override
    public void refreshLocale(String locale) {
        Locale localeObj = null;
        try {
            localeObj = new Locale(locale);
            LOG.debug("Locale object: "+localeObj.toString());
        } catch (Exception ex) {
            LOG.warn("Locate not detected", ex);
            localeObj = Locale.getDefault();
        }

        resource = ResourceBundle.getBundle("org.sonar.l10n.bw6", localeObj);
        if(resource != null){
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
            LOG.debug("Trying to recover the value of the key: "+key);
            out = resource.getString(key);
            LOG.debug("Recovered value: "+out);
        } catch (Exception ex) {
            LOG.warn("Error trying to format the key", ex);
        }
        return out;
    }

    public String printLocale() {
        return resource.getLocale().getLanguage() + " - " + resource.getLocale().getCountry();
    }

}

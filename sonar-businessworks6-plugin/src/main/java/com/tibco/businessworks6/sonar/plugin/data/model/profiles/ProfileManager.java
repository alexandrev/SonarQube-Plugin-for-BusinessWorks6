package com.tibco.businessworks6.sonar.plugin.data.model.profiles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.sonar.plugins.tibco.utils.common.URLClassPathList;



public class ProfileManager {

    private static final Object LOCK = new Object();

    private final Map<String, Profile> profileHash = new HashMap<String, Profile>();

    private static final Logger LOG = Logger.getLogger(ProfileManager.class);

    private static ProfileManager instance;

    public static final String RESOURCES_ROOT = "org/sonar/plugins/tibco/bw/profiles/";
    private static final String RESOURCES_MANDATORY = "qualityProfile.xsd";

    private ProfileManager() {
        loadProfiles();
    }

    public static ProfileManager getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ProfileManager();
            }
        }
        return instance;
    }

    public Profile getProfileByName(String name, String version) {
        if (name != null && version != null) {
            String key = name + "-" + version;
            return profileHash.get(key);
        }
        return null;
    }

    private void loadProfiles() {
        LOG.debug("Loading profiles...");

        List<URL> urlList = URLClassPathList.listResources(RESOURCES_ROOT, RESOURCES_MANDATORY, RESOURCES_ROOT.concat(".*xml"));

        for (URL url : urlList) {
            LOG.debug("Analyzing the file: " + url.toString());
            if (url.getFile().endsWith(".xml")) {
                LOG.debug("Detecting XML profile. Parsing...");
                try {
                    IBindingFactory bfact = BindingDirectory.getFactory(Profile.class);
                    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
                    Profile profile = (Profile) uctx.unmarshalDocument(url.openStream(), null);
                    if (profile != null) {
                        LOG.debug("Profile parsed sucessfully!");
                        String key = profile.getDataName() + "-" + profile.getDataVersion();
                        profileHash.put(key, profile);
                        LOG.debug("Profile added sucessfully!");
                    }
                } catch (JiBXException ex) {
                    LOG.error("Error detected: ", ex);
                } catch (IOException ex) {
                    LOG.error("Error detected: ", ex);
                }
                LOG.debug("Finished XML profile parsing...");
            }
        }

    }

    public Profile getProfileByName(String profileName) {
        if (profileName != null) {
            String key = profileName + "-" + "1.0";
            return profileHash.get(key);
        }
        return null;
    }

    public Profile getProfileByPath(String path) {
        FileInputStream fStream;
        try {
            fStream = new FileInputStream(path);
            IBindingFactory bfact = BindingDirectory.getFactory(Profile.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            return (Profile) uctx.unmarshalDocument(fStream, null);
        } catch (FileNotFoundException ex) {
            LOG.error("Error detected: ", ex);
        } catch (JiBXException ex) {
            LOG.error("Error detected: ", ex);
        }
        return null;
    }

}

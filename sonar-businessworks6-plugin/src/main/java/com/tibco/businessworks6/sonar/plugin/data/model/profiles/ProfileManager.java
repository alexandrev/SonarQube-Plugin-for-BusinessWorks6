package com.tibco.businessworks6.sonar.plugin.data.model.profiles;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    public Profile loadProfiles(String content) {
        Profile out = null;
        LOG.debug("Loading profiles...");
        try {
            IBindingFactory bfact = BindingDirectory.getFactory(Profile.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            Profile profile = (Profile) uctx.unmarshalDocument(stream, null);
            if (profile != null) {
                out = profile;
            }
        } catch (JiBXException ex) {
            LOG.error("Error detected: ", ex);
        }
        LOG.debug("Finished XML profile parsing...");
        return out;
    }
}

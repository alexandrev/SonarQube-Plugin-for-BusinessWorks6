package com.tibco.businessworks6.sonar.plugin.data.model.helper;

import com.tibco.businessworks6.sonar.plugin.data.model.SharedResourceProperties;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;
import com.tibco.businessworks6.sonar.plugin.language.BusinessWorks6Language;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;


public final class SharedResourceFactory {

    private static SharedResourceProperties configuration;

    private static final Logger LOG = Logger.getLogger(SharedResourceFactory.class);
    private static final String SHARED_RESOURCE_PROPERTIES_FILE_PATH = "/org/sonar/plugins/tibco/bw/data/resources/sharedResources.xml";
    private static final String SHARED_RESOURCE_EXTENSION_MAPPER_FILE_PATH = "/org/sonar/plugins/tibco/bw/data/resources/sharedresources.properties";

    private static SharedResourceFactory instance;

    private static final Object LOCK = new Object();

    public static boolean isResource(File inputfile) {
        String ext = FilenameUtils.getExtension(inputfile.getName());
        return !Arrays.asList(BusinessWorks6Language.INSTANCE.getNonResourceSuffixes()).contains(ext);
    }

    private Properties extensionMapper;

    private SharedResourceFactory() {
        init();
        loadProperties();
    }

    public static SharedResourceFactory getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new SharedResourceFactory();
            }
        }
        return instance;
    }

    public void init() {
        LOG.debug("Loading shared resources...");
        URL url = SharedResourceFactory.class.getResource(SHARED_RESOURCE_PROPERTIES_FILE_PATH);
        LOG.debug("Analyzing the file: " + url.toString());
        if (url.getFile().endsWith(".xml")) {
            LOG.debug("Detecting XML shared resource configuration. Parsing...");
            try {
                IBindingFactory bfact = BindingDirectory.getFactory(SharedResourceProperties.class);
                IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
                SharedResourceProperties properties = (SharedResourceProperties) uctx.unmarshalDocument(url.openStream(), null);
                if (properties != null) {
                    LOG.debug("Shared resource properties parsed sucessfully!");
                    configuration = properties;
                }
            } catch (JiBXException ex) {
                LOG.error("Error detected: ", ex);
            } catch (IOException ex) {
                LOG.error("Error detected: ", ex);
            }
            LOG.debug("Finished XML shared resource configuration parsing...");
        }

    }

    public BwSharedResource parse(File file) {
        BwSharedResource out = null;
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.getAbsolutePath());
            out = instanceCustomSharedResource(extension, file);
            out.parse(file);
            out.setProperties(configuration);

        }
        return out;
    }

    private void loadProperties() {
        try {
            this.extensionMapper = new Properties();
            extensionMapper.load(this.getClass().getResourceAsStream(SHARED_RESOURCE_EXTENSION_MAPPER_FILE_PATH));
        } catch (IOException ex) {
            LOG.error("Error loading the properties file for shared resources", ex);
        }
    }

    private BwSharedResource instanceCustomSharedResource(String extension, File file) {
        BwSharedResource out = null;
        if (extension != null && !extension.isEmpty()) {
            if ( file != null) {
                LOG.debug("Looking for extensions: " + extension);
                String returnedClassName = this.extensionMapper.getProperty(extension);
                if (returnedClassName != null && !returnedClassName.isEmpty()) {
                    LOG.debug("Recovered class name form properties mapper: " + returnedClassName);
                    LOG.debug("Starting to instance it using reflection");
                    Class c;
                    try {
                        c = Class.forName(returnedClassName);
                        out = (BwSharedResource) c.getConstructor().newInstance();
                    } catch (InstantiationException ex) {
                        LOG.error("Error while instation the class instance", ex);
                    } catch (IllegalAccessException ex) {
                        LOG.error("Error while accessing the class instance", ex);
                    } catch (ClassNotFoundException ex) {
                        LOG.error("Error while founding the class instance", ex);
                    } catch (NoSuchMethodException ex) {
                        LOG.error("Error while found the method class instance", ex);
                    } catch (SecurityException ex) {
                        LOG.error("Security Error while instation the class instance", ex);
                    } catch (IllegalArgumentException ex) {
                        LOG.error("Argument exception while instation the class instance", ex);
                    } catch (InvocationTargetException ex) {
                        LOG.error("Invocation error while instation the class instance", ex);
                    }
                    LOG.debug("Finished to instance it using reflection correctly");
                }

                if (out == null) {
                    LOG.debug("Using the default base class to do the parsing");
                    out = new BwSharedResource();
                }
            }

        }
        return out;
    }
}

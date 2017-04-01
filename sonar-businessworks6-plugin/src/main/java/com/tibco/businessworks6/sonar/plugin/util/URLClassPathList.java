package org.sonar.plugins.tibco.utils.common;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;


/**
 *
 * @author developer
 */
public class URLClassPathList {
    private static final Logger LOG = Logger.getLogger(URLClassPathList.class);
    
    private URLClassPathList(){
    
    }
    
     public static List<URL> listResources(String root, String resInFile, String regex) {
        List<URL> url = new ArrayList<URL>();
        LOG.debug("Listing Resources on " + root + " using as workaround " + resInFile + " that matches " + regex);
        try {
            URL resource = URLClassPathList.class.getResource("/".concat(root).concat(resInFile));
            if (resource != null) {
                if ("jar".equals(resource.getProtocol())){
                    JarFile jf = ((JarURLConnection) resource.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jf.entries();
                    if (entries != null) {
                        while (entries.hasMoreElements()) {
                            JarEntry je = entries.nextElement();
                            String jen = je.getName();
                            LOG.debug("Parsing jar entry: " + jen);
                            if (jen.matches(regex)) {
                                    URL tmpUrl = new URL("jar:file:" + jf.getName() + "!/" + jen);
                                    url.add(tmpUrl);
                                    LOG.debug("Matched: " + tmpUrl.getPath());
                            }
                        }
                    }
                } else if ("file".equals(resource.getProtocol())){
                    // Resources in file system
                    DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resource.toURI()),"*.properties");
                    LOG.debug("Listing resources in folder: " + resource.toURI());
                    for (Path entry: stream) {
                        URL tmpUrl = entry.toUri().toURL();
                        url.add(tmpUrl);
                        LOG.debug("Matched: " + tmpUrl.getPath());
                    }                
                }
                LOG.debug("Resources listed");
            } else {
                LOG.debug("No resources detected ");
            }
        } catch (IOException ex) {
            LOG.error("Error listing resources: ",ex);
        } catch (URISyntaxException ex) {
            LOG.error("Error listing resources: ",ex);
        }
        return url;
    }
   
}

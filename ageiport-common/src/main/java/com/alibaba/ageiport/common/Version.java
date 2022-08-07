package com.alibaba.ageiport.common;


import com.alibaba.ageiport.common.logger.Logger;
import com.alibaba.ageiport.common.logger.LoggerFactory;
import com.alibaba.ageiport.common.utils.ClassUtils;
import com.alibaba.ageiport.common.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Version
 *
 * @author xuechao.sxc
 */
public final class Version {
    private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

    private static final String VERSION = getVersion(Version.class, "");

    static {
        // check if there's duplicated jar
        Version.checkDuplicate(Version.class);
    }

    private Version() {
    }


    public static String getVersion() {
        return VERSION;
    }


    public static String getVersion(Class<?> cls, String defaultVersion) {
        try {
            // find version info from MANIFEST.MF first
            Package pkg = cls.getPackage();
            String version = null;
            if (pkg != null) {
                version = pkg.getImplementationVersion();
                if (StringUtils.isNotEmpty(version)) {
                    return version;
                }

                version = pkg.getSpecificationVersion();
                if (StringUtils.isNotEmpty(version)) {
                    return version;
                }
            }

            // guess version from jar file name if nothing's found from MANIFEST.MF
            CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                LOGGER.info("No codeSource for class " + cls.getName() + " when getVersion, use default version " + defaultVersion);
                return defaultVersion;
            }

            String file = codeSource.getLocation().getFile();
            if (!StringUtils.isEmpty(file) && file.endsWith(".jar")) {
                version = getFromFile(file);
            }

            // return default version if no version info is found
            return StringUtils.isEmpty(version) ? defaultVersion : version;
        } catch (Throwable e) {
            LOGGER.error("return default version, ignore exception " + e.getMessage(), e);
            return defaultVersion;
        }
    }

    /**
     * get version from file: path/to/group-module-x.y.z.jar, returns x.y.z
     */
    private static String getFromFile(String file) {
        // remove suffix ".jar": "path/to/group-module-x.y.z"
        file = file.substring(0, file.length() - 4);

        // remove path: "group-module-x.y.z"
        int i = file.lastIndexOf('/');
        if (i >= 0) {
            file = file.substring(i + 1);
        }

        // remove group: "module-x.y.z"
        i = file.indexOf("-");
        if (i >= 0) {
            file = file.substring(i + 1);
        }

        // remove module: "x.y.z"
        while (file.length() > 0 && !Character.isDigit(file.charAt(0))) {
            i = file.indexOf("-");
            if (i >= 0) {
                file = file.substring(i + 1);
            } else {
                break;
            }
        }
        return file;
    }

    public static void checkDuplicate(Class<?> cls, boolean failOnError) {
        checkDuplicate(cls.getName().replace('.', '/') + ".class", failOnError);
    }

    public static void checkDuplicate(Class<?> cls) {
        checkDuplicate(cls, false);
    }

    public static void checkDuplicate(String path, boolean failOnError) {
        try {
            // search in caller's classloader
            Set<String> files = getResources(path);
            // duplicated jar is found
            if (files.size() > 1) {
                String error = "Duplicate class " + path + " in " + files.size() + " jar " + files;
                if (failOnError) {
                    throw new IllegalStateException(error);
                } else {
                    LOGGER.error(error);
                }
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * search resources in caller's classloader
     */
    private static Set<String> getResources(String path) throws IOException {
        Enumeration<URL> urls = ClassUtils.getCallerClassLoader(Version.class).getResources(path);
        Set<String> files = new HashSet<String>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                String file = url.getFile();
                if (StringUtils.isNotEmpty(file)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

}


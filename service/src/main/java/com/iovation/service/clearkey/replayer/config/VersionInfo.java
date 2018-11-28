package com.iovation.service.clearkey.replayer.config;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 12/19/16
 * Time: 11:48 AM
 * Description: ${CARET}
 */
@Slf4j
public class VersionInfo {

    @Getter
    private final Map<String, String> versionData;
    @Getter
    private final String displayName;

    public VersionInfo(String resource, String displayName) {
        this.versionData = load(resource);
        this.displayName = displayName;
    }

    private Map<String, String> load(String resource) {
        Properties localProps = new Properties();
        InputStream buildStream = VersionInfo.class.getClassLoader().getResourceAsStream(resource);

        try {
            localProps.load(buildStream);
        } catch (IOException e) {
            log.error("loadVersionInfo e=" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(buildStream);
        }

        Map<String, String> map = Maps.newHashMap();
        for (String key : localProps.stringPropertyNames())
            map.put(key, localProps.getProperty(key));

        log.debug("Loaded gitbuild info: " + map);
        return map;
    }
}

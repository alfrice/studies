package com.iovation.service.clearkey.replayer.rest;

import com.google.common.collect.Maps;
import com.iovation.service.clearkey.replayer.config.VersionInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 12/22/16
 * Time: 1:18 PM
 * Description: ${CARET}
 */
@SuppressWarnings("MVCPathVariableInspection")
@RestController
@Api(value = "Rest service for the basic service information",
        description = "health and ping for replayer service")
public class ServiceController {

    final VersionInfo versionInfo;

    @Autowired
    public ServiceController(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public void getPing(HttpServletResponse httpResponse) throws IOException {
        httpResponse.getWriter().write("PONG");
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Map getVersion() {
        Map<String, Object> version = Maps.newHashMap();
        version.put("name", versionInfo.getDisplayName());
        version.put("version", versionInfo.getVersionData().get("version"));
        version.put("number", versionInfo.getVersionData().get("changeListNumber"));
        version.put("date", versionInfo.getVersionData().get("buildDate"));
        return version;
    }
}

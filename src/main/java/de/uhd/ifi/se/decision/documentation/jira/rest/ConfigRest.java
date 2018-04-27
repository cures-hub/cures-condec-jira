package de.uhd.ifi.se.decision.documentation.jira.rest;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.documentation.jira.persistence.ConfigPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description Rest resource for plugin configuration
 */
@Path("/config")
@Scanned
public class ConfigRest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final String pluginStorageKey;


    @ComponentImport
    private final UserManager userManager;

    @Inject
    public ConfigRest(UserManager userManager){
        this.userManager = userManager;
        this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
        this.transactionTemplate = ComponentGetter.getTransactionTemplate();
        this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
    }

    @Path("/setActivated")
    @POST
    public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isActivated") String isActivated){
        Response datacheck = checkData(request, projectKey);
        if(datacheck != null){
            return  datacheck;
        } else {
		if(isActivated == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
            try {
                ConfigPersistence.setActivated(projectKey, Boolean.valueOf(isActivated));
                return Response.ok(Status.ACCEPTED).build();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                return Response.status(Status.CONFLICT).build();
            }
        }
    }
    @Path("/setIssueStrategy")
    @POST
    public Response setIssueStrategy(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isIssueStrategy") String isIssueStrategy){
        Response datacheck = checkData(request, projectKey);
        if(datacheck != null){
            return  datacheck;
        } else {
            if (isIssueStrategy == null) {
                return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null")).build();
            }
            try {
                ConfigPersistence.setIssueStrategy(projectKey, Boolean.valueOf(isIssueStrategy));
                return Response.ok(Status.ACCEPTED).build();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                return Response.status(Status.CONFLICT).build();
            }
        }
    }

    private Response getResponseForGet(String projectKey){
        boolean isExistingSettings;
        try {
            Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".projectKey");
                    return o;
                }
            });
            if (ob instanceof String && ob.equals("true")) {
                isExistingSettings = true;
            } else {
                isExistingSettings = false;
            }
        } catch (Exception e) {
            isExistingSettings = false;
        }
        return Response.ok(isExistingSettings).build();
    }

    private Response checkData(HttpServletRequest request,String projectKey ){
        if(request == null) {
            return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
        }
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        if(projectKey == null || projectKey.equals("")) {
            LOGGER.error("ProjectKey in ConfigRest setResponseForGet");
            return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build();
        }
        return null;
    }
}
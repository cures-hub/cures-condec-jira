package com.atlassian.DecisionDocumentation.rest.Config;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ewald Rode
 * @description Rest resource for plugin configuration
 */
@Path("/config")
@Scanned
public class ConfigRest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);
    @ComponentImport
    private final UserManager userManager;
    
    @Inject
    public ConfigRest(UserManager userManager){
        this.userManager = userManager;
    }
    
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response get(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey)
    {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        //TODO sicherheitsabfrage fuer boolean isactivated und projectkey
        ConfigRestLogic cRL = new ConfigRestLogic();
        cRL.setResponseForGet(projectKey);
        return cRL.getResponse();
    }
    
    @POST
    public Response doPost(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isActivated") String isActivated){
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        //TODO sicherheitsabfrage for boolean isactivated und projectkey
        ConfigRestLogic cRL = new ConfigRestLogic();
        cRL.setIsAvtivated(projectKey, isActivated);
        return cRL.getResponse();
    }
    
    @PUT
    public Response doPut(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isIssueStrategy") String isIssueStrategy){
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        //TODO sicherheitsabfrage for boolean isIssueStrategy und projectkey
        ConfigRestLogic cRL = new ConfigRestLogic();
        cRL.setIsIssueStrategy(projectKey, isIssueStrategy);
        return cRL.getResponse();
    }
}
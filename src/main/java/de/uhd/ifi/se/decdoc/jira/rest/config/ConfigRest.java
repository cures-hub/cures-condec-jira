package de.uhd.ifi.se.decdoc.jira.rest.config;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

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
    	if(request==null) {
    		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
    	}
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        if(projectKey != null) {
        	ConfigRestLogic cRL = new ConfigRestLogic();
        	cRL.setResponseForGet(projectKey);
            return cRL.getResponse();
        } else {
        	return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build();
        }
    }
    
    @POST
    public Response doPost(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isActivated") String isActivated){
    	if(request == null) {
    		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
    	}
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        if(projectKey != null) {
        	if(isActivated == null) {
        		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
        	}
	        ConfigRestLogic cRL = new ConfigRestLogic();
	        cRL.setIsActivated(projectKey, isActivated);
	        return cRL.getResponse();
        } else {
        	return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build();
        }
    }
    
    @PUT
    public Response doPut(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("isIssueStrategy") String isIssueStrategy){
    	if(request == null) {
    		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
    	}
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            LOGGER.warn("Unauthorized user by name:{} tried to change Configuration", username);
            return Response.status(Status.UNAUTHORIZED).build();
        }
        if(projectKey != null) {
        	if(isIssueStrategy == null) {
        		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null")).build();
        	}
	        ConfigRestLogic cRL = new ConfigRestLogic();
	        cRL.setIsIssueStrategy(projectKey, isIssueStrategy);
	        return cRL.getResponse();
	    } else {
	    	return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build();
	    }
    }
}
package com.atlassian.DecisionDocumentation.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.rest.Config.model.ConfigRepresentation;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.DecisionDocumentation.util.Pair;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Ewald Rode
 * @description
 */
@Scanned
public class AdminServlet extends HttpServlet{

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminServlet.class);

    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer renderer;
    
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final String pluginStorageKey;

    @Inject
    public AdminServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
            @ComponentImport TemplateRenderer renderer) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
        this.transactionTemplate = ComponentGetter.getTransactionTemplate();
        this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            redirectToLogin(request, response);
            return;
        }
        Map<String, ConfigRepresentation> configMap = new HashMap<String, ConfigRepresentation>();
        for (Project proj : ComponentAccessor.getProjectManager().getProjects()){
            final String projectKey = proj.getKey();
            final String projectName = proj.getName();
            Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object isActivatedObject = settings.get(pluginStorageKey + ".isActivated");
                    Object isIssueStrategyObject = settings.get(pluginStorageKey + ".isIssueStrategy");
                    Pair<String,String> pair = null;
                    if (isActivatedObject != null && isIssueStrategyObject != null) {
                    	if (isActivatedObject instanceof String && isIssueStrategyObject instanceof String) {
                    		String isActivatedString = (String) isActivatedObject;
                    		String isIssueStrategyString = (String) isIssueStrategyObject;
                        	pair = new Pair<String,String>(isActivatedString,isIssueStrategyString);
                        }
                    } else if (isIssueStrategyObject != null && isIssueStrategyObject == null) {
                    	if (isActivatedObject instanceof String) {
                    		String isActivatedString = (String) isActivatedObject;
                        	pair = new Pair<String,String>(isActivatedString,"false");
                        }
                    } else if (isIssueStrategyObject == null && isIssueStrategyObject != null) {
                    	if (isIssueStrategyObject instanceof String) {
                    		String isIssueStrategyString = (String) isIssueStrategyObject;
                        	pair = new Pair<String,String>("false",isIssueStrategyString);
                        }
                    } else if (isIssueStrategyObject == null && isIssueStrategyObject == null) {
                    	pair = new Pair<String,String>("false","false");
                    }
                    return pair;
                }
            });
            String isActivated = "false";
            String isIssueStrategy = "false";
            if (ob instanceof Pair<?, ?>){
                Pair<String, String> pair = (Pair<String, String>) ob;
                isActivated = pair.getLeft();
                isIssueStrategy = pair.getRight();
            }
            
            ConfigRepresentation configRep = new ConfigRepresentation(projectKey, projectName, isActivated, isIssueStrategy);
            configMap.put(projectKey , configRep);
        }
        Map<String, Object> velocityParams = new HashMap<String, Object>();
        response.setContentType("text/html;charset=utf-8");
        velocityParams.put("requestUrl", request.getRequestURL());
        velocityParams.put("projectsMap", configMap);
        LOGGER.info("AdminServlet is now being rendered.");
        renderer.render("templates/admin.vm", velocityParams, response.getWriter());
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
        LOGGER.info("User with Name('{}') tried to access AdminServlet and has been redirected to Login",
                userManager.getRemoteUsername(request));
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}
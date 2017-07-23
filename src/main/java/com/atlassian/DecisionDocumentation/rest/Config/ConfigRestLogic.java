package com.atlassian.DecisionDocumentation.rest.Config;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

/**
 * @author Ewald Rode
 * @description
 */
public class ConfigRestLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRestLogic.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final String pluginStorageKey;
    
    /**
     * isActivated is saved as String, because JIRA pluginsettings dont accept booleans, enums or self-defined classes
     */
    private String isActivated;
    private Status status;
    
    public ConfigRestLogic(){
    	this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
        this.transactionTemplate = ComponentGetter.getTransactionTemplate();
        this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
    }
    
    public void setResponseForGet(final String projectKey){
    	//TODO set status.... ist HTTP GET ueberhaupt noetig?
        try{
            Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".projectKey");
                    return o;
                }
            });
            if (ob instanceof String){
                isActivated = (String) ob;
            } else {
                isActivated = "false";
            }
        } catch (Exception e){
            isActivated = "false";
        }
    }
    
    public void setIsAvtivated(final String projectKey, final String isActivated){
        try{
            transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    settings.put(pluginStorageKey + ".isActivated", isActivated);
                    return null;
                }
            });
            status = Status.ACCEPTED;
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            status = Status.CONFLICT;
        }
    }

    public void setIsIssueStrategy(final String projectKey, final String isIssueStrategy){
        try{
            transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    settings.put(pluginStorageKey + ".isIssueStrategy", isIssueStrategy);
                    return null;
                }
            });
            status = Status.ACCEPTED;
            //TODO add issueTypes to issuetypescheme of given project
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            status = Status.CONFLICT;
        }
    }
    
    public Response getResponse() {
        if(status != Status.CONFLICT){
            if(status == Status.ACCEPTED){
                return Response.ok(Status.ACCEPTED).build();
            } else {
                //kommt nur bei einer GET-Request vor
                //TODO logger +  Refactoring
                return Response.ok(isActivated).build();
            }
        }else {
            return Response.status(Status.CONFLICT).build();
        }
    }
}
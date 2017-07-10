package com.atlassian.DecisionDocumentation.util;

import java.util.Map;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

/**
 * @author Ewald Rode
 * @description Constraint for web-item. Calculates whether web-item should be displayed depending on the project specific context
 */
public class IsActivated implements Condition {

    private PluginSettingsFactory pluginSettingsFactory;
    private TransactionTemplate transactionTemplate;
    private String pluginStorageKey;
    
    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
        this.transactionTemplate = ComponentGetter.getTransactionTemplate();
        this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        String shouldDisplay = "false";
        Object object = context.get("projectKey");
        if (object instanceof String){
            final String projectKey = (String) object;
            Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".projectKey");
                    if (o instanceof String){
                        return o;
                    } else {
                        //TODO Logger.error erroneous settings 
                        return false;
                    }
                }
            });
            boolean bool = false;
            if (ob instanceof String){
                shouldDisplay = (String) ob;
                try {
                     bool = Boolean.valueOf(shouldDisplay);
                } catch (Exception e) {
                    //TODO Logger
                }
                return bool;
            } else {
                //error TODO logger
                return false;
            }
        } else {
            //error object either null or other type, check atlassian-plugin.xml definition of constraint for web-item
            //TODO Logger
            return false;
        }
    }
}
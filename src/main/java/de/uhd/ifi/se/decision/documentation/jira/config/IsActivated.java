package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @author Ewald Rode
 * @description Constraint for web-item. Calculates whether web-item should be displayed depending on the project specific context
 */
public class IsActivated implements Condition {
	private static final Logger LOGGER = LoggerFactory.getLogger(IsActivated.class);
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
    	if(context == null) {
    		LOGGER.error("PluginSettings are corrupt");
    		return false;
    	}
        String shouldDisplay = "false";
        Object object = context.get("projectKey");
        if (object instanceof String){
            final String projectKey = (String) object;
            Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".isActivated");
                    if (o instanceof String){
                        return o;
                    } else {
                    	LOGGER.error("PluginSettings are corrupt");
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
                    LOGGER.error(e.getMessage());
                }
                return bool;
            } else {
                LOGGER.error("PluginSettings are corrupt");
                return false;
            }
        } else {
        	LOGGER.error("Context has no projectKey");
            return false;
        }
    }
}
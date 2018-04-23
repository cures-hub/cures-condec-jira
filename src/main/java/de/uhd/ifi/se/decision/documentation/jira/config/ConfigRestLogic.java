package de.uhd.ifi.se.decision.documentation.jira.config;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @description Getter and Setter for ConfigRestResource
 */
public class ConfigRestLogic {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRestLogic.class);
	private final PluginSettingsFactory pluginSettingsFactory;
	private final TransactionTemplate transactionTemplate;
	private final String pluginStorageKey;

	private boolean isActivated;
	private Status status;

	public ConfigRestLogic() {
		this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
		this.transactionTemplate = ComponentGetter.getTransactionTemplate();
		this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
	}

	public void setResponseForGet(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("Empyt ProjectKey in ConfigRestLogic setResponseForGet");
			status = Status.CONFLICT;
		} else {
			try {
				Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
					public Object doInTransaction() {
						PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
						Object o = settings.get(pluginStorageKey + ".projectKey");
						return o;
					}
				});
				if (ob instanceof String && ob.equals("true")) {
					isActivated = true;
				} else {
					isActivated = false;
				}
			} catch (Exception e) {
				isActivated = false;
			}
		}
	}

	public void setActivated(String projectKey, boolean isActivated) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("ProjectKey in ConfigRestLogic setResponseForGet");
			status = Status.CONFLICT;
		} else {
			try {
				transactionTemplate.execute(new TransactionCallback<Object>() {
					public Object doInTransaction() {
						PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
						settings.put(pluginStorageKey + ".isActivated", Boolean.toString(isActivated));
						return null;
					}
				});
				status = Status.ACCEPTED;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				status = Status.CONFLICT;
			}
		}
	}

	public void setIssueStrategy(final String projectKey, boolean isIssueStrategy) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("Persistence strategy cannot be set since project key is invalid.");
			status = Status.CONFLICT;
		} else {
			try {
				transactionTemplate.execute(new TransactionCallback<Object>() {
					public Object doInTransaction() {
						PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
						settings.put(pluginStorageKey + ".isIssueStrategy", Boolean.toString(isIssueStrategy));
						return null;
					}
				});
				status = Status.ACCEPTED;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				status = Status.CONFLICT;
			}
		}
	}

	public Response getResponse() {
		if (status != Status.CONFLICT) {
			if (status == Status.ACCEPTED) {
				return Response.ok(Status.ACCEPTED).build();
			} else {
				return Response.ok(isActivated).build();
			}
		} else {
			return Response.status(Status.CONFLICT).build();
		}
	}
}
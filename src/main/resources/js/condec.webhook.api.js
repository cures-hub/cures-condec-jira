/**
 * This module implements the communication with the ConDec Webhook REST API 
 * to configure and test the webhook that posts changed decision knowledge to e.g. Slack.
 *
 * Is referenced in HTML by settings/webbhook/...
 */
(function(global) {

	var ConDecWebhookAPI = function() {
		this.restPrefix = AJS.contextPath() + "/rest/condec/latest/webhook";
	};

	ConDecWebhookAPI.prototype.setWebhookData = function(projectKey, webhookUrl, webhookSecret) {
		generalApi.postJSON(this.restPrefix + "/setWebhookData.json?projectKey=" + projectKey
			+ "&webhookUrl=" + webhookUrl + "&webhookSecret=" + webhookSecret, null, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The webhook for this project has been set.");
				}
			});
	};

	ConDecWebhookAPI.prototype.setWebhookEnabled = function(isActivated, projectKey) {
		generalApi.postJSON(this.restPrefix + "/setWebhookEnabled.json?projectKey=" + projectKey
			+ "&isActivated=" + isActivated, null, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The webhook activation for this project has been changed.");
				}
			});
	};

	ConDecWebhookAPI.prototype.setWebhookType = function(webhookType, projectKey, isWebhookTypeEnabled) {
		generalApi.postJSON(this.restPrefix + "/setWebhookType.json?projectKey=" + projectKey
			+ "&webhookType=" + webhookType + "&isWebhookTypeEnabled=" + isWebhookTypeEnabled, null, function(
				error, response) {
			if (error === null) {
				conDecAPI.showFlag("success", "The webhook root element type was changed for this project.");
			}
		});
	};

	ConDecWebhookAPI.prototype.sendTestPost = function(projectKey) {
		generalApi.postJSON(this.restPrefix + "/sendTestPost.json?projectKey="
			+ projectKey, null, function(error, response) {
				if (error === null) {
					conDecAPI.showFlag("success", "The webhook test was send.");
				}
			});
	};

	global.conDecWebhookAPI = new ConDecWebhookAPI();
})(window);
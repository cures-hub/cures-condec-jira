package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WebConnector{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConnector.class);
    private String url;
    private String secret;

    public WebConnector(String projectKey){
        this.url = ConfigPersistence.getWebhookUrl(projectKey);
        this.secret = ConfigPersistence.getWebhookSecret(projectKey);
    }

    public WebConnector(String webhookUrl, String webhookSecret){
        if(webhookUrl == null || webhookSecret == null){
            webhookUrl = "";
            webhookSecret = "";
            LOGGER.error("Webhook could not be created because Webhook Url and Secret are null");
        }
        this.url = webhookUrl;
        this.secret = webhookSecret;
    }

    public boolean sendWebHookTreant(String projectKey, String issueKey) {
        try {
            HttpClient httpClient = new HttpClient();
            WebBodyProvider provider = new WebBodyProvider(projectKey, issueKey);
            PostMethod postMethod = provider.getPostMethod();
            postMethod.setURI(new HttpsURL(url));
            int respEntity = httpClient.executeMethod(postMethod);
            System.out.println(respEntity);
            if (respEntity == 200) {
                return true;
            }
        } catch (HttpException e) {
            LOGGER.error("Could not send WebHook data because of "+ e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("Could not send WebHook data because of "+ e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

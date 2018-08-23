package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

public class WebConnector{
    private String url;
    private String secret;

    public WebConnector(String projectKey){
        this.url = ConfigPersistence.getWebhookUrl(projectKey);
        this.secret = ConfigPersistence.getWebhookSecret(projectKey);
    }

    public WebConnector(String webhookUrl, String webhookSecret){
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
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }
        return false;
    }
}

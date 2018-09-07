package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.combinator.testing.Str;

import java.io.IOException;

public class WebConnector{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConnector.class);
    private String url;
    private String secret;
    private PostMethod postMethod;

    public WebConnector(String projectKey){
        if(projectKey != null){
            this.url = ConfigPersistence.getWebhookUrl(projectKey);
            this.secret = ConfigPersistence.getWebhookSecret(projectKey);
        }
        if(url == null) {
            url = "";
            LOGGER.error("Webhook could not be created because Webhook Url is null");
        }
        if(secret == null){
            secret = "";
            LOGGER.error("Webhook could not be created because Webhook Secret is null");
        }
    }

    public WebConnector(String webhookUrl, String webhookSecret){
        if(webhookUrl == null) {
            webhookUrl = "";
            LOGGER.error("Webhook could not be created because Webhook Url is null");
        }
        if( webhookSecret == null){
            webhookSecret = "";
            LOGGER.error("Webhook could not be created because Webhook Secret is null");
        }
        this.url = webhookUrl;
        this.secret = webhookSecret;
    }

    public boolean sendWebHookForIssueKey(String projectKey, String issueKey) {
        if(!checkSubmitionData(projectKey, issueKey)){
            return false;
        }
        WebBodyProvider provider = new WebBodyProvider(projectKey, issueKey);
        postMethod = provider.getPostMethodForIssueKey();
        return submitPostMethod();
    }

    //TODO Needs some kind of getGitHash function in the persistence strategy
    //Error in line 31 in Graph
    // this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(rootElementKey);
    public boolean sendWebHookForGitHash(String projectKey, String gitHash){
        if(!checkSubmitionData(projectKey,gitHash)){
            return false;
        }
        WebBodyProvider provider = new WebBodyProvider(projectKey, gitHash);
        postMethod = provider.getPostMethodForGitHash();
        return submitPostMethod();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private boolean submitPostMethod(){
        try {
            HttpClient httpClient = new HttpClient();
            Header header = new Header();
            header.setName("X-Hub-Signature");
            header.setValue(this.secret);
            postMethod.setRequestHeader(header);
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

    private boolean checkSubmitionData(String projectKey, String key){
        if(this.url==null || this.url.equals("")){
            LOGGER.error("Could not send WebHook data because the Url is Null or empty");
            return false;
        }
        if(this.secret == null || this.secret.equals("")){
            LOGGER.error("Could not send WebHook data because Secret is Null or empty");
            return false;
        }
        if(projectKey == null || projectKey.equals("")){
            LOGGER.error("Could not send WebHook data because projectKey Null or empty");
            return false;
        }
        if(projectKey == null || projectKey.equals("")){
            LOGGER.error("Could not send WebHook data because projectKey Null or empty");
            return false;
        }
        if(key == null || key.equals("")){
            LOGGER.error("Could not send WebHook data because issueKey Null or empty");
            return false;
        }
        return true;
    }

}

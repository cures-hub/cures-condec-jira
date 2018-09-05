package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.jira.util.json.JSONObject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        if(projectKey == null || projectKey.equals("")){
            LOGGER.error("Could not send WebHook data because projectKey Null or empty");
            return false;
        }
        if(issueKey == null || issueKey.equals("")){
            LOGGER.error("Could not send WebHook data because issueKey Null or empty");
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
        if(projectKey == null || projectKey.equals("")){
            LOGGER.error("Could not send WebHook data because projectKey Null or empty");
            return false;
        }
        if(gitHash == null || gitHash.equals("")){
            LOGGER.error("Could not send WebHook data because issueKey Null or empty");
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

    public void submitTestPost(String projectKey, String gitHash){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.url);
        httpPost.setHeader("accept", "application/json");
        WebBodyProvider provider = new WebBodyProvider(projectKey,gitHash);
        List<BasicNameValuePair> nvps = provider.getNvm();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println(response2.getStatusLine());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}

package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;

import java.io.IOException;

public class WebConnector{
    private String url = "https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec";
    private String secret = "03f90207-73bc-44d9-9848-d3f1f8c8254e";

    public boolean sendWebHookTreant() {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        Treant treant = new Treant("Test", "Test", 1);
        NameValuePair head = new NameValuePair("issueKey", "CONDEC-1234");
        NameValuePair body = new NameValuePair("ConDeTree", treant.toString());
        postMethod.addParameter(head);
        postMethod.addParameter(body);

        try {
            int respEntity = httpClient.executeMethod(postMethod);
            System.out.println(treant.toString());
            System.out.println(respEntity);
            if (respEntity == 200) {
                return true;
            }
            if (respEntity == 404) {
                return false;
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

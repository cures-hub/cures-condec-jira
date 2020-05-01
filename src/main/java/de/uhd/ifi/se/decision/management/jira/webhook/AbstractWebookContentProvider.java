package de.uhd.ifi.se.decision.management.jira.webhook;

import org.apache.commons.httpclient.methods.PostMethod;



public abstract class AbstractWebookContentProvider  {


  String projectKey;
  WebhookType type;


  public abstract PostMethod createPostMethod();
  //  abstract String createWebhookData();


}

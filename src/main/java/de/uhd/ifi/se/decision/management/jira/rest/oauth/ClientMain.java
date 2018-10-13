package de.uhd.ifi.se.decision.management.jira.rest.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMain {

    public static void main(String[] args) throws Exception {
//        if (args.length == 0) {
//            throw new IllegalArgumentException("No command specified. Use one of " + Command.names());
//        }

        PropertiesClient propertiesClient = new PropertiesClient();
        JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(propertiesClient);
        
        
       String s1 =  "requestToken";
        List<String> s2 = new ArrayList<String>();
        
        
//        s2.add("http://cures.ifi.uni-heidelberg.de:8080/");
//        s2.add("request");
//        s2.add("http://cures.ifi.uni-heidelberg.de:8080/rest/gitplugin/1.0/issues/LUCENE-7264/commits");
        
//        List<String> argumentsWithoutFirst = Arrays.asList(args).subList(1, args.length);

        new OAuthClient(propertiesClient, jiraOAuthClient).execute(Command.fromString(s1), s2);
    }
}

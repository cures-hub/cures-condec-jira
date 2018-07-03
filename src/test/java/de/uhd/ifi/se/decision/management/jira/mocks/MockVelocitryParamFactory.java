package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.VelocityParamFactory;

import java.util.HashMap;
import java.util.Map;

public class MockVelocitryParamFactory implements VelocityParamFactory {
    @Override
    public Map<String, Object> getDefaultVelocityParams(JiraAuthenticationContext jiraAuthenticationContext) {
        return null;
    }

    @Override
    public Map<String, Object> getDefaultVelocityParams(Map<String, Object> map, JiraAuthenticationContext jiraAuthenticationContext) {
        Map<String, Object> velocityParams = new HashMap<>();
        return velocityParams;
    }

    @Override
    public Map<String, Object> getDefaultVelocityParams() {
        return null;
    }

    @Override
    public Map<String, Object> getDefaultVelocityParams(Map<String, Object> map) {
        return null;
    }
}

package de.uhd.ifi.se.decision.documentation.jira.mocks;

import com.atlassian.jira.security.roles.ProjectRole;

public class MockProjectRole implements ProjectRole {

    private Long id;
    private String name;
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

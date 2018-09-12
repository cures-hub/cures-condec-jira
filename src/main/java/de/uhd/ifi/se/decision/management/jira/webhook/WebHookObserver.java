package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

import java.util.ArrayList;
import java.util.List;

public class WebHookObserver  {
    private String projectKey;
    private WebConnector connector;

    public WebHookObserver(String projectKey){
        this.projectKey = projectKey;
        connector = new WebConnector(projectKey);
    }

    public boolean sendIssueChanges(DecisionKnowledgeElement decisionKnowledgeElement){
        ArrayList<DecisionKnowledgeElement> workItemList = getLinkedWorkItems(decisionKnowledgeElement);
        return submitChangesWorkItems(workItemList);
    }

    public boolean sendIssueDeleteChanges(ApplicationUser user, DecisionKnowledgeElement decisionKnowledgeElement){
        AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
        ArrayList<DecisionKnowledgeElement> workItemList = getLinkedWorkItems(decisionKnowledgeElement);
        boolean isDeleted = strategy.deleteDecisionKnowledgeElement(decisionKnowledgeElement, user);
        if(decisionKnowledgeElement.getType().equals(KnowledgeType.WORKITEM)){
            workItemList.remove(decisionKnowledgeElement);
        }
        if(!isDeleted){
            return false;
        }
        return submitChangesWorkItems(workItemList);
    }

    private boolean submitChangesWorkItems(List<DecisionKnowledgeElement> workItemList){
        boolean submitted = true;
        for(DecisionKnowledgeElement workItem : workItemList){
            if(!connector.sendWebHookForIssueKey(projectKey,workItem.getKey())){
                submitted = false;
            }
        }
        return submitted;
    }

    private ArrayList<DecisionKnowledgeElement> getLinkedWorkItems(DecisionKnowledgeElement element){
        ArrayList<DecisionKnowledgeElement> workItemList = new ArrayList<>();

        AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
        //TODO IsIssueStrategy?
        List<DecisionKnowledgeElement> linkedElements = strategy.getLinkedElements(element);
        for(DecisionKnowledgeElement linkedElement: linkedElements){
            if(linkedElement.getType().equals(KnowledgeType.WORKITEM)){
                workItemList.add(linkedElement);
            }
            workItemList.addAll(getLinkedWorkItems(linkedElement));
        }
        return workItemList;
    }
}

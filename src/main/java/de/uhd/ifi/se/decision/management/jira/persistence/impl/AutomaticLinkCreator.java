package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.Query;

public class AutomaticLinkCreator {

	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static DecisionKnowledgeElement getPotentialParentElement(DecisionKnowledgeElement element) {
		if (element == null) {
			return null;
		}
		List<DecisionKnowledgeElement> potentialParentElements = getPotentialParentElements(element);
		if (potentialParentElements.isEmpty()) {
			return new DecisionKnowledgeElementImpl(element.getJiraIssue());
		}
		if (potentialParentElements.size() == 2) {
			return getMostRecentElement(potentialParentElements.get(0), potentialParentElements.get(1));
		}
		return potentialParentElements.get(0);
	}

	private static List<DecisionKnowledgeElement> getPotentialParentElements(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> potentialParentElements = new ArrayList<DecisionKnowledgeElement>();
		List<KnowledgeType> parentTypes = KnowledgeType.getParentTypes(element.getType());
		for (KnowledgeType parentType : parentTypes) {
			DecisionKnowledgeElement potentialParentElement = searchForLast(element, parentType);
			if (potentialParentElement != null) {
				potentialParentElements.add(potentialParentElement);
			}
		}
		return potentialParentElements;
	}

	public static DecisionKnowledgeElement searchForLast(DecisionKnowledgeElement sentence,
			KnowledgeType typeToSearch) {
		PartOfJiraIssueText lastSentence = null;
		PartOfJiraIssueTextInDatabase[] databaseEntries = ACTIVE_OBJECTS.find(PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ?", ((PartOfJiraIssueText) sentence).getJiraIssueId())
						.order("ID DESC"));

		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			if (databaseEntry.getType().equalsIgnoreCase(typeToSearch.toString())) {
				lastSentence = new PartOfJiraIssueTextImpl(databaseEntry);
				break;
			}
		}
		return lastSentence;
	}

	private static DecisionKnowledgeElement getMostRecentElement(DecisionKnowledgeElement first,
			DecisionKnowledgeElement second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (first.getId() > second.getId()) {
			return first;
		}
		return second;
	}

}

package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.Query;

public class AutomaticLinkCreator {

	public static DecisionKnowledgeElement getMostRecentElement(DecisionKnowledgeElement first,
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

	public static List<KnowledgeType> getParentTypes(KnowledgeType knowledgeType) {
		List<KnowledgeType> parentTypes = new ArrayList<KnowledgeType>();
		if (knowledgeType == null) {
			return parentTypes;
		}

		if (knowledgeType == KnowledgeType.ARGUMENT || knowledgeType == KnowledgeType.PRO
				|| knowledgeType == KnowledgeType.CON) {
			parentTypes.add(KnowledgeType.ALTERNATIVE);
			parentTypes.add(KnowledgeType.DECISION);
		} else if (knowledgeType == KnowledgeType.DECISION || knowledgeType == KnowledgeType.ALTERNATIVE) {
			parentTypes.add(KnowledgeType.ISSUE);
		}

		return parentTypes;
	}

	public static List<DecisionKnowledgeElement> getPotentialParentElements(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> potentialParentElements = new ArrayList<DecisionKnowledgeElement>();
		List<KnowledgeType> parentTypes = getParentTypes(sentence.getType());
		for (KnowledgeType parentType : parentTypes) {
			DecisionKnowledgeElement potentialParentElement = searchForLast(sentence, parentType);
			if (potentialParentElement != null) {
				potentialParentElements.add(potentialParentElement);
			}
		}
		return potentialParentElements;
	}

	public static DecisionKnowledgeElement getPotentialParentElement(PartOfJiraIssueText sentence) {
		if (sentence == null) {
			return null;
		}
		List<DecisionKnowledgeElement> potentialParentElements = getPotentialParentElements(sentence);
		if (potentialParentElements.isEmpty()) {
			return null;
		}
		if (potentialParentElements.size() == 2) {
			return getMostRecentElement(potentialParentElements.get(0), potentialParentElements.get(1));
		}
		return potentialParentElements.get(0);
	}

	public static DecisionKnowledgeElement searchForLast(PartOfJiraIssueText sentence, KnowledgeType typeToSearch) {
		PartOfJiraIssueText lastSentence = null;
		PartOfJiraIssueTextInDatabase[] databaseEntries = JiraIssueTextPersistenceManager.ACTIVE_OBJECTS.find(
				PartOfJiraIssueTextInDatabase.class,
				Query.select().where("JIRA_ISSUE_ID = ?", sentence.getJiraIssueId()).order("ID DESC"));

		for (PartOfJiraIssueTextInDatabase databaseEntry : databaseEntries) {
			if (databaseEntry.getType().equalsIgnoreCase(typeToSearch.toString())) {
				lastSentence = new PartOfJiraIssueTextImpl(databaseEntry);
				break;
			}
		}
		return lastSentence;
	}

}

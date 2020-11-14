package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class RDFSourceInputFactory {

	public static InputMethod getInputMethod(RecommenderType recommenderType) {
		switch (recommenderType) {
			case KEYWORD:
				return new RDFSourceInputString();
			case ISSUE:
				return new RDFSourceInputKnowledgeElement();
			default:
				return new RDFSourceInputString();
		}
	}

}

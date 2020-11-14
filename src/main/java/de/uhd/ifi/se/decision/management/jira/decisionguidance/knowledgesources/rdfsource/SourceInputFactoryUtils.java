package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;

public final class SourceInputFactoryUtils {

	public static InputMethod getInputMethod(RecommenderType recommenderType) {
		switch (recommenderType) {
			case ISSUE:
				return new RDFSourceInputKnowledgeElement();
			default:
				return new RDFSourceInputString();
		}
	}

}

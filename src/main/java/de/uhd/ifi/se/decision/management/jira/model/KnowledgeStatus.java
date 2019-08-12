package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum KnowledgeStatus {
	IDEA, DECIDED, REJECTED, UNDEFINED;

	public static KnowledgeStatus getKnowledgeStatus(String status){
		if(status == null || status.isEmpty()){
			return UNDEFINED;
		}
		for(KnowledgeStatus knowledgeStatus: KnowledgeStatus.values()){
			if(knowledgeStatus.name().toLowerCase(Locale.ENGLISH).matches(status.toLowerCase(Locale.ENGLISH))){
				return knowledgeStatus;
			}
		}
		return UNDEFINED;
	}

	public static List<String> toList() {
		List<String> knowledgeStatus = new ArrayList<>();
		for(KnowledgeStatus status: KnowledgeStatus.values()){
			knowledgeStatus.add(status.toString());
		}
		return knowledgeStatus;
	}

	@Override
	public String toString(){
		return this.name().substring(0,1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}
}

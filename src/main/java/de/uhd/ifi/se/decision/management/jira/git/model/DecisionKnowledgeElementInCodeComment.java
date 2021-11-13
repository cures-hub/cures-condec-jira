package de.uhd.ifi.se.decision.management.jira.git.model;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class DecisionKnowledgeElementInCodeComment extends KnowledgeElement {

	private ChangedFile codeFile;
	private String hash;
	private KeyData keyData;

	public DecisionKnowledgeElementInCodeComment(KnowledgeElement rationale) {
		setProject(rationale.getProject());
		setSummary(rationale.getSummary());
		setDescription(rationale.getDescription());
		setKey(rationale.getKey());
		setType(rationale.getType());
	}

	public DecisionKnowledgeElementInCodeComment() {
		this.documentationLocation = DocumentationLocation.CODE;
	}

	@XmlElement
	public KeyData getKeyData() {
		keyData = new KeyData(getKey());
		if (keyData.source.isBlank()) {
			keyData.source = getDescription().split(":")[0];
		}
		return keyData;
	}

	@XmlElement
	public String getImage() {
		return getType().getIconUrl();
	}

	public class KeyData {
		@XmlElement
		public String value = "";
		@XmlElement
		public String source = "";

		public KeyData(String key) {
			this.value = key;
		}
	}
}
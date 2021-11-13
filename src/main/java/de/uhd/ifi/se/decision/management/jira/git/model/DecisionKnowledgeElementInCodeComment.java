package de.uhd.ifi.se.decision.management.jira.git.model;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class DecisionKnowledgeElementInCodeComment extends KnowledgeElement {

	public ChangedFile codeFile;
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
		keyData = new KeyData(codeFile);
		return keyData;
	}

	@XmlElement
	public String getImage() {
		return getType().getIconUrl();
	}

	public class KeyData {
		@XmlElement
		public String source = "";

		public KeyData(ChangedFile file) {
			source = file != null ? file.getName() : null;
		}
	}
}
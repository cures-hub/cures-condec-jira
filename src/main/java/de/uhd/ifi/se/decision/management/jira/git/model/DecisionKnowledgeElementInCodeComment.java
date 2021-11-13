package de.uhd.ifi.se.decision.management.jira.git.model;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class DecisionKnowledgeElementInCodeComment extends KnowledgeElement {

	private ChangedFile codeFile;
	private String hash;
	private String image;
	private KeyData keyData;

	public DecisionKnowledgeElementInCodeComment(KnowledgeElement rationale) {
		setProject(rationale.getProject());
		setSummary(rationale.getSummary());
		setDescription(rationale.getDescription());
		setKey(rationale.getKey());
		setType(rationale.getType());
	}

	public DecisionKnowledgeElementInCodeComment() {
		// TODO Auto-generated constructor stub
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
		/*
		 * commit typed dec. elements have keys in this form:
		 * SOURCE+SPACECHAR+POSITION+SPACECHAR+RATIONALEHASHCODE where : SOURCE is
		 * either commit id hash, meaning rationale comes from commit message text or
		 * the file path meaning rationale comes from source code. Therefore it is
		 * defined as:
		 * 
		 * SOURCE := COMMITMESSAGE | FILESOURCE COMMITMESSAGE matches regex [a-f0-9]{40}
		 * FILESOURCE consists of: FILEPATH+SPACECHAR+DIFFSEQUENCE+SPACECHAR+DIFFKEY
		 * 
		 * 
		 * SPACECHAR is white space character
		 * 
		 * POSITION starts with line number of source rationale was found in separated
		 * by colon character followed by line number of source rationale ended
		 * separated by colon character followed by cursor position relative to code
		 * block comment text or commit message text
		 * 
		 * RATIONALEHASHCODE is hash value of rationale text
		 * 
		 * Note: in case of file SOURCEs, file paths can have spaces. Others components
		 * of the key will not have spaces.
		 */
		@XmlElement
		public String value = "";
		@XmlElement
		public String source = "";
		@XmlElement
		public String position = "";
		@XmlElement
		public String rationaleHash = "";

		public KeyData(String key) {
			this.value = key;
			String[] keyComponents = key.split(" ");
			int len = keyComponents.length;
			if (len < 3) {
				return;
			}
			rationaleHash = keyComponents[len - 1];
			position = keyComponents[len - 2];

			String[] sourceComp = Arrays.copyOfRange(keyComponents, 0, len - 2);
			source = String.join(" ", sourceComp);

			// source still includes filename, diff sequence number and diff entry
			if (!source.contains("commit")) {
				int lastSpaceOccurrencePosition = source.lastIndexOf(" ");
				if (lastSpaceOccurrencePosition > -1) {
					source = source.substring(0, lastSpaceOccurrencePosition);
					lastSpaceOccurrencePosition = source.lastIndexOf(" ");
				}
			}
		}
	}
}
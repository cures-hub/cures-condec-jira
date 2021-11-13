package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Represents a branch in git with commits, changed code files, and decision
 * knowledge in code comments and commit messages.
 */
public class BranchDiff {

	@XmlElement
	private String branchName;

	@XmlElement
	private List<RationaleData> elements;

	public BranchDiff(String branchName, List<KnowledgeElement> decisionKnowledgeElements) {
		this.branchName = branchName;
		this.elements = new ArrayList<>();
		for (KnowledgeElement rationale : decisionKnowledgeElements) {
			elements.add(new RationaleData(rationale));
		}
	}

	public String getBranchName() {
		return branchName;
	}

	public List<RationaleData> getElements() {
		return elements;
	}

	/* Class mapping DecisionKnowledgeElement to xml */
	class RationaleData {
		@XmlElement
		private String summary;
		@XmlElement
		private String description;
		@XmlElement
		public KeyData key;
		@XmlElement
		private String type;

		public RationaleData(KnowledgeElement rationale) {
			summary = rationale.getSummary();
			description = rationale.getDescription();
			key = new KeyData(rationale.getKey());
			if (key.source.isBlank()) {
				key.source = rationale.getDescription().split(":")[0];
			}
			key.sourceTypeCodeFile = !key.sourceTypeCommitMessage;
			type = rationale.getType().toString();
		}

		public String getDescription() {
			return description;
		}

		public KeyData getKey() {
			return key;
		}

		public String getType() {
			return type;
		}

		public String getSummary() {
			return summary;
		}

		class KeyData {
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
			public boolean sourceTypeCommitMessage = false;
			@XmlElement
			public boolean sourceTypeCodeFile = false;
			@XmlElement
			public boolean codeFileA = false;
			@XmlElement
			public String diffEntrySequence = "";
			@XmlElement
			public String diffEntry = "";
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
				sourceTypeCommitMessage = source.contains("commit");
				sourceTypeCodeFile = !sourceTypeCommitMessage;

				// source still includes filename, diff sequence number and diff entry
				if (sourceTypeCodeFile) {
					int lastSpaceOccurrencePosition = source.lastIndexOf(" ");
					if (lastSpaceOccurrencePosition > -1) {
						diffEntry = source.substring(1 + lastSpaceOccurrencePosition);
						source = source.substring(0, lastSpaceOccurrencePosition);
						lastSpaceOccurrencePosition = source.lastIndexOf(" ");
						if (lastSpaceOccurrencePosition > -1) {
							diffEntrySequence = source.substring(1 + lastSpaceOccurrencePosition);
							source = source.substring(0, lastSpaceOccurrencePosition);
						}
					}
					codeFileA = false;
					if (source.startsWith("~")) {
						codeFileA = true;
					}
				}
			}
		}
	}
}

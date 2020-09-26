package de.uhd.ifi.se.decision.management.jira.config.workflows;

/**
 * @issue How can we create workflows programmatically?
 * @issue How can we read XML files from the resources folder?
 */
public class IssueWorkflow {

	public static String getXMLDescriptor() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<!DOCTYPE workflow PUBLIC \"-//OpenSymphony Group//DTD OSWorkflow 2.8//EN\" \"http://www.opensymphony.com/osworkflow/workflow_2_8.dtd\">"
				+ "<workflow>\r\n"
				+ "  <initial-actions>\r\n"
				+ "    <action id=\"1\" name=\"Create\">\r\n"
				+ "      <results>\r\n"
				+ "        <unconditional-result old-status=\"null\" status=\"open\" step=\"1\"/>\r\n"
				+ "      </results>\r\n"
				+ "    </action>\r\n"
				+ "  </initial-actions>\r\n"
				+ "  <steps>\r\n"
				+ "    <step id=\"1\" name=\"Unresolved\">\r\n"
				+ "      <meta name=\"jira.status.id\">1</meta>\r\n"
				+ "      <actions>\r\n"
				+ "        <action id=\"121\" name=\"Set resolved\">\r\n"
				+ "          <results>\r\n"
				+ "            <unconditional-result old-status=\"Not Done\" status=\"Done\" step=\"4\"/>\r\n"
				+ "          </results>\r\n"
				+ "        </action>\r\n"
				+ "      </actions>\r\n"
				+ "    </step>\r\n"
				+ "    <step id=\"4\" name=\"Resolved\">\r\n"
				+ "      <meta name=\"jira.status.id\">10001</meta>\r\n"
				+ "      <actions>\r\n"
				+ "        <action id=\"111\" name=\"Set unresolved\">\r\n"
				+ "          <results>\r\n"
				+ "            <unconditional-result old-status=\"null\" status=\"null\" step=\"1\"/>\r\n"
				+ "          </results>\r\n"
				+ "        </action>\r\n"
				+ "      </actions>\r\n"
				+ "    </step>\r\n"
				+ "  </steps>\r\n"
				+ "</workflow>";
	}
}

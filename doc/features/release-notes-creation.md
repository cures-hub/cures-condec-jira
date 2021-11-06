# Release Notes Creation

The ConDec Jira plugin enables to create release notes including explicit rationale (=decision knowledge).

See the [release notes of the ConDec Jira plugin](https://github.com/cures-hub/cures-condec-jira/releases) as an example.

For every Jira issue, a rating is calculated from the following metrics:
- number of decision knowledge elements reachable from the Jira issue (with a number of hops/link distance of 3)
- Priority (e.g., low, medium, high)
- number of comments of the Jira issue
- length of summary and description text (number of words)
- number of days that the Jira issue was open
- number of resolved Jira issues by the assignee user
- number of reported Jira issues by the reporter user



The rating is used to sort the release notes entries: entries with the highest rating are placed at the top.
You can set weights for the metrics.
For every metric, the metric value (e.g. number of comments) is multipled with the weight.
The rating is calculated as the sum of all metrics.

## Design Details

![Overview class diagram](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/diagrams/class_diagram_release_notes.png)
*Overview class diagram for the release notes creation feature*
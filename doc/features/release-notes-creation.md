# Release Notes Creation

The ConDec Jira plugin enables to create release notes 

For every Jira issue, a rating is calculated from the following metrics:
- number of decision knowledge elements reachable from the Jira issue (with a number of hops/link distance of 3)
- Priority low, medium, high
- number of comments of the Jira issue
- length of summary and description text (number of words)
- number of days that the Jira issue was open
- number of resolved Jira issues by the assignee user
- number of reported Jira issues by the reporter user



The rating is used to sort the release notes entries: entries with the highest rating are placed at the top.
You can set weights for the metrics.
For every metric, the metric value (e.g. number of comments) is multipled with the weight.
The rating is calculated as the sum of all metrics.
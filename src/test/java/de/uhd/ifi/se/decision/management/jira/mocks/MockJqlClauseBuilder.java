package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.jql.builder.ConditionBuilder;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlFieldReference;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;
import com.atlassian.query.QueryImpl;
import com.atlassian.query.clause.Clause;
import com.atlassian.query.clause.ClauseVisitor;
import com.atlassian.query.operand.Operand;
import com.atlassian.query.operator.Operator;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MockJqlClauseBuilder implements JqlClauseBuilder {

	@Override
	public JqlQueryBuilder endWhere() {
		return null;
	}

	@Override
	public Query buildQuery() {
		Clause clause = new Clause() {
			@Override
			public String getName() {
				return "BOO";
			}

			@Override
			public List<Clause> getClauses() {
				return null;
			}

			@Override
			public <R> R accept(ClauseVisitor<R> clauseVisitor) {
				return null;
			}
		};
		return new QueryImpl(clause);
	}

	@Override
	public JqlClauseBuilder clear() {
		return null;
	}

	@Override
	public JqlClauseBuilder defaultAnd() {
		return null;
	}

	@Override
	public JqlClauseBuilder defaultOr() {
		return null;
	}

	@Override
	public JqlClauseBuilder defaultNone() {
		return null;
	}

	@Override
	public JqlClauseBuilder and() {
		return null;
	}

	@Override
	public JqlClauseBuilder or() {
		return null;
	}

	@Override
	public JqlClauseBuilder not() {
		return null;
	}

	@Override
	public JqlClauseBuilder sub() {
		return null;
	}

	@Override
	public JqlClauseBuilder endsub() {
		return null;
	}

	@Override
	public JqlClauseBuilder affectedVersion(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder affectedVersion(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder affectedVersionIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder affectedVersion() {
		return null;
	}

	@Override
	public JqlClauseBuilder fixVersion(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder fixVersion(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder fixVersion(Long aLong) {
		return null;
	}

	@Override
	public JqlClauseBuilder fixVersion(Long... longs) {
		return null;
	}

	@Override
	public JqlClauseBuilder fixVersionIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder fixVersion() {
		return null;
	}

	@Override
	public JqlClauseBuilder priority(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder priority() {
		return null;
	}

	@Override
	public JqlClauseBuilder resolution(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder unresolved() {
		return null;
	}

	@Override
	public ConditionBuilder resolution() {
		return null;
	}

	@Override
	public JqlClauseBuilder status(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder status() {
		return null;
	}

	@Override
	public JqlClauseBuilder statusCategory(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder statusCategory() {
		return null;
	}

	@Override
	public JqlClauseBuilder issueType(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder issueTypeIsStandard() {
		return null;
	}

	@Override
	public JqlClauseBuilder issueTypeIsSubtask() {
		return null;
	}

	@Override
	public ConditionBuilder issueType() {
		return null;
	}

	@Override
	public JqlClauseBuilder description(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder descriptionIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder description() {
		return null;
	}

	@Override
	public JqlClauseBuilder summary(String s) {
		return null;
	}

	@Override
	public ConditionBuilder summary() {
		return null;
	}

	@Override
	public JqlClauseBuilder environment(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder environmentIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder environment() {
		return null;
	}

	@Override
	public JqlClauseBuilder comment(String s) {
		return null;
	}

	@Override
	public ConditionBuilder comment() {
		return null;
	}

	@Override
	public JqlClauseBuilder project(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder project(Long... longs) {
		return null;
	}

	@Override
	public ConditionBuilder project() {
		return null;
	}

	@Override
	public JqlClauseBuilder category(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder category() {
		return null;
	}

	@Override
	public JqlClauseBuilder createdAfter(Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder createdAfter(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder createdBetween(Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder createdBetween(String s, String s1) {
		return null;
	}

	@Override
	public ConditionBuilder created() {
		return null;
	}

	@Override
	public JqlClauseBuilder updatedAfter(Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder updatedAfter(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder updatedBetween(Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder updatedBetween(String s, String s1) {
		return null;
	}

	@Override
	public ConditionBuilder updated() {
		return null;
	}

	@Override
	public JqlClauseBuilder dueAfter(Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder dueAfter(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder dueBetween(Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder dueBetween(String s, String s1) {
		return null;
	}

	@Override
	public ConditionBuilder due() {
		return null;
	}

	@Override
	public JqlClauseBuilder lastViewedAfter(Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder lastViewedAfter(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder lastViewedBetween(Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder lastViewedBetween(String s, String s1) {
		return null;
	}

	@Override
	public ConditionBuilder lastViewed() {
		return null;
	}

	@Override
	public JqlClauseBuilder resolutionDateAfter(Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder resolutionDateAfter(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder resolutionDateBetween(Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder resolutionDateBetween(String s, String s1) {
		return null;
	}

	@Override
	public ConditionBuilder resolutionDate() {
		return null;
	}

	@Override
	public JqlClauseBuilder reporterUser(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder reporterInGroup(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder reporterIsCurrentUser() {
		return null;
	}

	@Override
	public JqlClauseBuilder reporterIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder reporter() {
		return null;
	}

	@Override
	public JqlClauseBuilder assigneeUser(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder assigneeInGroup(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder assigneeIsCurrentUser() {
		return null;
	}

	@Override
	public JqlClauseBuilder assigneeIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder assignee() {
		return null;
	}

	@Override
	public JqlClauseBuilder component(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder component(Long... longs) {
		return null;
	}

	@Override
	public JqlClauseBuilder componentIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder component() {
		return null;
	}

	@Override
	public JqlClauseBuilder labels(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder labelsIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder labels() {
		return null;
	}

	@Override
	public JqlClauseBuilder issue(String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder issueInHistory() {
		return null;
	}

	@Override
	public JqlClauseBuilder issueInWatchedIssues() {
		return null;
	}

	@Override
	public JqlClauseBuilder issueInVotedIssues() {
		return null;
	}

	@Override
	public ConditionBuilder issue() {
		return null;
	}

	@Override
	public JqlClauseBuilder issueParent(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder issueParent() {
		return null;
	}

	@Override
	public ConditionBuilder currentEstimate() {
		return null;
	}

	@Override
	public ConditionBuilder originalEstimate() {
		return null;
	}

	@Override
	public ConditionBuilder timeSpent() {
		return null;
	}

	@Override
	public ConditionBuilder workRatio() {
		return null;
	}

	@Override
	public JqlClauseBuilder level(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder level() {
		return null;
	}

	@Override
	public JqlClauseBuilder savedFilter(String... strings) {
		return null;
	}

	@Override
	public ConditionBuilder savedFilter() {
		return null;
	}

	@Override
	public ConditionBuilder votes() {
		return null;
	}

	@Override
	public JqlClauseBuilder voterUser(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder voterInGroup(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder voterIsCurrentUser() {
		return null;
	}

	@Override
	public JqlClauseBuilder voterIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder voter() {
		return null;
	}

	@Override
	public ConditionBuilder watches() {
		return null;
	}

	@Override
	public JqlClauseBuilder watcherUser(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder attachmentsExists(boolean b) {
		return null;
	}

	@Override
	public JqlClauseBuilder watcherInGroup(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder watcherIsCurrentUser() {
		return null;
	}

	@Override
	public JqlClauseBuilder watcherIsEmpty() {
		return null;
	}

	@Override
	public ConditionBuilder watcher() {
		return null;
	}

	@Override
	public ConditionBuilder field(String s) {
		return null;
	}

	@Override
	public ConditionBuilder customField(Long aLong) {
		return null;
	}

	@Override
	public JqlClauseBuilder addClause(Clause clause) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateCondition(String s, Operator operator, Date date) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateCondition(String s, Date... dates) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateCondition(String s, Operator operator, Date... dates) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateCondition(String s, Collection<Date> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateCondition(String s, Operator operator, Collection<Date> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addDateRangeCondition(String s, Date date, Date date1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, String s1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, String s1, String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, String s1, Collection<String> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, Operator operator, String s1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, Operator operator, String s1, String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFunctionCondition(String s, Operator operator, String s1, Collection<String> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, String s1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, Collection<String> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addFieldReferenceCondition(JqlFieldReference jqlFieldReference, Collection<String> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, Operator operator, String s1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, Operator operator, String... strings) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringCondition(String s, Operator operator, Collection<String> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addStringRangeCondition(String s, String s1, String s2) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Long aLong) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Long... longs) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Collection<Long> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Operator operator, Long aLong) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Operator operator, Long... longs) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberCondition(String s, Operator operator, Collection<Long> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addNumberRangeCondition(String s, Long aLong, Long aLong1) {
		return null;
	}

	@Override
	public ConditionBuilder addCondition(String s) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Operand operand) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Operand... operands) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Collection<? extends Operand> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Operator operator, Operand operand) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Operator operator, Operand... operands) {
		return null;
	}

	@Override
	public JqlClauseBuilder addCondition(String s, Operator operator, Collection<? extends Operand> collection) {
		return null;
	}

	@Override
	public JqlClauseBuilder addRangeCondition(String s, Operand operand, Operand operand1) {
		return null;
	}

	@Override
	public JqlClauseBuilder addEmptyCondition(String s) {
		return null;
	}

	@Override
	public Clause buildClause() {
		return null;
	}
}

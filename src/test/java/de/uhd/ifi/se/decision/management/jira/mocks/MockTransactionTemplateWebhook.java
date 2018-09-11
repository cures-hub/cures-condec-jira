package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class MockTransactionTemplateWebhook implements TransactionTemplate {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(TransactionCallback<T> arg0) {
        return (T) "http://true";
    }
}

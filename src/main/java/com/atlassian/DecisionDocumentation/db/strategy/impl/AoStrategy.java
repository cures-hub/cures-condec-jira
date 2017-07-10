package com.atlassian.DecisionDocumentation.db.strategy.impl;

import java.util.List;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.rest.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.model.SimpleDecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class AoStrategy implements Strategy {

	@Override
	public long createDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		return 0;
		/*
		final String description = req.getParameter("task");
        ao.executeInTransaction(new TransactionCallback<Todo>() // (1)
        {
            @Override
            public Todo doInTransaction()
            {
                final Todo todo = ao.create(Todo.class); // (2)
                todo.setDescription(description); // (3)
                todo.setComplete(false);
                todo.save(); // (4)
                return todo;
            }
        });

        res.sendRedirect(req.getContextPath() + "/plugins/servlet/todo/list");
        */
	}

	@Override
	public void editDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void deleteDecisionComponent(DecisionRepresentation dec, ApplicationUser user) {
		// TODO Auto-generated method stub
	}

	@Override
	public void createLink(LinkRepresentation link, ApplicationUser user) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteLink(LinkRepresentation link, ApplicationUser user) {
		// TODO Auto-generated method stub	
	}

	@Override
	public List<SimpleDecisionRepresentation> searchUnlinkedDecisionComponents(long id, String projectKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
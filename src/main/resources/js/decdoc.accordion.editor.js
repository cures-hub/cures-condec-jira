function addOptionsToAllDecisionComponents(parentNode) {
	for (var counter = 0; counter < simpleKnowledgeTypes.length; ++counter) {
		addOptionToDecisionComponent(simpleKnowledgeTypes[counter], parentNode);
	}
}

function addOptionToDecisionComponent(type, parentNode) {
	if (type === "Solution") {
		if (document.getElementById(type).innerHTML === "") {
			addingCreateButton(type);
		}
	} else if (type === "Argument") {
		document.getElementById(type).insertAdjacentHTML(
						'beforeend',
						'<p>Do you want to add an additional '	+ type
								+ '? <input type="radio" name="natureOfArgument" value="pro" checked="checked">Pro<input type="radio" name="natureOfArgument" value="contra">Contra<input type="radio" name="natureOfArgument" value="comment">Comment<input type="text" id="inputField'
								+ type + '" placeholder="Name of ' + type
								+ '"><input type="button" name="CreateAndLinkDecisionComponent' + type
								+ '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
		var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
		var inputField = document.getElementById("inputField" + type);
		createDecisionKnowledgeElementButton.addEventListener('click', function() {
			var summary = inputField.value;
			var description = "TODO";
			inputField.value = "";
			var argumentCheckBoxGroup = document.getElementsByName("natureOfArgument");
			for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
				if (argumentCheckBoxGroup[i].checked == true) {
					var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
					if (selectedNatureOfArgument == "pro") {
						createDecisionKnowledgeElement(summary, description, type, function(newId) {
							createLink(parentNode.id, newId, "support", function() {
								buildTreeViewer(getProjectKey(), newId);
							});
						});
					} else if (selectedNatureOfArgument == "contra") {
						createDecisionKnowledgeElement(summary, description, type, function(newId) {
							createLink(parentNode.id, newId, "attack", function() {
								buildTreeViewer(getProjectKey(), newId);
							});
						});
					} else if (selectedNatureOfArgument == "comment") {
						createDecisionKnowledgeElement(summary, description, type, function(newId) {
							createLink(parentNode.id, newId, "comment", function() {
								buildTreeViewer(getProjectKey(), newId);
							});
						});
					}
				}
			}
		});
	} else {
		addingCreateButton(type);
	}
}

function addingCreateButton(type) {
    document.getElementById(type).insertAdjacentHTML(
        'beforeend',
        '<p>Do you want to add an additional ' + type + '?<input type="text" id="inputField' + type
        + '" placeholder="Name of ' + type
        + '"><input type="button" name="CreateAndLinkDecisionComponent' + type
        + '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
    var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
    var inputField = document.getElementById("inputField" + type);
    createDecisionKnowledgeElementButton.addEventListener('click', function() {
        var summary = inputField.value;
        var description = "TODO";
        inputField.value = "";
        createDecisionKnowledgeElement(summary, description, type, function(newId) {
            createLink(parentNode.id, newId, "contain", function() {
                buildTreeViewer(getProjectKey(), newId);
            });
        });
    });
}

function fillAccordion(data, projectKey, node) {
	var detailsElement = document.getElementById("Details");
	detailsElement
			.insertAdjacentHTML(
					'beforeend',
					'<p>'
							+ node.type
							+ ' / '
							+ node.summary
							+ ' <input type="button" name="updateIssue" id="updateIssue" value="Update"/></p>'
							+ '<p><textarea id="IssueDescription" style="width:99%; height:auto;border: 1px solid rgba(204,204,204,1); ">'
							+ node.description + '</textarea></p>');
	detailsElement.style.display = "block";
	var updateButton = document.getElementById("updateIssue");
	updateButton.addEventListener('click', function() {
		editDecisionComponent(node.id, node.summary, document.getElementById("IssueDescription").value, function() {
			buildTreeViewer(projectKey, node.id);
		});
	});

	getUnlinkedDecisionComponents(
			node.id,
			projectKey,
			function(unlinkedDecisionComponents) {
				var insertString = '<select name="linkExistingIssueSearchField">';
				for (var index = 0; index < unlinkedDecisionComponents.length; index++) {
					insertString += '<option value="' + unlinkedDecisionComponents[index].id + '">'
							+ unlinkedDecisionComponents[index].text + '</option>';
				}
				insertString += '</select><input type="button" name="linkExistingIssueButton" id="linkExistingIssueButton" value="Create Link"/>';
				document.getElementById("Details").insertAdjacentHTML('beforeend', insertString);
				var linkButton = document.getElementById("linkExistingIssueButton");
				linkButton
						.addEventListener(
								'click',
								function() {
									createLink(
											node.id,
											$('select[name="linkExistingIssueSearchField"] option:selected').val() /*$("#linkExistingIssueSearchField")[0].value*/,
											"contain", function() {
												buildTreeViewer(projectKey, node.key);
											});
									singleSelect.value = '';
									window.location.reload();
								});
			});
	if (data.node.children.length > 0) {
		for (var counter = 0; counter < data.node.children.length; ++counter) {
			var child = $('#evts').jstree(true).get_node(data.node.children[counter]);
			var type = child.data.type;
			for (var i = 0; i < simpleKnowledgeTypes.length; i++) {
				if (simpleKnowledgeTypes[i].toLocaleLowerCase() === type.toLocaleLowerCase()) {
					document.getElementById(simpleKnowledgeTypes[i]).insertAdjacentHTML(
							'beforeend',
							'<div class="issuelinkbox"><p>' + child.data.type + ' / ' + child.data.summary + '</p>'
									+ '<p>Description: ' + child.data.description + '</p></div>');
					document.getElementById(simpleKnowledgeTypes[i]).style.display = "block";
				}
			}
		}
		addOptionsToAllDecisionComponents(data.node.data);
	} else {
		addOptionsToAllDecisionComponents(data.node.data);
	}
}

function deleteContentOfAccordionEditor() {
	var details = document.getElementById("Details");
	clearInner(details);
	details.innerHTML = "";	
	for (var index = 0; index < simpleKnowledgeTypes.length; index++) {
		document.getElementById(simpleKnowledgeTypes[index]).innerHTML = "";
		document.getElementById(simpleKnowledgeTypes[index]).style.display = "none";
	}
}

function clearInner(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
}

function clear(node) {
	while (node.hasChildNodes()) {
		clear(node.firstChild);
	}
	node.parentNode.removeChild(node);
}
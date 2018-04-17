function addOptionsToAllDecisionComponents(parentNode) {
	var types = [ "Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption",
			"Constraint", "Implication", "Assessment", "Argument" ];
	for (var counter = 0; counter < types.length; ++counter) {
		addOptionToDecisionComponent(types[counter], parentNode);
	}
}
function addOptionToDecisionComponent(type, parentNode) {
	if (type === "Solution") {
		if (document.getElementById(type).innerHTML === "") {
			document.getElementById(type).insertAdjacentHTML(
					'beforeend',
					'<p>Do you want to add an additional ' + type + '? <input type="text" id="inputField' + type
							+ '" placeholder="Name of ' + type
							+ '"><input type="button" name="CreateAndLinkDecisionComponent' + type
							+ '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
			var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
			var inputField = document.getElementById("inputField" + type);
			createDecisionKnowledgeElementButton.addEventListener('click', function() {
				var summary = inputField.value;
				inputField.value = "";
				var description = "TODO";
				createDecisionKnowledgeElement(summary, description, type, function(newId) {
					createLink(parentNode.id, newId, "contain", function() {
						buildTreeViewer(getProjectKey(), newId);
					});
				});
			});
		}
	} else if (type === "Argument") {
		document
				.getElementById(type)
				.insertAdjacentHTML(
						'beforeend',
						'<p>Do you want to add an additional '
								+ type
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
				if (argumentCheckBoxGroup[i].checked === true) {
					var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
					if (selectedNatureOfArgument === "pro") {
						createDecisionKnowledgeElement(summary, description, type, function(newId) {
							createLink(parentNode.id, newId, "support", function() {
								buildTreeViewer(getProjectKey(), newId);
							});
						});
					} else if (selectedNatureOfArgument === "contra") {
						createDecisionKnowledgeElement(summary, description, type, function(newId) {
							createLink(parentNode.id, newId, "attack", function() {
								buildTreeViewer(getProjectKey(), newId);
							});
						});
					} else if (selectedNatureOfArgument === "comment") {
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
		document.getElementById(type).insertAdjacentHTML(
				'beforeend',
				'<p>Do you want to add an additional ' + type + '?<input type="text" id="inputField' + type
						+ '" placeholder="Name of ' + type
						+ '"><input type="button" name="CreateAndLinkDecisionComponent' + type
						+ '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
		var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
		createDecisionKnowledgeElementButton.addEventListener('click', function() {
			var inputField = document.getElementById("inputField" + type);
			var summary = inputField.value;
			var description = "TODO";
			inputField.value = "";
			createDecisionKnowledgeElement(summary, description, type, function(newId) {
				console.log(parentNode);
				console.log(newId);
				createLink(parentNode.id, newId, "contain", function() {
					buildTreeViewer(getProjectKey(), newId);
				});
			});
		});
	}
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
			var array = [ "Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption",
					"Constraint", "Implication", "Assessment", "Argument" ];
			for (var i = 0; i < array.length; i++) {
				if (array[i].toLocaleLowerCase() === type.toLocaleLowerCase()) {
					document.getElementById(array[i]).insertAdjacentHTML(
							'beforeend',
							'<div class="issuelinkbox"><p>' + child.data.type + ' / ' + child.data.summary + '</p>'
									+ '<p>Description: ' + child.data.description + '</p></div>');
					document.getElementById(array[i]).style.display = "block";
				}
			}
		}
		addOptionsToAllDecisionComponents(data.node.data);
	} else {
		addOptionsToAllDecisionComponents(data.node.data);
	}
}

/* Deletes all content from Accordion */
function setBack(text) {
	var details = document.getElementById("Details");
	clearInner(details);
	details.innerHTML = text;
	var problem = document.getElementById("Problem");
	clearInner(problem);
	problem.innerHTML = text;
	document.getElementById("Problem").style.display = "none";
	document.getElementById("Issue").innerHTML = text;
	document.getElementById("Issue").style.display = "none";
	document.getElementById("Goal").innerHTML = text;
	document.getElementById("Goal").style.display = "none";
	document.getElementById("Solution").innerHTML = text;
	document.getElementById("Solution").style.display = "none";
	document.getElementById("Alternative").innerHTML = text;
	document.getElementById("Alternative").style.display = "none";
	document.getElementById("Claim").innerHTML = text;
	document.getElementById("Claim").style.display = "none";
	document.getElementById("Context").innerHTML = text;
	document.getElementById("Context").style.display = "none";
	document.getElementById("Assumption").innerHTML = text;
	document.getElementById("Assumption").style.display = "none";
	document.getElementById("Constraint").innerHTML = text;
	document.getElementById("Constraint").style.display = "none";
	document.getElementById("Implication").innerHTML = text;
	document.getElementById("Implication").style.display = "none";
	document.getElementById("Assessment").innerHTML = text;
	document.getElementById("Assessment").style.display = "none";
	document.getElementById("Argument").innerHTML = text;
	document.getElementById("Argument").style.display = "none";
	document.getElementById("treant-container").innerHTML = text;
}
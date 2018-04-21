function addOptionsToAllDecisionComponents(parentNode) {
	for (var index = 0; index < simpleKnowledgeTypes.length; index++) {
		addOptionToDecisionComponent(simpleKnowledgeTypes[index], parentNode);
	}
}

function setUpEditorContent(type) {
	var radioButtons = "";
	if (type == "Argument") {
		var radioButtons = '<input type="radio" name="form-radio-argument" value="Pro Argument" checked="checked">Pro'
				+ '<input type="radio" name="form-radio-argument" value="Contra Argument">Contra'
				+ '<input type="radio" name="form-radio-argument" value="Comment">Comment';
	}
	document.getElementById(type).insertAdjacentHTML(
			'beforeend',
			'<p>Do you want to add an additional ' + type + '? ' + radioButtons
					+ '<input type="text" id="form-input-summary' + type + '" placeholder="Summary of ' + type
					+ '"> <input type="submit" id="form-input-submit' + type + '" value="Add ' + type + '"/></p>');
}

function addOptionToDecisionComponent(type, parentNode) {
	setUpEditorContent(type);
	var submitButton = document.getElementById("form-input-submit" + type);
	submitButton.addEventListener("click", function() {
		var summary = document.getElementById("form-input-summary" + type).value;
		var description = "TODO";
		console.log(submitButton.id);
		if (submitButton.id === "form-input-submitArgument") {
			type = $('input[name=form-radio-argument]:checked').val();
		}
		createDecisionKnowledgeElementAsChild(summary, description, type, parentNode.id);
		document.getElementById("form-input-summary" + type).value = "";
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
				linkButton.addEventListener('click', function() {
					var childId = $('select[name="linkExistingIssueSearchField"] option:selected').val();
					createLinkToExistingElement(node.id, childId);
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
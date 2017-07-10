var getJSON = function(url, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", url, true);
	xhr.setRequestHeader("Content-type", "application/json", "charset=utf-8");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status == 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send();
};
var postJSON = function(url, data, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-type", "application/json", "charset=utf-8");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status == 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send(JSON.stringify(data));
};
var putJSON = function(url, data, callback) {
	var xhr = new XMLHttpRequest();
	xhr.open("PUT", url, true);
	xhr.setRequestHeader("Content-type", "application/json", "charset=utf-8");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.responseType = "json";
	xhr.onload = function() {
		var status = xhr.status;
		if (status == 200) {
			callback(null, xhr.response);
		} else {
			callback(status);
		}
	};
	xhr.send(JSON.stringify(data));
};

var createDecisionComponent = function(summary, issueType, callback){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	if(summary != ""){
		var jsondata = {
			"projectKey": projectKey,
			"name": summary,
			"type": issueType
		};
		postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=create", jsondata, function(err, data) {
			if (err!=null){
				var errorFlag = AJS.flag({
					type: 'error',
					close: 'auto',
					title: 'Error',
					body: issueType + ' has not been created. Error Code: ' + err
				});
			} else {
				callback(data);
			}
		});
	} else {
		//summary is empty
	}
}
var editDecisionComponent = function(issueId, summary, description){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	var jsondata = {
		"id": issueId,
		"name": summary,
		"projectKey": projectKey,
		"description": description
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=edit", jsondata, function(err, data) {
		if (err!=null){
		} else {
		}
	});
}
var deleteDecisionComponent = function(issueId){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	var jsondata = {
		"id": issueId,
		"projectKey": projectKey
	};
	postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=delete", jsondata, function(err, data) {
		if (err!=null){
		} else {
		}
	});
}
var createLink = function(parentId, childId, linkType, callback){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	var jsondata = {
		"linkType": linkType,
		"ingoingId": childId,
		"outgoingId": parentId
	};
	putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + "&actionType=create", jsondata, function(err, data) {
		if (err!=null){
			var errorFlag = AJS.flag({
				type: 'error',
				close: 'auto',
				title: 'Error',
				body: 'IssueLink could not be created'
			});
		} else {
			callback(data);
		}
	});
}
var deleteLink = function(parentId, childId, linkType, callback){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	var jsondata = {
		"linkType": linkType,
		"ingoingId": childId,
		"outgoingId": parentId
	};
	putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + "&actionType=delete", jsondata, function(err, data) {
		if (err!=null){
			var errorFlag = AJS.flag({
				type: 'error',
				close: 'auto',
				title: 'Error',
				body: 'IssueLink could not be deleted'
			});
		} else {
			callback(data);
		}
	});
}


var addOptionsToAllDecisionComponents = function(parentKey){
	var issueTypes = ["Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption", "Constraint", "Implication", "Assessment", "Argument"];
	for (counter = 0; counter < issueTypes.length; ++ counter){
		addOptionToDecisionComponent(issueTypes[counter],parentKey);
	}
};
var addOptionToDecisionComponent = function(type, parentId){
	if (type == "Solution"){
		if(document.getElementById(type).innerHTML == ""){
			document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '?<input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type+ '" id="CreateAndLinkDecisionComponent' + type+ '" value="Add ' + type + '"/></p>');
		var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
		var inputField = document.getElementById("inputField" + type);
		createDecisionComponentButton.addEventListener('click', function(event){
			var tempString = inputField.value;
			inputField.value = "";
			createDecisionComponent(tempString, type, function(data){
				var successFlag = AJS.flag({
					type: 'success',
					close: 'auto',
					title: 'Success',
					body: type + ' has been created.'
				});
				createLink(parentId, data.id, "contain", function(data){
					var successFlag = AJS.flag({
						type: 'success',
						close: 'auto',
						title: 'Success',
						body: 'IssueLink has been created.'
					});
				});
			});
		});
		}
	} else if (type == "Argument"){
		document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '? <input type="radio" name="natureOfArgument" value="pro" checked="checked">Pro<input type="radio" name="natureOfArgument" value="contra">Contra<input type="radio" name="natureOfArgument" value="comment">Comment<input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type+ '" id="CreateAndLinkDecisionComponent' + type+ '" value="Add ' + type + '"/></p>');
		var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
		var inputField = document.getElementById("inputField" + type);		
		createDecisionComponentButton.addEventListener('click', function(event){
			var tempString = inputField.value;
			inputField.value = "";
			var argumentCheckBoxGroup = document.getElementsByName("natureOfArgument");
			for(var i = 0; i < argumentCheckBoxGroup.length; i++) {
			   if(argumentCheckBoxGroup[i].checked == true) {
				   var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
				   if (selectedNatureOfArgument == "pro"){
						createDecisionComponent(tempString, type, function(data){
							var successFlag = AJS.flag({
								type: 'success',
								close: 'auto',
								title: 'Success',
								body: type + ' has been created.'
							});
							createLink(parentId, data.id, "support", function(data){
								var successFlag = AJS.flag({
									type: 'success',
									close: 'auto',
									title: 'Success',
									body: 'IssueLink has been created.'
								});
							});
						});
				   } else if (selectedNatureOfArgument == "contra"){
						createDecisionComponent(tempString, type, function(data){
							var successFlag = AJS.flag({
								type: 'success',
								close: 'auto',
								title: 'Success',
								body: type + ' has been created.'
							});
							createLink(parentId, data.id, "attack", function(data){
								var successFlag = AJS.flag({
									type: 'success',
									close: 'auto',
									title: 'Success',
									body: 'IssueLink has been created.'
								});
							});
						});
				   } else if (selectedNatureOfArgument == "comment"){
						createDecisionComponent(tempString, type, function(data){
							var successFlag = AJS.flag({
								type: 'success',
								close: 'auto',
								title: 'Success',
								body: type + ' has been created.'
							});
							createLink(parentId, data.id, "comment", function(data){
								var successFlag = AJS.flag({
									type: 'success',
									close: 'auto',
									title: 'Success',
									body: 'IssueLink has been created.'
								});
							});
						});
				   } else {
					   
				   }
			   }
			 }
		});
	} else {
		document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '?<input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type+ '" id="CreateAndLinkDecisionComponent' + type+ '" value="Add ' + type + '"/></p>');
		var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);	
		createDecisionComponentButton.addEventListener('click', function(event){
			var inputField = document.getElementById("inputField" + type);
			var tempString = inputField.value;
			inputField.value = "";
			createDecisionComponent(tempString, type, function(data){
				var successFlag = AJS.flag({
					type: 'success',
					close: 'auto',
					title: 'Success',
					body: type + ' has been created.'
				});
				createLink(parentId, data.id, "contain", function(data){
					var successFlag = AJS.flag({
						type: 'success',
						close: 'auto',
						title: 'Success',
						body: 'IssueLink has been created.'
					});
				});
			});
		});
	}
};


var initializeSite = function(){
	var pathname = window.location.pathname;
	var stringArray = pathname.split("/");
	var projectKey = stringArray[stringArray.length-1];
	var treeJSONUrl = AJS.contextPath() + "/rest/treeviewerrest/latest/treeviewer.json?projectKey=" + projectKey;
	getJSON(treeJSONUrl, function(err, data) {
		if (err!=null){
			displayGetJsonError(err);
		} else {
			$('#evts')
				.on("select_node.jstree", function (e,data) {
					setBack();
					var nodeKey = data.node.data.key;
					var detailsElement = document.getElementById("Details");
					detailsElement.insertAdjacentHTML('beforeend', '<p><a href="' +
						AJS.contextPath() + '/browse/' + nodeKey + '">' + nodeKey + 
						' / ' + data.node.data.summary + '</a><input type="button" name="updataIssue" id="updateIssue" value="Update Issue"/></p>' + 
						'<p><textarea id="IssueDescription" style="width:99%; height:auto;border: 1px solid rgba(204,204,204,1); ">' + 
						data.node.data.description + '</textarea></p>'
						);
					detailsElement.style.display = "block";
					var updateButton = document.getElementById("updateIssue");
					updateButton.addEventListener('click', function(event){
						editDecisionComponent(data.node.data.id, data.node.data.summary, document.getElementById("IssueDescription").value);
					});
					
					getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + '&issueId=' + data.node.data.id, function(err, data) {
						if (err!=null){
							displayGetJsonError(err);
						} else {
							document.getElementById("Details").insertAdjacentHTML('beforeend', '<input type="text" id="linkExistingIssueSearchField" placeholder="Link to existing Issue..."/>' + 
								'<input type="button" name="linkExistingIssueButton" id="linkExistingIssueButton" value="Create Link"/>');
							var singleSelect = $("#linkExistingIssueSearchField").select2({
								data: data,
							});
							document.getElementById("linkExistingIssueSearchField").style.display = "block";
							var linkButton = document.getElementById("linkExistingIssueButton");
							linkButton.addEventListener('click', function(event){ 
								var arrayOfSelectedIssues = $('#linkExistingIssueSearchField').select2('data');
								var divider = arrayOfSelectedIssues.text.indexOf(" / ");
								createIssueLink("contain", nodeKey,arrayOfSelectedIssues.text.substring(0, divider), AJS.contextPath());
								singleSelect.select2('val', '');
								window.location.reload();
							});
						}
					});
					if(data.node.children.length > 0){
						for(counter = 0; counter < data.node.children.length; ++counter){
							var child = $('#evts').jstree(true).get_node(data.node.children[counter]);
							var issueType = child.data.issueType;
							var array= ["Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption", "Constraint", "Implication", "Assessment", "Argument"];
							if(array.indexOf(issueType)!=-1){
								document.getElementById(issueType).insertAdjacentHTML('beforeend', '<div class="issuelinkbox"><p><a href="' +
									AJS.contextPath() + '/browse/' + child.data.key + '">' + child.data.key +
									' / ' + child.data.summary + '</a></p>' + '<p>Description: ' + child.data.description + '</p></div>'
								);
								document.getElementById(child.data.issueType).style.display = "block";
							}
						}
						addOptionsToAllDecisionComponents(data.node.data.id);
					} else {
						addOptionsToAllDecisionComponents(data.node.data.id);
					}

					var depthOfTree = document.getElementById("depthOfTreeInput").value;
					var treantUrl = AJS.contextPath() + "/rest/treantsrest/latest/treant.json?projectKey=" + projectKey + "&issueKey=" + data.node.data.key + "&depthOfTree=" + depthOfTree;
					getJSON(treantUrl, function(err, data) {
						if (err!=null){
							document.getElementById("treant-container").innerHTML = "Fehler beim Abfragen der Daten";
						} else {
							var chart_config = data;
							document.getElementById("treant-container").innerHTML="";
							var myTreant = new Treant(chart_config);
						}
					});
				})
				.jstree(data);
			document.getElementById("Details").style.display = "block";
				/*Suchfeld Binding*/
			$(".search-input").keyup(function() {
					var searchString = $(this).val();
					$('#evts').jstree(true).search(searchString);
				});
		}
	});
	/*ClickHandler for accordionelements*/
	$(document).ready(function(){
			$("dt").click(function(){
				$(this).next("dd").slideToggle("fast");
			});
		});
	/*ClickHandler for the creation of decisions*/
	var createDecisionButton = document.getElementById("CreateDecision");
	var DecisionInputField = document.getElementById("DecisionInputField");
	createDecisionButton.addEventListener('click', function(event){
		var tempDecString = DecisionInputField.value;
		DecisionInputField.value = "";
		createDecisionComponent(tempDecString, "Decision", function(data){
			/*TODO data verarbeiten*/
			var successFlag = AJS.flag({
				type: 'success',
				close: 'auto',
				title: 'Success',
				body: 'Decision has been created'
			});
			location.reload();
		});
	});
	/*ClickHandler for the Editor Button*/
	var viewEditorButton = document.getElementById("view-editor");
	viewEditorButton.addEventListener('click', function(event){
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		editorContainer.style.display="block";
		treantContainer.style.visibility = "hidden";
	});
	/*ClickHandler for the Tree Button*/
	var viewTreeButton = document.getElementById("view-tree");
	viewTreeButton.addEventListener('click', function(event){
		var editorContainer = document.getElementById("container");
		var treantContainer = document.getElementById("treant-container");
		treantContainer.style.visibility = "visible";
		editorContainer.style.display = "none";
	});
	var DepthOfTreeInput = document.getElementById("depthOfTreeInput");
	DepthOfTreeInput.addEventListener('input', function (evt) {
		var DepthOfTreeWarningLabel = document.getElementById("DepthOfTreeWarning");
		if(this.value > 0){
			DepthOfTreeWarningLabel.style.visibility = "hidden";
		} else {
			DepthOfTreeWarningLabel.style.visibility = "visible";
		}
	});
}

/* Displays Error Message in Accoridon */
var displayGetJsonError = function(errorCode){
	document.getElementById("Details").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Problem").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Issue").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Goal").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Solution").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Alternative").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Claim").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Context").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Assumption").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Constraint").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Implication").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Assessment").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
	document.getElementById("Argument").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
};
/* Deletes all content from Accordion */
var setBack = function(){
	document.getElementById("Details").innerHTML = "";
	document.getElementById("Problem").innerHTML = "";
	document.getElementById("Problem").style.display = "none";
	document.getElementById("Issue").innerHTML = "";
	document.getElementById("Issue").style.display = "none";
	document.getElementById("Goal").innerHTML = "";
	document.getElementById("Goal").style.display = "none";
	document.getElementById("Solution").innerHTML = "";
	document.getElementById("Solution").style.display = "none";
	document.getElementById("Alternative").innerHTML = "";
	document.getElementById("Alternative").style.display = "none";
	document.getElementById("Claim").innerHTML = "";
	document.getElementById("Claim").style.display = "none";
	document.getElementById("Context").innerHTML = "";
	document.getElementById("Context").style.display = "none";
	document.getElementById("Assumption").innerHTML = "";
	document.getElementById("Assumption").style.display = "none";
	document.getElementById("Constraint").innerHTML = "";
	document.getElementById("Constraint").style.display = "none";
	document.getElementById("Implication").innerHTML = "";
	document.getElementById("Implication").style.display = "none";
	document.getElementById("Assessment").innerHTML = "";
	document.getElementById("Assessment").style.display = "none";
	document.getElementById("Argument").innerHTML = "";
	document.getElementById("Argument").style.display = "none";
}
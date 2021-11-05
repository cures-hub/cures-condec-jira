(function(global) {
	var ConDecReleaseNotesDialog = function() {
		this.configuration = {};
	};

	ConDecReleaseNotesDialog.prototype.showCreateReleaseNoteDialog = function() {
		var releaseNoteDialog = document.getElementById("create-release-note-dialog");
		var configurationSubmitButton = document.getElementById("create-release-note-submit-button");
		var useSprintSelect = document.getElementById("useSprint");
		var titleInput = document.getElementById("title");
		var sprintOptions = document.getElementById("selectSprints");
		var useReleaseSelect = document.getElementById("useReleases");
		var releaseOptions = document.getElementById("selectReleases");

		var titleWasChanged = false;
		var editor;

		var metricNames = {
			"decision_knowledge_count": "#Decision Knowledge",
			"priority": "Priority",
			"comment_count": "#Comments",
			"size_description": "#Words Description",
			"size_summary": "#Words Summary",
			"days_completion": "#Days to completion",
			"experience_resolver": "Experience Resolver",
			"experience_reporter": "Experience Reporter"
		};

		AJS.tabs.setup();

		removeListItemIssues();
		AJS.tabs.change(jQuery('a[href="#tab-configuration"]'));
		makeAsyncCalls();
		var sprintsArray;
		var releasesArray = [];
		removeEditor();

		function removeEditor() {
			var editorDiv = document.getElementById("create-release-note-dialog-contain-editor");
			editorDiv.parentNode.removeChild(editorDiv);
			$("#create-release-note-dialog-contain-editor-content").append("<div id='create-release-note-dialog-contain-editor'>" +
				"<textarea id='create-release-note-textarea'></textarea></div>")
		}

		function removeListItemIssues() {
			var listItem = document.getElementById("listItemTabIssues");
			if (listItem) {
				listItem.remove();
			}
		}

		function prefillDateBox() {
			var today = new Date();
			var twoWeeksAgo = new Date(today.getTime() - 12096e5);
			var todayString = today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2) + '-' + ('0' + today.getDate()).slice(-2);
			var twoWeeksAgoString = twoWeeksAgo.getFullYear() + '-' + ('0' + (twoWeeksAgo.getMonth() + 1)).slice(-2) + '-' + ('0' + twoWeeksAgo.getDate()).slice(-2);
			document.getElementById("start-range").value = twoWeeksAgoString;
			document.getElementById("final-range").value = todayString;
		}

		function makeAsyncCalls() {
			// load sprints
			var sprintPromise = conDecReleaseNotesAPI.getSprintsByProject().then(function(sprints) {
				sprints.map(function(sprint) {
					sprintsArray = sprint.values;
					if (sprintsArray && sprintsArray.length) {
						$('#selectSprints').empty();
						sprintsArray.forEach(function(sprint) {
							if (sprint && sprint.startDate && sprint.endDate) {
								$('#selectSprints').append('<option class="sprint-option" value="' + sprint.id + '">' + sprint.name + '</option>');
							} else {
								$('#selectSprints').append('<option class="sprint-option" disabled value="' + sprint.id + '">' + sprint.name + '</option>');
							}
						});
					}
				});
			}).catch(function(err) {
				conDecAPI.showFlag("info", "No sprints could be loaded. " + err);
			});

			// load issue types
			var issueTypePromise = conDecReleaseNotesAPI.getIssueTypes().then(function(issueTypes) {
				conDecReleaseNotesAPI.getReleaseNotesConfiguration().then(function(releaseNotesConfig) {
					manageIssueTypes(issueTypes, releaseNotesConfig);
					addJiraIssueMetrics(releaseNotesConfig.jiraIssueMetricWeights);
				});
			});

			var releasesPromise = conDecReleaseNotesAPI.getReleases().then(function(releases) {
				var releaseSelector = $('#selectReleases');
				releases.map(function(release) {
					if (release && release.startDate.iso && release.releaseDate.iso) {
						releaseSelector.append('<option value="' + release.id + '">' + release.name + '</option>');
						releasesArray.push(release);
					} else {
						releaseSelector.append('<option disabled value="' + release.id + '">' + release.name + '</option>');
					}
				});
			});

			Promise.all([sprintPromise, issueTypePromise, releasesPromise])
				.finally(function() {
					AJS.dialog2(releaseNoteDialog).show();
					prefillDateBox();
				});
		}

		function manageIssueTypes(issueTypes, releaseNotesConfig) {
			if (issueTypes && issueTypes.length) {
				// empty lists
				var bugSelector = $("#multipleBugs");
				var featureSelector = $("#multipleFeatures");
				var improvementSelector = $("#multipleImprovements");
				bugSelector.empty();
				featureSelector.empty();
				improvementSelector.empty();
				issueTypes.map(function(issueType) {
					var bugSelected = false;
					var bugString = '<option value="' + issueType.name + '"';
					var featureSelected = false;
					var featureString = '<option value="' + issueType.name + '"';
					var improvementSelected = false;
					var improvementString = '<option value="' + issueType.name + '"';
					if (releaseNotesConfig) {
						if (releaseNotesConfig.jiraIssueTypesForBugFixes) {
							bugSelected = releaseNotesConfig.jiraIssueTypesForBugFixes.indexOf(issueType.name) > -1;
						}
						if (releaseNotesConfig.jiraIssueTypesForNewFeatures) {
							featureSelected = releaseNotesConfig.jiraIssueTypesForNewFeatures.indexOf(issueType.name) > -1;
						}
						if (releaseNotesConfig.jiraIssueTypesForImprovements) {
							improvementSelected = releaseNotesConfig.jiraIssueTypesForImprovements.indexOf(issueType.name) > -1;
						}
					}
					if (bugSelected) {
						bugString += "selected";
					}
					if (featureSelected) {
						featureString += "selected";
					}
					if (improvementSelected) {
						improvementString += "selected";
					}
					bugSelector.append(bugString + '>' + issueType.name + '</option>');
					featureSelector.append(featureString + '>' + issueType.name + '</option>');
					improvementSelector.append(improvementString + '>' + issueType.name + '</option>');
				})
			}
		}

		function addTabAndChangeToIt(tabId, title) {
			var listItem = document.getElementById("listItemTab" + tabId);
			if (!listItem) {
				$("#tab-list-menu").append("<li class='menu-item' id='listItemTab" + tabId + "'><a href='#" + tabId + "'>" + title + "</a></li>");
			}
			AJS.tabs.setup();
			AJS.tabs.change(jQuery('a[href="#' + tabId + '"]'));
		}

		function addJiraIssueMetrics(listOfCriteria) {
			var keys = Object.keys(listOfCriteria);
			var elementToAppend = $("#metricWeight");
			elementToAppend.empty();
			for (i = 0; i < keys.length; i++) {
				currentKey = keys[i];
				currentMetricName = metricNames[currentKey.toLowerCase()];
				elementToAppend.append("<div class='field-group'>" +
					"<label>" + currentMetricName + "</label>" +
					"<input class='text short-field' type='number' value='" + listOfCriteria[keys[i]] +
					"' max='10' min='0' id='" + currentKey.toLowerCase() + "'>" +
					"</div>");
			}
		}

		useSprintSelect.onchange = function() {
			setSprintOrReleaseOption("selectSprints");
		};
		sprintOptions.onchange = function() {
			setSprintOrReleaseOption("selectSprints");
		};
		useReleaseSelect.onchange = function() {
			setSprintOrReleaseOption("selectReleases")
		};
		releaseOptions.onchange = function() {
			setSprintOrReleaseOption("selectReleases")
		};

		function setSprintOrReleaseOption(selectId) {
			if (!titleWasChanged) {
				var options = document.getElementById(selectId).options;
				for (var i = 0; i < options.length; i++) {
					if (options[i].selected) {
						titleInput.value = options[i].innerText;
					}
				}
			}
		}

		titleInput.onchange = function() {
			titleWasChanged = titleInput.value;
		};

		configurationSubmitButton.onclick = function() {
			var startDate = $("#start-range").val();
			var endDate = $("#final-range").val();
			var useSprints = $("#useSprint").prop("checked");
			var useReleases = $("#useReleases").prop("checked");
			var selectedSprint = parseInt($("#selectSprints").val()) || "";
			var selectedRelease = parseInt($("#selectReleases").val()) || "";
			// first check : both dates have to be selected or a sprint or
			// releases and the checkbox
			if ((!useSprints) && (!useReleases) && (!!startDate === false || !!endDate === false)) {
				conDecAPI.showFlag("error", "The time range has to be selected.");
				return;
			}

			function getStartAndEndDate() {
				var result = { startDate: "", endDate: "" };
				if (useReleases && releasesArray && releasesArray.length) {
					var selectedReleaseWithDates = releasesArray.filter(function(release) {
						return release.id = selectedRelease;
					});
					if (selectedReleaseWithDates && selectedReleaseWithDates.length) {
						result.startDate = selectedReleaseWithDates[0].startDate.iso;
						result.endDate = selectedReleaseWithDates[0].releaseDate.iso;
					} else {
						conDecAPI.showFlag("error", "Something went wrong with the release selection");
						return false;
					}
				} else if (useSprints && sprintsArray && sprintsArray.length) {
					// get dates of selected sprint			
					var selectedSprintWithDates = sprintsArray.filter(function(sprint) {
						return sprint.id === selectedSprint;
					});
					if (selectedSprintWithDates && selectedSprintWithDates.length) {
						var formattedStartDate = formatSprintDate(selectedSprintWithDates[0].startDate);
						var formattedEndDate = formatSprintDate(selectedSprintWithDates[0].endDate);
						if (formattedStartDate && formattedEndDate) {
							result.startDate = formattedStartDate;
							result.endDate = formattedEndDate;
						} else {
							conDecAPI.showFlag("error", "Neither a sprint was selected or start dates were filled");
							return false;
						}
					} else {
						conDecAPI.showFlag("error", "Neither a sprint was selected or start dates were filled");
						return false;
					}
				} else if (!!startDate && !!endDate) {
					result.startDate = startDate;
					result.endDate = endDate;
				} else {
					conDecAPI.showFlag("error", "Neither a sprint was selected or start dates were filled");
					return false;
				}
				return result;
			}

			function formatSprintDate(date) {
				var dateFormat = new Date(date);
				if (typeof dateFormat.getFullYear === "function") {
					var month = dateFormat.getMonth() + 1;
					return dateFormat.getFullYear() + "-" + month + "-" + dateFormat.getDate();
				}
			}

			function getJiraIssueMetric(metricNames) {
				var keys = Object.keys(metricNames);
				var result = {};
				for (i = 0; i < keys.length; i++) {
					var value = $("#" + keys[i]).val();
					var key = keys[i].toUpperCase();
					result[key] = value;
				}
				return result;
			}

			var timeRange = getStartAndEndDate();
			if (!timeRange) {
				return;
			}

			var jiraIssueMetricWeights = getJiraIssueMetric(metricNames);
			var bugFixes = $("#multipleBugs").val();
			var features = $("#multipleFeatures").val();
			var improvements = $("#multipleImprovements").val();
			var title = $("#release-notes-title").val();

			configuration = {
				title: title,
				startDate: timeRange.startDate,
				endDate: timeRange.endDate,
				sprintId: selectedSprint,
				jiraIssueTypesForBugFixes: bugFixes,
				jiraIssueTypesForNewFeatures: features,
				jiraIssueTypesForImprovements: improvements,
				jiraIssueMetricWeights: jiraIssueMetricWeights
			};

			conDecReleaseNotesAPI.proposeElements(configuration).then(function(releaseNotes) {
				addTabAndChangeToIt("tab-issues", "Suggested Jira Issues");
				conDecReleaseNotesDialog.releaseNotes = releaseNotes;
				console.log(releaseNotes);
				showTables(releaseNotes);
				showTitle(configuration.title);
			});

			function showTables(releaseNotes) {
				// first remove old tables
				$("#displayIssueTables").empty();
				showTable("Improvements", releaseNotes.improvements);
				showTable("NewFeatures", releaseNotes.newFeatures);
				showTable("BugFixes", releaseNotes.bugFixes);
			}

			function showTitle(title) {
				$("#suggestedIssuesTitle").text("Suggested Elements for Release Notes: " + title);
			}

			function showTable(category, proposedReleaseNotesEntries) {
				if (proposedReleaseNotesEntries.length === 0) {
					return;
				}
				var divToAppend = $("#displayIssueTables");
				var title = "<h3>" + category + "</h3>";
				var table = "<table class='aui aui-table-list'><thead><tr>" +
					"<th>Include</th>" +
					"<th>Relevance Rating</th>" +
					"<th>Key</th>" +
					"<th>Summary</th>" +
					"<th>Type</th>" +
					"</tr></thead>";
				var tableRows = "";
				proposedReleaseNotesEntries.map(function(entry) {
					var expander = "<div id='expanderOfRating_" + category + entry.element.key + "' class='aui-expander-content'>" +
						"<ul class='noDots'>" +
						"<li>#Comments: " + entry.jiraIssueMetrics.COMMENT_COUNT + "</li>" +
						"<li>#Decision Knowledge: " + entry.jiraIssueMetrics.DECISION_KNOWLEDGE_COUNT + "</li>" +
						"<li>Days Completion: " + entry.jiraIssueMetrics.DAYS_COMPLETION + "</li>" +
						"<li>Exp. Reporter: " + entry.jiraIssueMetrics.EXPERIENCE_REPORTER + "</li>" +
						"<li>Exp. Resolver: " + entry.jiraIssueMetrics.EXPERIENCE_RESOLVER + "</li>" +
						"<li>Priority: " + entry.jiraIssueMetrics.PRIORITY + "</li>" +
						"<li>Description Size: " + entry.jiraIssueMetrics.SIZE_DESCRIPTION + "</li>" +
						"<li>Summary Size: " + entry.jiraIssueMetrics.SIZE_SUMMARY + "</li>" +
						"<li><b>Total Rating: " + entry.rating + "</b></li>" +
						"</ul>" +
						"</div>" +
						"<a data-replace-text='less' class='aui-expander-trigger' aria-controls='expanderOfRating_" + category + entry.element.key + "'>show details</a>";
					var tableRow = "<tr>" +
						"<td><input class='checkbox includeInReleaseNote_" + category + "' checked type='checkbox' name='useSprint' id='includeInReleaseNote_" + entry.element.key + "'></td>" +
						"<td>" + expander + "</td>" +
						"<td><a target='_blank' href='" + entry.element.url + "'>" + entry.element.key + "</a></td>" +
						"<td>" + entry.element.summary + "</td>" +
						"<td>" + entry.element.type + "</td>" +
						"</tr>";
					tableRows += tableRow;
				});
				table += tableRows;
				divToAppend.append(title);
				divToAppend.append(table);
				divToAppend.append("</table>");
			}
		};

		document.getElementById("create-release-note-submit-issues-button").onclick = function() {
			var checkedItems = { "BugFixes": [], "NewFeatures": [], "Improvements": [] };
			Object.keys(checkedItems).map(function(cat) {
				var queryElement = $(".includeInReleaseNote_" + cat);
				queryElement.each(function(i) {
					if ($(queryElement[i]).prop("checked")) {
						var key = queryElement[i].id.split(/includeInReleaseNote_(.+)/)[1];
						checkedItems[cat].push(key);
					}
				})
			});

			var releaseNotes = {
				title: configuration.title,
				improvements: checkedItems["Improvements"],
				bugFixes: checkedItems["BugFixes"],
				newFeatures: checkedItems["NewFeatures"],
				projectKey: conDecAPI.projectKey
			};

			conDecReleaseNotesAPI.createReleaseNotesContent(releaseNotes)
				.then(function(response) {
					removeEditor();
					addTabAndChangeToIt("tab-editor", "Release Notes Content");
					editor = new Editor({ element: document.getElementById("create-release-note-textarea") });
					editor.render();
					editor.codemirror.setValue(response.markdown);
				}.bind(this)).catch(function(err) {
					conDecAPI.showFlag("error", err.toString());
				});
		};

		document.getElementById("create-release-note-submit-content").onclick = function() {
			var content = editor.codemirror.getValue();
			var releaseNotes = {
				content: content,
				title: configuration.title,
				startDate: configuration.startDate,
				endDate: configuration.endDate,
				projectKey: conDecAPI.projectKey
			};

			conDecReleaseNotesAPI.createReleaseNotes(releaseNotes).then(function(response) {
				if (response && response > 0) {
					var event = new Event('updateReleaseNoteTable');
					document.getElementById("release-notes-table").dispatchEvent(event);
					AJS.dialog2(releaseNoteDialog).hide();
				}
			}).catch(function(err) {
				conDecAPI.showFlag("error", err.toString());
			})
		};

		document.getElementById("create-release-note-dialog-cancel-button").onclick = function() {
			AJS.dialog2(releaseNoteDialog).hide();
		};
	};

	ConDecReleaseNotesDialog.prototype.showEditReleaseNoteDialog = function(id) {
		var editDialog = document.getElementById("edit-release-note-dialog");
		var saveButton = document.getElementById("edit-release-note-submit-content");
		var cancelButton = document.getElementById("edit-release-note-dialog-cancel-button");
		var deleteButton = document.getElementById("deleteReleaseNote");
		var titleInput = document.getElementById("edit-release-note-dialog-title");
		var exportMDButton = document.getElementById("edit-release-note-dialog-export-as-markdown-button");
		var exportWordButton = document.getElementById("edit-release-note-dialog-export-as-word-button");
		var editor;

		conDecReleaseNotesAPI.getReleaseNotesById(id).then(function(result) {
			$(".editor-preview").empty();
			AJS.dialog2(editDialog).show();
			removeEditor();
			titleInput.value = result.title;
			editor = new Editor({ element: document.getElementById("edit-release-note-textarea") });
			editor.render();
			editor.codemirror.setValue(result.content);
		}).catch(function(error) {
			conDecAPI.showFlag("error", "Could not retrieve the release notes. " + error);
		});

		function removeEditor() {
			var editorDiv = document.getElementById("edit-release-note-dialog-contain-editor");
			editorDiv.parentNode.removeChild(editorDiv);
			$("#edit-release-note-dialog-content").append("<div id='edit-release-note-dialog-contain-editor'>" +
				"<textarea id='edit-release-note-textarea'></textarea>" +
				"</div>")
		}

		saveButton.onclick = function() {
			var releaseNote = { id: id, title: titleInput.value, content: editor.codemirror.getValue() };
			conDecReleaseNotesAPI.updateReleaseNotes(releaseNote).then(function() {
				fireChangeEvent();
				AJS.dialog2(editDialog).hide();
			}).catch(function(err) {
				conDecAPI.showFlag("error", "Saving failed. " + err.toString());
			});
		};
		cancelButton.onclick = function() {
			AJS.dialog2(editDialog).hide();
		};
		deleteButton.onclick = function() {
			conDecReleaseNotesAPI.deleteReleaseNotes(id).then(function() {
				fireChangeEvent();
				AJS.dialog2(editDialog).hide();
			}).catch(function(err) {
				conDecAPI.showFlag("error", "Deleting failed. " + err.toString());
			});
		};

		function fireChangeEvent() {
			var event = new Event('updateReleaseNoteTable');
			document.getElementById("release-notes-table").dispatchEvent(event);
		}

		exportMDButton.onclick = function() {
			var mdString = "data:text/plain;charset=utf-8," + encodeURIComponent(editor.codemirror.getValue());
			downloadFile(mdString, "md")
		};

		exportWordButton.onclick = function() {
			var htmlString = $(".editor-preview").html();
			if (htmlString) {
				var htmlContent = $("<html>").html(htmlString).html();
				var wordString = "data:text/html," + htmlContent;
				downloadFile(wordString, "doc")
			} else {
				conDecAPI.showFlag("error", "Please change to the preview view of the editor first, then try again.");
			}
		};

		function downloadFile(content, fileEnding) {
			var fileName = "releaseNote." + fileEnding;
			var link = document.createElement('a');
			link.style.display = 'none';
			link.setAttribute('href', content);
			link.setAttribute('download', fileName);
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	};

	global.conDecReleaseNotesDialog = new ConDecReleaseNotesDialog();
})(window);
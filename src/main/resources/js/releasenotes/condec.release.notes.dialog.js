(function(global) {
	var ConDecReleaseNotesDialog = function() {
		this.configuration = {};
	};

	ConDecReleaseNotesDialog.prototype.showCreateReleaseNoteDialog = function() {
		var releaseNoteDialog = document.getElementById("create-release-note-dialog");
		var cancelButton = document.getElementById("create-release-note-dialog-cancel-button");
		var configurationSubmitButton = document.getElementById("create-release-note-submit-button");
		var issueSelectSubmitButton = document.getElementById("create-release-note-submit-issues-button");
		var saveContentButton = document.getElementById("create-release-note-submit-content");
		var useSprintSelect = document.getElementById("useSprint");
		var titleInput = document.getElementById("title");
		var sprintOptions = document.getElementById("selectSprints");
		var useReleaseSelect = document.getElementById("useReleases");
		var releaseOptions = document.getElementById("selectReleases");

		var titleWasChanged = false;
		var editor;

		AJS.tabs.setup();

		// prioritisation criteria
		var criteria = [
			{ title: "#Decision Knowledge", id: "decision_knowledge_count" },
			{ title: "Priority", id: "priority" },
			{ title: "#Comments", id: "comment_count" },
			{ title: "Words Description", id: "size_description" },
			{ title: "Words Summary", id: "size_summary" },
			{ title: "Days to completion", id: "days_completion" },
			{ title: "Experience Resolver", id: "experience_resolver" },
			{ title: "Experience Reporter", id: "experience_reporter" }
		];

		addjiraIssueMetric(criteria);
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
			var sprintPromise = new Promise(function(resolve, reject) {
				conDecReleaseNotesAPI.getSprintsByProject()
					.then(function(sprints) {
						var hasValidSprints = false;
						sprintsArray = sprints.map(function(sprint) {
							return sprint.values;
						});
						if (sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
							$('#selectSprints').empty();
							sprintsArray[0].map(function(sprint) {
								if (sprint && sprint.startDate && sprint.endDate) {
									hasValidSprints = true;
									$('#selectSprints').append('<option class="sprint-option" value="' + sprint.id + '">' + sprint.name + '</option>');
								} else {
									$('#selectSprints').append('<option class="sprint-option" disabled value="' + sprint.id + '">' + sprint.name + '</option>');
								}
							});
						}
						if (hasValidSprints) {
							resolve();
						} else {
							reject("No valid Sprints found");
						}
					}).catch(function(err) {
						reject(err);
					});
			}).catch(function(err) {
				disableSprintBox();
				conDecAPI.showFlag("error", "No sprints could be loaded. " + err);
			});
			// load issue types
			var issueTypePromise = new Promise(function(resolve, reject) {
				conDecReleaseNotesAPI.getIssueTypes()
					.then(function(issueTypes) {
						conDecReleaseNotesAPI.getReleaseNotesConfiguration().then(function(releaseNotesConfig) {
							resolve({ issueTypes: issueTypes, releaseNotesConfig: releaseNotesConfig });
						}).catch(function() {
							resolve({ issueTypes: issueTypes, releaseNotesConfig: null });
						});
					}).catch(function(err) {
						reject(err);
					});
			}).then(function(values) {
				// set issue types
				var issueTypes = values.issueTypes;
				var releaseNotesConfig = values.releaseNotesConfig;
				manageIssueTypes(issueTypes, releaseNotesConfig);
			}).catch(function(err) {
				conDecAPI.showFlag("error", "No issue-types could be loaded. "
					+ "This won't be working without Jira-Issues associated to a project: " + err);
			});

			var releasesPromise = new Promise(function(resolve, reject) {
				conDecReleaseNotesAPI.getReleases().then(function(releases) {
					var hasValidReleases = false;
					var releaseSelector = $('#selectReleases');
					releaseSelector.empty();
					releases.map(function(release) {
						if (release && release.startDate.iso && release.releaseDate.iso) {
							hasValidReleases = true;
							releaseSelector.append('<option value="' + release.id + '">' + release.name + '</option>');
							releasesArray.push(release);
						} else {
							releaseSelector.append('<option disabled value="' + release.id + '">' + release.name + '</option>');
						}
					});
					if (!hasValidReleases) {
						disableReleaseBox();
					}
					resolve();
				}).catch(function(err) {
					disableReleaseBox();
					reject(err);
				})
			}).catch(function(err) {
				conDecAPI.showFlag("error", "Loading the releases went wrong. " + err);
			});

			Promise.all([sprintPromise, issueTypePromise, releasesPromise])
				.finally(function() {
					AJS.dialog2(releaseNoteDialog).show();
					prefillDateBox();
				})
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
				console.log(releaseNotesConfig);
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

		function disableSprintBox() {
			$("#useSprint").attr("disabled", true);
			$("#selectSprints").attr("disabled", true);
		}

		function disableReleaseBox() {
			$("#useReleases").attr("disabled", true);
			$("#selectReleases").attr("disabled", true);
		}

		function addTabAndChangeToIt(tabId, title) {
			var listItem = document.getElementById("listItemTab" + tabId);
			if (!listItem) {
				$("#tab-list-menu").append("<li class='menu-item' id='listItemTab" + tabId + "'><a href='#" + tabId + "'>" + title + "</a></li>");
			}
			AJS.tabs.setup();
			AJS.tabs.change(jQuery('a[href="#' + tabId + '"]'));
		}

		function addjiraIssueMetric(listOfCriteria) {
			var elementToAppend = $("#metricWeight");
			elementToAppend.empty();
			listOfCriteria.map(function(element) {
				elementToAppend.append("<div class='field-group'>" +
					"<label for='" + element.id + "'>" + element.title + "</label>" +
					"<input class='text short-field' type='number' value='1' max='10' min='0' id='" + element.id + "'>" +
					"</div>")
			});
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
					var selectedDates = releasesArray.filter(function(release) {
						return release.id = selectedRelease;
					});
					if (selectedDates && selectedDates.length) {
						result.startDate = selectedDates[0].startDate.iso;
						result.endDate = selectedDates[0].releaseDate.iso;
					} else {
						conDecAPI.showFlag("error", "Something went wrong with the release selection");
						return false;
					}
				} else if (useSprints && sprintsArray && sprintsArray.length && sprintsArray[0] && sprintsArray[0].length) {
					// get dates of selected sprint
					var selectedDates = sprintsArray[0].filter(function(sprint) {
						return sprint.id === selectedSprint;
					});
					if (selectedDates && selectedDates.length && selectedDates[0] && selectedDates[0].startDate && selectedDates[0].endDate) {
						var formattedStartDate = formatSprintDate(selectedDates[0].startDate);
						var formattedEndDate = formatSprintDate(selectedDates[0].endDate);
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

			function getjiraIssueMetric(listOfCriteria) {
				var result = {};
				listOfCriteria.map(function(element) {
					var value = $("#" + element.id).val();
					var key = element.id.toUpperCase();
					result[key] = value;
				});
				return result;
			}

			var timeRange = getStartAndEndDate();
			if (!timeRange) {
				return;
			}

			var jiraIssueMetricWeights = getjiraIssueMetric(criteria);
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

			function showTable(category, issues) {
				if (issues.length === 0) {
					return;
				}
				var divToAppend = $("#displayIssueTables");
				var title = "<h2>" + category + "</h2>";
				var table = "<table class='aui'><thead><tr>" +
					"<th>Include</th>" +
					"<th>Relevance-Rating</th>" +
					"<th>Key</th>" +
					"<th>Summary</th>" +
					"<th>Type</th>" +
					"</tr></thead>";
				var tableRows = "";
				issues.map(function(issue) {
					var expander = "<div id='expanderOfRating_" + category + issue.element.key + "' class='aui-expander-content'>" +
						"<ul class='noDots'>" +
						"<li>#Comments: " + issue.jiraIssueMetrics.element + "</li>" +
						"<li>#Decision Knowledge: " + issue.jiraIssueMetrics.COUNT_DECISION_KNOWLEDGE + "</li>" +
						"<li>Days Completion: " + issue.jiraIssueMetrics.DAYS_COMPLETION + "</li>" +
						"<li>Exp. Reporter: " + issue.jiraIssueMetrics.EXPERIENCE_REPORTER + "</li>" +
						"<li>Exp. Resolver: " + issue.jiraIssueMetrics.EXPERIENCE_RESOLVER + "</li>" +
						"<li>Priority: " + issue.jiraIssueMetrics.PRIORITY + "</li>" +
						"<li>Description Size: " + issue.jiraIssueMetrics.SIZE_DESCRIPTION + "</li>" +
						"<li>Summary Size: " + issue.jiraIssueMetrics.SIZE_SUMMARY + "</li>" +
						"</ul>" +
						"</div>" +
						"<a data-replace-text='" + issue.rating + " less' class='aui-expander-trigger' aria-controls='expanderOfRating_" + category + issue.element.key + "'>" + issue.rating + " details</a>";
					var tableRow = "<tr>" +
						"<td><input class='checkbox includeInReleaseNote_" + category + "' checked type='checkbox' name='useSprint' id='includeInReleaseNote_" + issue.element.key + "'></td>" +
						"<td>" + expander + "</td>" +
						"<td><a target='_blank' href='" + issue.element.url + "'>" + issue.element.key + "</a></td>" +
						"<td>" + issue.element.summary + "</td>" +
						"<td>" + issue.element.type + "</td>" +
						"</tr>";
					tableRows += tableRow;
				});
				table += tableRows;
				divToAppend.append(title);
				divToAppend.append(table);
				divToAppend.append("</table>");
			}
		};

		issueSelectSubmitButton.onclick = function() {
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
					addTabAndChangeToIt("tab-editor", "Final edit");
					editor = new Editor({ element: document.getElementById("create-release-note-textarea") });
					editor.render();
					editor.codemirror.setValue(response.markdown);
				}.bind(this)).catch(function(err) {
					conDecAPI.showFlag("error", err.toString());
				});
		};

		saveContentButton.onclick = function() {
			console.log("editor", editor.codemirror.getValue());
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

		cancelButton.onclick = function() {
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
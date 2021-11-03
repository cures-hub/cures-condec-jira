(function(global) {
	var ConDecReleaseNotesDialog = function() {
	};

	ConDecReleaseNotesDialog.prototype.showCreateReleaseNoteDialog = function() {
		// HTML elements
		// set button busy before we show the dialog
		var openingButton = document.getElementById("create-release-notes-button");
		setButtonBusyAndDisabled(openingButton, true);
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
		var firstResultObject = {};

		AJS.tabs.setup();

		// add task prioritisation
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

		var targetGroupMapping = [
			{ id: "DEVELOPER", title: "Developer", includes: ["include_decision_knowledge", "include_bug_fixes"] },
			{ id: "TESTER", title: "Tester", includes: ["include_bug_fixes", "include_test_instructions"] },
			{ id: "ENDUSER", title: "Enduser", includes: [] }
		];

		var softwareTypeMapping =
			[
				{ includes: [], title: "Simple website", id: "simple_website" },
				{ includes: ["include_breaking_changes", "include_extra_link"], title: "Framework", id: "framework" },
				{ includes: ["include_upgrade_guide"], title: "Installable software", id: "software" },
				{ includes: ["include_breaking_changes"], title: "API", id: "api" }
			];

		var allTargetGroupIncludes = ["include_decision_knowledge", "include_bug_fixes", "include_test_instructions"];
		var allSoftwareTypeIncludes = ["include_breaking_changes", "include_extra_link", "include_upgrade_guide"];

		addjiraIssueMetric(criteria);
		removeListItemIssues();
		AJS.tabs.change(jQuery('a[href="#tab-configuration"]'));
		makeAsyncCalls();
		fillSoftwaretypesAndTargetGroups();
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

		function fillSoftwaretypesAndTargetGroups() {
			$("#selectSoftwareType").empty();
			$("#selectTargetGroup").empty();
			softwareTypeMapping.map(function(type) {
				$("#selectSoftwareType").append("<option value='" + type.id + "'>" + type.title + "</option>")
			});
			targetGroupMapping.map(function(targetGroup) {
				$("#selectTargetGroup").append("<option value='" + targetGroup.id + "'>" + targetGroup.title + "</option>")
			});
			document.getElementById("selectSoftwareType").onchange = onSoftwareTypeChange;
			document.getElementById("selectTargetGroup").onchange = onTargetGroupChange;
			onSoftwareTypeChange();
			onTargetGroupChange();
		}

		function onTargetGroupChange() {
			var selectedGroup = $("#selectTargetGroup").val();

			var foundGroup = targetGroupMapping.filter(function(targetGroup) {
				return targetGroup.id === selectedGroup
			});
			allTargetGroupIncludes.map(function(include) {
				if (foundGroup[0].includes.indexOf(include) > -1) {
					$("#" + include).prop("checked", true);
				} else {
					$("#" + include).prop("checked", false);
				}
			})
		}

		function onSoftwareTypeChange() {
			var selectedType = $("#selectSoftwareType").val();
			var foundType = softwareTypeMapping.filter(function(type) {
				return type.id === selectedType
			});
			allSoftwareTypeIncludes.map(function(include) {
				if (foundType[0].includes.indexOf(include) > -1) {
					$("#" + include).prop("checked", true);
				} else {
					$("#" + include).prop("checked", false);
				}
			})
		}

		function throwAlert(title, message) {
			AJS.flag({
				type: "error",
				close: "auto",
				title: title,
				body: message
			});
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
				throwAlert("No sprints could be loaded", err);
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
				throwAlert("No issue-types could be loaded", "This won't be working without Jira-Issues associated to a project: " + err);
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
				throwAlert("Loading the Releases went wrong", err)
			});


			Promise.all([sprintPromise, issueTypePromise, releasesPromise])
				.finally(function() {
					// disable busy button
					setButtonBusyAndDisabled(openingButton, false);
					// Show dialog
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
					var bugString = '<option value="' + issueType.id + '"';
					var featureSelected = false;
					var featureString = '<option value="' + issueType.id + '"';
					var improvementSelected = false;
					var improvementString = '<option value="' + issueType.id + '"';
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
			// first empty list
			elementToAppend.empty();
			elementToAppend.append("<form class='aui'>")
			listOfCriteria.map(function(element) {
				elementToAppend.append("<div class='field-group'>" +
					"<label for='" + element.id + "'>" + element.title + "</label>" +
					"<input class='medium-field' type='number' value='1' max='10' min='0' id='" + element.id + "'>" +
					"</div>")
			});
			elementToAppend.append("</form>")
		}

		function setButtonBusyAndDisabled(button, busy) {
			if (busy) {
				button.busy();
				button.setAttribute('aria-disabled', 'true');
			} else {
				button.idle();
				button.setAttribute('aria-disabled', 'false');
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
				throwAlert("Select Date or Sprint or Release", "The start date and the end date have to be selected, if the sprints or releases are not available or not selected");
				return
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
						throwAlert("An error occured", "Something went wrong with the release selection");
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
							throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
							return false;
						}
					} else {
						throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
						return false;
					}
				} else if (!!startDate && !!endDate) {
					result.startDate = startDate;
					result.endDate = endDate;
				} else {
					// throw exception
					throwAlert("An error occured", "Neither a sprint was selected or start dates were filled");
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


			var additionalConfiguration = {};

			function getAdditionalConfiguration() {
				$(".advancedOptionalConfiguration").each(function(i) {
					var item = ($(".advancedOptionalConfiguration").get(i));
					var name = item.name;
					var isChecked = item.checked;
					additionalConfiguration[name.toUpperCase()] = isChecked;
				})
			}

			var timeRange = getStartAndEndDate();
			if (!timeRange) {
				return;
			}
			// set button busy and disabled
			setButtonBusyAndDisabled(configurationSubmitButton, true);
			getAdditionalConfiguration();
			var jiraIssueMetricWeights = getjiraIssueMetric(criteria);
			var targetGroup = $("#selectTargetGroup").val();
			var bugFixes = $("#multipleBugs").val();
			var features = $("#multipleFeatures").val();
			var improvements = $("#multipleImprovements").val();
			var title = $("#release-notes-title").val();
			// submit configuration
			var configuration = {
				title: title,
				startDate: timeRange.startDate,
				endDate: timeRange.endDate,
				sprintId: selectedSprint,
				targetGroup: targetGroup,
				jiraIssueTypesForBugFixes: bugFixes,
				jiraIssueTypesForNewFeatures: features,
				jiraIssueTypesForImprovements: improvements,
				additionalConfiguration: additionalConfiguration,
				jiraIssueMetricWeights: jiraIssueMetricWeights
			};

			conDecReleaseNotesAPI.proposeElements(configuration).then(function(response) {
				if (response) {
					// change tab
					addTabAndChangeToIt("tab-issues", "Suggested Issues");
					console.log(response);

					firstResultObject = response;
					// display issues and information
					if (response.proposals) {
						showTables(response.proposals);
					}
					if (response.title) {
						showTitle(response.title)
					}
				}

			}).catch(function(err) {
				// we handle this exception directly in condec.api
			}).finally(function() {
				// set button idle
				setButtonBusyAndDisabled(configurationSubmitButton, false);
			});

			function showTables(response) {
				// first remove old tables
				$("#displayIssueTables").empty();
				Object.keys(response).map(function(category) {
					if (response[category] && response[category].length) {
						showTable(category, response[category]);
					}
				})
			}

			function showTitle(title) {
				$("#suggestedIssuesTitle").text("Suggested Issues for Release Notes: " + title);
			}

			function showTable(category, issues) {
				var mapCategoryToTitles = {
					"bug_fixes": "Bug Fixes",
					"new_features": "New Features",
					"improvements": "Improvements"
				};
				var divToAppend = $("#displayIssueTables");
				var title = "<h2>" + mapCategoryToTitles[category] + "</h2>";
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
			// set button busy
			setButtonBusyAndDisabled(issueSelectSubmitButton, true);

			var checkedItems = { "bug_fixes": [], "new_features": [], "improvements": [] }
			Object.keys(checkedItems).map(function(cat) {
				var queryElement = $(".includeInReleaseNote_" + cat);
				queryElement.each(function(i) {
					if ($(queryElement[i]).prop("checked")) {
						var key = queryElement[i].id.split(/includeInReleaseNote_(.+)/)[1];
						checkedItems[cat].push(key);
					}
				})
			});
			var additionalConfigurationObjectSelected = [];
			Object.getOwnPropertyNames(firstResultObject.additionalConfiguration).forEach(function(val, idx, array) {
				if (firstResultObject.additionalConfiguration[val]) {
					additionalConfigurationObjectSelected.push(val)
				}
			});

			var postObject = {
				selectedKeys: checkedItems,
				title: { id: [firstResultObject.title] },
				additionalConfiguration: { id: additionalConfigurationObjectSelected }
			};
			conDecReleaseNotesAPI.postProposedKeys(postObject)
				.then(function(response) {
					if (response) {
						// remove editor
						removeEditor();
						// change tab
						addTabAndChangeToIt("tab-editor", "Final edit");
						if (response.markdown) {
							// display editor and text
							editor = new Editor({ element: document.getElementById("create-release-note-textarea") });
							editor.render();
							editor.codemirror.setValue(response.markdown);
						}
					}

				}.bind(this)).catch(function(err) {
					throwAlert("An error occurred", err.toString());
				}).finally(function() {
					// set button idle
					setButtonBusyAndDisabled(issueSelectSubmitButton, false);
				});
		};

		saveContentButton.onclick = function() {
			console.log("editor", editor.codemirror.getValue());
			var content = editor.codemirror.getValue();
			var releaseNotes = {
				content: content,
				title: firstResultObject.title,
				startDate: firstResultObject.startDate,
				endDate: firstResultObject.endDate,
				projectKey: conDecAPI.projectKey
			};

			conDecReleaseNotesAPI.createReleaseNotes(releaseNotes).then(function(response) {
				if (response && response > 0) {
					var event = new Event('updateReleaseNoteTable');
					document.getElementById("release-notes-table").dispatchEvent(event);
					AJS.dialog2(releaseNoteDialog).hide();
				}
			}).catch(function(err) {
				throwAlert("An error saving occurred", err.toString());
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
		var openingButton = document.getElementById("openEditReleaseNoteDialogButton_" + id);
		var deleteButton = document.getElementById("deleteReleaseNote");
		var titleInput = document.getElementById("edit-release-note-dialog-title");
		var exportMDButton = document.getElementById("edit-release-note-dialog-export-as-markdown-button");
		var exportWordButton = document.getElementById("edit-release-note-dialog-export-as-word-button");
		var editor;
		setButtonBusyAndDisabled(openingButton, true);

		conDecReleaseNotesAPI.getReleaseNotesById(id).then(function(result) {
			$(".editor-preview").empty();
			AJS.dialog2(editDialog).show();
			removeEditor();
			titleInput.value = result.title;
			editor = new Editor({ element: document.getElementById("edit-release-note-textarea") });
			editor.render();
			editor.codemirror.setValue(result.content);

		}).catch(function(error) {
			throwAlert("Retrieving Release notes failed", "Could not retrieve the release notes.")
		}).finally(function() {
			setButtonBusyAndDisabled(openingButton, false);
		});

		function removeEditor() {
			var editorDiv = document.getElementById("edit-release-note-dialog-contain-editor");
			editorDiv.parentNode.removeChild(editorDiv);
			$("#edit-release-note-dialog-content").append("<div id='edit-release-note-dialog-contain-editor'>" +
				"<textarea id='edit-release-note-textarea'></textarea>" +
				"</div>")
		}

		function setButtonBusyAndDisabled(button, busy) {
			if (busy) {
				button.busy();
				button.setAttribute('aria-disabled', 'true');
			} else {
				button.idle();
				button.setAttribute('aria-disabled', 'false');
			}
		}

		saveButton.onclick = function() {
			setButtonBusyAndDisabled(saveButton, true);
			var releaseNote = { id: id, title: titleInput.value, content: editor.codemirror.getValue() };
			conDecReleaseNotesAPI.updateReleaseNotes(releaseNote).then(function() {
				fireChangeEvent();
				AJS.dialog2(editDialog).hide();
			}).catch(function(err) {
				throwAlert("Saving failed", err.toString());
			}).finally(function() {
				setButtonBusyAndDisabled(saveButton, false);
			});
		};
		cancelButton.onclick = function() {
			AJS.dialog2(editDialog).hide();
		};
		deleteButton.onclick = function() {
			setButtonBusyAndDisabled(deleteButton, true);
			conDecReleaseNotesAPI.deleteReleaseNotes(id).then(function() {
				fireChangeEvent();
				AJS.dialog2(editDialog).hide();
			}).catch(function(err) {
				throwAlert("Deleting failed", err.toString());
			}).finally(function() {
				setButtonBusyAndDisabled(deleteButton, false);
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
				throwAlert("Error downloading Word", "Please change to the preview view of the editor first, then try again.")
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

		function throwAlert(title, message) {
			AJS.flag({
				type: "error",
				close: "auto",
				title: title,
				body: message
			});
		}
	};

	global.conDecReleaseNotesDialog = new ConDecReleaseNotesDialog();
})(window);
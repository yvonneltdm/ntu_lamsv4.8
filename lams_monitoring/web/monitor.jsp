<!DOCTYPE html>
<%@ include file="/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration" import="org.lamsfoundation.lams.util.ConfigurationKeys" %>

<c:set var="localeLanguage"><lams:user property="localeLanguage" /></c:set>
<c:set var="ALLOW_DIRECT_LESSON_LAUNCH"><%=Configuration.get(ConfigurationKeys.ALLOW_DIRECT_LESSON_LAUNCH)%></c:set>
<c:set var="serverURL"><%=Configuration.get(ConfigurationKeys.SERVER_URL)%></c:set>
<c:if test="${fn:substring(serverURL, fn:length(serverURL)-1, fn:length(serverURL)) != '/'}">
	<c:set var="serverURL">${serverURL}/</c:set>
</c:if>

<lams:html>
	<head>
		<meta charset="utf-8" />
		<title><fmt:message key="monitor.title" /></title>
		<link rel="icon" type="image/x-icon" href="<lams:LAMSURL/>images/svg/lamsv5_logo.svg">
		
		<link rel="stylesheet" href="<lams:LAMSURL/>css/components.css">
		<link rel="stylesheet" href="<lams:WebAppURL/>css/components-monitoring.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>css/jquery-ui-bootstrap-theme5.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>includes/font-awesome6/css/all.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>css/vertical-timeline.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>css/x-editable.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>css/free.ui.jqgrid.custom.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>css/tempus-dominus.min.css">
		<link rel="stylesheet" href="<lams:LAMSURL/>gradebook/includes/css/gradebook.css" />

		<lams:css suffix="chart"/>

		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery-ui.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.plugin.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.countdown.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.cookie.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.timeago.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/timeagoi18n/jquery.timeago.${fn:toLowerCase(localeLanguage)}.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/popper.min.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/bootstrap5.bundle.min.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/x-editable.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/chartjs/chart.umd.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/snap.svg.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/free.jquery.jqgrid.min.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/tempus-dominus.min.js"></script>

		<c:choose>
			<c:when test="${localeLanguage eq 'es'}">
				<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jqgrid-i18n/grid.locale-es.js"></script>
				<c:set var="jqGridInternationalised" value="true" />
			</c:when>
			<c:when test="${localeLanguage eq 'fr'}">
				<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jqgrid-i18n/grid.locale-fr.js"></script>
				<c:set var="jqGridInternationalised" value="true" />
			</c:when>
			<c:when test="${localeLanguage eq 'el'}">
				<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jqgrid-i18n/grid.locale-el.js"></script>
				<c:set var="jqGridInternationalised" value="true" />
			</c:when>
			<c:when test="${localeLanguage eq 'no'}">
				<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jqgrid-i18n/grid.locale-no.js"></script>
				<c:set var="jqGridInternationalised" value="true" />
			</c:when>
		</c:choose>

		<lams:JSImport src="includes/javascript/common.js" />
		<lams:JSImport src="includes/javascript/download.js" />
		<lams:JSImport src="includes/javascript/portrait5.js" />
		<lams:JSImport src="includes/javascript/dialog5.js" />
		<lams:JSImport src="includes/javascript/monitorLesson.js" relative="true" />


		<script type="text/javascript">
			var lessonId = ${lesson.lessonID},
					userId = '<lams:user property="userID"/>',
					ldId = ${lesson.learningDesignID},
					contentFolderId = '${contentFolderID}',
					lessonStateId = ${lesson.lessonStateID},
					createDateTimeStr = '${lesson.createDateTimeStr}',
					lessonStartDate = '${lesson.scheduleStartDate.toGMTString()}',
					lessonEndDate = '${lesson.scheduleEndDate.toGMTString()}',
					liveEditEnabled = ${enableLiveEdit && lesson.liveEditEnabled},
					TOTAL_LESSON_LEARNERS_NUMBER = ${lesson.numberPossibleLearners},

					iraToolContentId = '${iraToolContentId}',
					traToolContentId = '${traToolContentId}',
					aeToolContentIds = "${aeToolContentIds}",
					aeToolTypes = "${aeToolTypes}",
					aeActivityTitles = "${aeActivityTitles}",
					peerreviewToolContentId = "${peerreviewToolContentId}",

					LAMS_URL = '<lams:LAMSURL/>',
					csrfToken = '<csrf:tokenname/> : <csrf:tokenvalue/>',
					csrfTokenName = '<csrf:tokenname/>',
					csrfTokenValue = '<csrf:tokenvalue/>',

					LABELS = {
						EMAIL_NOTIFICATIONS_TITLE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='index.emailnotifications'/></spring:escapeBody>",
						FORCE_COMPLETE_CLICK : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='force.complete.click'/></spring:escapeBody>",
						FORCE_COMPLETE_BUTTON : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.force.complete'/></spring:escapeBody>",
						FORCE_COMPLETE_END_LESSON_CONFIRM : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='force.complete.end.lesson.confirm'/></spring:escapeBody>",
						FORCE_COMPLETE_ACTIVITY_CONFIRM : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='force.complete.activity.confirm'/></spring:escapeBody>",
						FORCE_COMPLETE_REMOVE_CONTENT : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='force.complete.remove.content'/></spring:escapeBody>",
						FORCE_COMPLETE_DROP_FAIL : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='force.complete.drop.fail'/></spring:escapeBody>",
						LEARNER_GROUP_COUNT : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='learner.group.count'/></spring:escapeBody>",
						LEARNER_GROUP_SHOW : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='learner.group.show'/></spring:escapeBody>",
						LEARNER_GROUP_REMOVE_PROGRESS : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='learner.group.remove.progress'/></spring:escapeBody>",
						EMAIL_BUTTON : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.email'/></spring:escapeBody>",
						NOTIFCATIONS : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='email.notifications'/></spring:escapeBody>",
						SAVE_BUTTON : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.save'/></spring:escapeBody>",
						CANCEL_BUTTON : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.cancel'/></spring:escapeBody>",
						LEARNER_FINISHED_DIALOG_TITLE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='learner.finished.dialog.title'/></spring:escapeBody>",
						LESSON_REMOVE_ALERT : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.remove.alert'/></spring:escapeBody>",
						LESSON_REMOVE_DOUBLECHECK_ALERT : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.remove.doublecheck.alert'/></spring:escapeBody>",
						LESSON_STATE_CREATED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.created'/></spring:escapeBody>",
						LESSON_STATE_SCHEDULED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.scheduled'/></spring:escapeBody>",
						LESSON_STATE_STARTED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.started'/></spring:escapeBody>",
						LESSON_STATE_SUSPENDED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.suspended'/></spring:escapeBody>",
						LESSON_STATE_FINISHED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.finished'/></spring:escapeBody>",
						LESSON_STATE_ARCHIVED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.archived'/></spring:escapeBody>",
						LESSON_STATE_REMOVED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.removed'/></spring:escapeBody>",
						LESSON_STATE_ACTION_DISABLE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.action.disable'/></spring:escapeBody>",
						LESSON_STATE_ACTION_ACTIVATE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.action.activate'/></spring:escapeBody>",
						LESSON_STATE_ACTION_REMOVE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.action.remove'/></spring:escapeBody>",
						LESSON_STATE_ACTION_ARCHIVE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.state.action.archive'/></spring:escapeBody>",
						LESSON_ERROR_SCHEDULE_DATE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='error.lesson.schedule.date'/></spring:escapeBody>",
						LESSON_EDIT_CLASS : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.edit.class'/></spring:escapeBody>",
						CLASS_ADD_ALL_CONFIRM : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='class.add.all.confirm'/></spring:escapeBody>",
						CLASS_ADD_ALL_SUCCESS : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='class.add.all.success'/></spring:escapeBody>",
						LESSON_GROUP_DIALOG_CLASS : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.group.dialog.class'/></spring:escapeBody>",
						CURRENT_ACTIVITY : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.activity.current.tooltip'/></spring:escapeBody>",
						COMPLETED_ACTIVITY : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.activity.completed.tooltip'/></spring:escapeBody>",
						ATTEMPTED_ACTIVITY : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.activity.attempted.tooltip'/></spring:escapeBody>",
						TOSTART_ACTIVITY : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.activity.tostart.tooltip'/></spring:escapeBody>",
						SUPPORT_ACTIVITY : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.activity.support.tooltip'/></spring:escapeBody>",
						PROGRESS_NOT_STARTED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learner.progress.not.started'/></spring:escapeBody>",
						TIME_CHART : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.timechart'/></spring:escapeBody>",
						TIME_CHART_TOOLTIP : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.timechart.tooltip'/></spring:escapeBody>",
						LIVE_EDIT_CONFIRM : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.live.edit.confirm'/></spring:escapeBody>",
						CONTRIBUTE_GATE : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.gate'/></spring:escapeBody>",
						CONTRIBUTE_GATE_PASSWORD : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.gate.password'/></spring:escapeBody>",
						CONTRIBUTE_GROUPING : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.grouping'/></spring:escapeBody>",
						CONTRIBUTE_BRANCHING : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.branching'/></spring:escapeBody>",
						CONTRIBUTE_CONTENT_EDITED : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.content.edited'/></spring:escapeBody>",
						CONTRIBUTE_TOOL : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.tool'/></spring:escapeBody>",
						CONTRIBUTE_TOOLTIP : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.go.tooltip'/></spring:escapeBody>",
						CONTRIBUTE_BUTTON : "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.go'/></spring:escapeBody>",
						CONTRIBUTE_OPEN_GATE_NOW_BUTTON: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.open.now'/></spring:escapeBody>",
						CONTRIBUTE_OPEN_GATE_NOW_TOOLTIP: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.open.now.tooltip'/></spring:escapeBody>",
						CONTRIBUTE_OPEN_GATE_BUTTON: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.open'/></spring:escapeBody>",
						CONTRIBUTE_OPEN_GATE_TOOLTIP: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.open.tooltip'/></spring:escapeBody>",
						CONTRIBUTE_OPENED_GATE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.opened'/></spring:escapeBody>",
						CONTRIBUTE_OPENED_GATE_TOOLTIP: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.task.gate.opened.tooltip'/></spring:escapeBody>",
						CONTRIBUTE_ATTENTION: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.task.attention'/></spring:escapeBody>",
						HELP: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.help'/></spring:escapeBody>",
						LESSON_INTRODUCTION: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.lesson.introduction'/></spring:escapeBody>",
						EMAIL_TITLE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.email'/></spring:escapeBody>",
						TOUR_DISABLED_ELEMENT: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='tour.this.is.disabled'/></spring:escapeBody>",
						PROGRESS_EMAIL_SUCCESS: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.sent.success'/></spring:escapeBody>",
						PROGRESS_EMAIL_SEND_NOW_QUESTION: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.send.now.question'/></spring:escapeBody>",
						PROGRESS_EMAIL_SEND_FAILED: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.send.failed'/></spring:escapeBody>",
						PROGRESS_SELECT_DATE_FIRST: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.select.date.first'/></spring:escapeBody>",
						PROGRESS_ENTER_TWO_DATES_FIRST: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.enter.two.dates.first'/></spring:escapeBody>",
						PROGRESS_EMAIL_GENERATE_ONE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.would.you.like.to.generate'/></spring:escapeBody>",
						PROGRESS_EMAIL_GENERATE_TWO: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.how.many.dates.to.generate'/></spring:escapeBody>",
						PROGRESS_EMAIL_TITLE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='progress.email.title'/></spring:escapeBody>",
						ERROR_DATE_IN_PAST: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='error.date.in.past'/></spring:escapeBody>",
						LESSON_START: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.lesson.starts'><fmt:param value='%0'/></fmt:message></spring:escapeBody>",
						LESSON_FINISH: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.lesson.finishes'><fmt:param value='%0'/></fmt:message></spring:escapeBody>",
						LESSON_ACTIVITY_SCORES_ENABLE_ALERT: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.display.activity.scores.alert'/></spring:escapeBody>",
						LESSON_ACTIVITY_SCORES_DISABLE_ALERT: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='lesson.hide.activity.scores.alert'/></spring:escapeBody>",
						RESCHEDULE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.reschedule'/></spring:escapeBody>",
						LESSON_ERROR_START_END_DATE: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='error.lesson.end.date.must.be.after.start.date'/></spring:escapeBody>",
						LIVE_EDIT_BUTTON: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.live.edit'/></spring:escapeBody>",
						LIVE_EDIT_TOOLTIP: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='button.live.edit.tooltip'/></spring:escapeBody>",
						LIVE_EDIT_WARNING: "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.person.editing.lesson'><fmt:param value='%0'/></fmt:message></spring:escapeBody>"
					};

			<c:if test="${jqGridInternationalised}">
			$.extend(true, $.jgrid,$.jgrid.regional['${language}']);
			</c:if>
		</script>

	</head>
	<body class="component">
	<div class="component-page-wrapper monitoring-page-wrapper ">
		<div class="component-sidebar active">

			<c:if test="${not isIntegrationOrganisation}">
			<a href="/" title="<fmt:message key='label.monitoring.return.to.index' />">
				</c:if>
				<img class="lams-logo" src="<lams:LAMSURL/>images/svg/lamsv5_logo.svg"
					 alt="<fmt:message key='label.monitoring.logo' />" />
				<c:if test="${not isIntegrationOrganisation}">
			</a>
			</c:if>


			<div class="component-menu">
				<div class="component-menu-btn d-flex flex-column align-items-center">
					<div class="navigate-btn-container">
						<a id="load-sequence-tab-btn" href="#" class="btn btn-primary navigate-btn active"
						   data-tab-name="sequence"	title="<fmt:message key='tab.dashboard' />">
							<i class="fa fa-cubes fa-lg"></i>
						</a>
						<label for="load-sequence-tab-btn" class="d-none d-md-block">
							<fmt:message key='tab.dashboard' />
						</label>
					</div>

					<c:if test="${isTBLSequence}">
						<div class="navigate-btn-container">
							<a id="load-teams-tab-btn" class="btn btn-primary navigate-btn" href="#"
							   data-tab-name="teams" title="<fmt:message key='label.teams' />">
								<i class="fa fa-people-group fa-lg"></i>
							</a>
							<label for="load-teams-tab-btn" class="d-none d-md-block">
								<fmt:message key='label.teams' />
							</label>
						</div>

						<c:if test="${not empty isIraAvailable}">
							<div class="navigate-btn-container">
								<a id="load-irat-tab-btn" class="btn btn-primary navigate-btn" href="#"
								   data-tab-name="iratStudentChoices"	title="<fmt:message key='label.ira' />">
									<i class="fa fa-user fa-lg"></i>
								</a>

								<label for="load-irat-tab-btn" class="d-none d-md-block">
									<fmt:message key='label.ira' />
								</label>
							</div>
						</c:if>

						<c:if test="${not empty isScratchieAvailable}">
							<div class="navigate-btn-container">
								<a id="load-trat-tab-btn" class="btn btn-primary navigate-btn" href="#"
								   data-tab-name="tratStudentChoices" title="<fmt:message key='label.tra' />">
									<i class="fa fa-users fa-lg"></i>
								</a>

								<label for="load-trat-tab-btn" class="d-none d-md-block">
									<fmt:message key='label.tra' />
								</label>
							</div>

							<c:if test="${burningQuestionsEnabled}">
								<div class="navigate-btn-container">
									<a id="load-burning-tab-btn" class="btn btn-primary navigate-btn" href="#"
									   data-tab-name="burningQuestions" title="<fmt:message key='label.monitoring.burning.questions' />">
										<i class="fa fa-question-circle fa-lg"></i>
									</a>
									<label for="load-burning-tab-btn" class="d-none d-md-block">
										<fmt:message key='label.monitoring.burning.questions' />
									</label>
								</div>
							</c:if>
						</c:if>

						<c:if test="${not empty isAeAvailable}">
							<div class="navigate-btn-container">
								<a id="load-aes-tab-btn" class="btn btn-primary navigate-btn" href="#"
								   data-tab-name="aes" title="<fmt:message key='label.aes' />">
									<i class="fa fa-dashboard fa-lg"></i>
								</a>
								<label for="load-aes-tab-btn" class="d-none d-md-block">
									<fmt:message key='label.aes' />
								</label>
							</div>
						</c:if>

						<c:if test="${not empty isPeerreviewAvailable}">
							<div class="navigate-btn-container">
								<a id="load-peer-review-tab-btn" class="btn btn-primary navigate-btn" href="#"
								   data-tab-name="peerReview" title="<fmt:message key='label.peer.review' />">
									<i class="fa fa-person-circle-question fa-lg"></i>
								</a>
								<label for="load-aes-tab-btn" class="d-none d-md-block">
									<fmt:message key='label.peer.review' />
								</label>
							</div>
						</c:if>
					</c:if>

					<div class="navigate-btn-container">
						<a id="load-learners-tab-btn" href="#" class="btn btn-primary navigate-btn"
						   data-tab-name="learners"	title="<fmt:message key='tab.learners' />">
							<i class="fa fa-solid fa-users fa-lg"></i>
						</a>
						<label for="load-learners-tab-btn" class="d-none d-md-block">
							<fmt:message key='tab.learners' />
						</label>
					</div>

					<div class="navigate-btn-container">
						<a id="load-gradebook-tab-btn" href="#" class="btn btn-primary navigate-btn"
						   data-tab-name="gradebook" title="<fmt:message key='tab.gradebook' />">
							<i class="fa fa-solid fa-list-ol fa-lg"></i>
						</a>
						<label for="load-gradebook-tab-btn" class="d-none d-md-block">
							<fmt:message key='tab.gradebook' />
						</label>
					</div>

					<div class="navigate-btn-container">
						<a id="edit-lesson-btn" class="btn btn-primary navigate-btn" href="#" title="<fmt:message key='label.monitoring.edit.lesson.settings' />">
							<i class="fa fa-pen fa-lg"></i>
						</a>
						<label for="edit-lesson-btn" class="d-none d-md-block">
							<fmt:message key='label.monitoring.edit' />
						</label>
					</div>

				</div>

				<div class="lesson-properties">
					<dl id="lessonDetails" class="dl-horizontal">
						<c:if test="${ALLOW_DIRECT_LESSON_LAUNCH}">
							<dt class="text-muted mt-0"><small><fmt:message key="lesson.learner.url"/></small></dt>
							<dd class="text-muted">
								<small id="lessonUrl" class="text-break">
									<c:out value="${serverURL}r/${lesson.encodedLessonID}" escapeXml="true"/>
									<button id="lessonUrlCopyToClipboardButton" class="btn btn-primary btn-sm ms-2"
											onClick="javascript:copyLessonUrlToClipboard()"
											title='<fmt:message key="button.copy.lesson.url.tooltip"/>'>
										<i class="fa-regular fa-clipboard"></i>
									</button>
								</small>
							</dd>
						</c:if>

						<dt><fmt:message key="lesson.state"/>
						</dt>
						<dd>
							<button data-bs-toggle="collapse" data-bs-target="#changeState" id="lessonStateLabel" class="lessonManageField"
								<c:if test="${lesson.lessonStateID eq 7}">
									disabled
								</c:if>
							></button>
							<c:if test="${lesson.lessonStateID != 7}">
								<div style="display:inline-block;vertical-align: middle;">
									<span id="lessonStartDateSpan" class="lessonManageField loffset5"></span>
									<span id="lessonFinishDateSpan" class="lessonManageField loffset5"></span>
								</div>

								<!--  Change lesson status or start/schedule start -->
								<div class="collapse offset10" id="changeState">
									<div id="lessonScheduler">
										<form>
											<div id="lessonStartApply">
												<div class="form-group mt-2" >
													<label for="scheduleDatetimeField" class="form-label"><fmt:message key="lesson.start"/></label>
													<input class="lessonManageField form-control-sm" id="scheduleDatetimeField" type="text" autocomplete="nope" />
												</div>

												<div class="mt-2">
													<a id="scheduleLessonButton" class="btn btn-sm btn-default lessonManageField" href="#"
													   onClick="javascript:scheduleLesson()"
													   title='<fmt:message key="button.schedule.tooltip"/>'>
														<fmt:message key="button.schedule"/>
													</a>
													<a id="startLessonButton" class="btn btn-sm btn-secondary" href="#"
													   onClick="javascript:startLesson()"
													   title='<fmt:message key="button.start.now.tooltip"/>'>
														<fmt:message key="button.start.now"/>
													</a>
												</div>
											</div>

											<div id="lessonDisableApply">
												<div class="form-group mt-2">
													<label for="disableDatetimeField" class="form-label d-block"><fmt:message key="lesson.end"/></label>
													<input class="lessonManageField form-control-sm" id="disableDatetimeField" type="text"/>
												</div>
												<div class="mt-2">
													<a id="scheduleDisableLessonButton" class="btn btn-sm btn-secondary lessonManageField" href="#"
													   onClick="javascript:scheduleDisableLesson()"
													   title='<fmt:message key="button.schedule.disable.tooltip"/>'>
														<fmt:message key="button.schedule"/>
													</a>
													<a id="disableLessonButton" class="btn btn-sm btn-secondary" href="#"
													   onClick="javascript:disableLesson()"
													   title='<fmt:message key="button.disable.now.tooltip"/>'>
														<fmt:message key="button.disable.now"/>
													</a>
												</div>
											</div>
										</form>
									</div>

									<div id="lessonStateChanger">
										<select id="lessonStateField" class="form-select-sm mt-2" onchange="lessonStateFieldChanged()">
											<option value="-1"><fmt:message key="lesson.select.state"/></option>
										</select>
										<span id="lessonStateApply">
										<button type="button" class="lessonManageField btn btn-sm btn-primary"
												onClick="javascript:changeLessonState()"
												title='<fmt:message key="lesson.change.state.tooltip"/>'>
											<i class="fa fa-check"></i>
											<span class="hidden-xs"><fmt:message key="button.apply"/></span>
										</button>
									</span>
									</div>
								</div>
							</c:if>
						</dd>

						<!--
					<dt><fmt:message key="lesson.learners"/>:</dt>
					<dd title='<fmt:message key="lesson.ratio.learners.tooltip"/>' id="learnersStartedPossibleCell"></dd>
					 -->

						<!--  lesson actions -->
						<dt><fmt:message key="lesson.manage"/>:</dt>
						<dd>
							<div>
								<button id="editLessonNameButton" class="btn btn-sm btn-primary"
										type="button"
										title='<fmt:message key="button.edit.lesson.name"/>'>
									<i class="fa fa-pencil"></i>
									<span class="hidden-xs"><fmt:message key="button.edit.lesson.name"/></span>
								</button>

								<button id="liveEditButton" class="btn btn-sm btn-primary" style="display:none"
										type="button" onClick="javascript:openLiveEdit()"
										title='<fmt:message key='button.live.edit.tooltip'/>'>
									<i class="fa fa-pen-to-square"></i>
									<span class="hidden-xs"><fmt:message key='button.live.edit'/></span>
								</button>

								<button id="viewLearnersButton" class="btn btn-sm btn-primary"
										type="button" onClick="javascript:showLessonLearnersDialog()"
										title='<fmt:message key="button.view.learners.tooltip"/>'>
									<i class="fa fa-users"></i>
									<span class="hidden-xs"><fmt:message key="button.view.learners"/></span>
								</button>

								<button id="editClassButton" class="btn btn-sm btn-primary"
										type="button" onClick="javascript:showClassDialog()"
										title='<fmt:message key="button.edit.class.tooltip"/>'>
									<i class="fa fa-user-times"></i>
									<span class="hidden-xs"><fmt:message key="button.edit.class"/></span>
								</button>

								<c:if test="${lesson.enabledLessonNotifications}">
									<button id="notificationButton" class="btn btn-sm btn-primary"
											type="button" onClick="javascript:showNotificationsDialog(null,${lesson.lessonID})">
										<i class="fa fa-bullhorn"></i>
										<span class="hidden-xs"><fmt:message key="email.notifications"/></span>
									</button>
								</c:if>

								<a id="editTimerButton" class="btn btn-sm btn-primary"
								   type="button" href="<lams:LAMSURL/>monitoring/timer.jsp" target="_blank"
								   title='<fmt:message key="label.countdown.timer" />'>
									<i class="fa fa-hourglass-half"></i>
									<span class="hidden-xs"><fmt:message key="label.countdown.timer"/></span>
								</a>
							</div>

							<div class="mt-2">
								<c:if test="${lesson.enableLessonIntro}">
									<button id="editIntroButton" class="btn btn-sm btn-primary"
											type="button" onClick="javascript:showIntroductionDialog(${lesson.lessonID})">
										<i class="fa fa-sm fa-info"></i>
										<span class="hidden-xs"><fmt:message key="label.lesson.introduction"/></span>
									</button>
								</c:if>

								<lams:Switch id="gradebookOnCompleteButton" checked="${lesson.gradebookOnComplete}"
											 labelKey="label.display.activity.scores" iconClass="fa fa-sm fa-list-ol" />
							</div>
						</dd>

						<!-- Progress Emails -->
						<dt><fmt:message key="lesson.progress.email"/>:</dt>
						<dd>
							<button id="sendProgressEmail" class="btn btn-primary btn-sm"
									onClick="javascript:sendProgressEmail()"/>
							<i class="fa fa-sm fa-envelope"></i>
							<span class="hidden-xs"><fmt:message key="progress.email.send"/></span>
							</button>
							<button id="configureProgressEmail" class="btn btn-primary btn-sm"
									onClick="javascript:configureProgressEmail()"/>
							<i class="fa fa-sm fa-cog"></i>
							<span class="hidden-xs"><fmt:message key="progress.email.configure"/></span>
							</button>
						</dd>

						<dt class="text-muted mt-4"><small><fmt:message key="label.monitoring.learning.design.path"/></small></dt>
						<dd class="text-muted">
							<small class="text-break font-monospace">
								<c:out value="${ldPath}" escapeXml="true"/>
							</small>
						</dd>
					</dl>
				</div>
			</div>
		</div>

		<div class="component-page-content monitoring-page-content active">
			<header class="d-flex justify-content-between">
				<h1 id="lesson-name"><c:out value="${lesson.lessonName}"/></h1>
				<div class="top-menu">
					<div id="sequenceSearchPhraseContainer" class="input-group">
						<input id="sequenceSearchPhrase" type="search" class="form-control shadow" placeholder="<fmt:message key='label.monitoring.search.learners' />...">
						<button id="sequenceSearchPhraseButton" class="btn bg-white shadow" type="button" disabled onClick="javascript:sequenceClearSearchPhrase(true)" aria-label="<fmt:message key='label.monitoring.search.learners' />">
							<i id="sequenceSearchPhraseIcon" class="fa-solid fa-fw fa-magnifying-glass"></i>
							<i id="sequenceSearchPhraseClearIcon" class="fa-solid fa-fw fa-lg fa-xmark"></i>
						</button>
					</div>
					<!--
				<div class="top-menu-btn component-menu-btn">
					<a href="#" onClick="javscript:refreshMonitor()"><img src="<lams:LAMSURL/>images/components/icon2.svg" alt="#" /></a>
				</div>
				 -->
				</div>
			</header>

			<div class="tab-content pt-2">

			</div>
		</div>
	</div>

	<c:if test="${not empty lesson.lessonInstructions}">
		<div class="d-none" id="lesson-instructions-source">
			<c:out value="${lesson.lessonInstructions}" escapeXml="false" />
		</div>
	</c:if>

	<div id="learnerGroupDialogContents" class="dialogContainer">
		<span id="learnerGroupMultiSelectLabel"><fmt:message key='learner.group.multi.select'/></span>
		<table id="listLearners" class="table table-borderless">
			<tr id="learnerGroupSearchRow">
				<td colspan="5">
					<div class="input-group mb-3">
						<input type="text" class="form-control dialogSearchPhrase" placeholder="<fmt:message key='search.learner.textbox' />"
							   aria-label="<fmt:message key='search.learner.textbox' />">
						<span class="dialogSearchPhraseIcon input-group-text" title="<fmt:message key='search.learner.textbox' />">
				  		<i class=" fa-solid fa-sm fa-search"></i>
				  </span>
					</div>
				</td>
				<td>
					<button class="btn btn-xs btn-secondary dialogSearchPhraseClear"
							onClick="javascript:learnerGroupClearSearchPhrase()"
							title="<fmt:message key='learners.search.phrase.clear.tooltip' />">
						<i class="fa-solid fa-fw fa-xmark"></i>
					</button>
				</td>
			</tr>
			<tr>
				<td class="navCell pageMinus10Cell">
					<button class="btn btn-xs btn-secondary"
							onClick="javascript:shiftLearnerGroupList(-10)"
							title="<fmt:message key='learner.group.backward.10'/>">
						<i class="fa-solid fa-fw fa-step-backward"></i>
					</button>

				</td>
				<td class="navCell pageMinus1Cell">
					<button class="btn btn-xs btn-secondary"
							onClick="javascript:shiftLearnerGroupList(-1)"
							title="<fmt:message key='learner.group.backward.1'/>">
						<i class="fa-solid fa-fw fa-backward"></i>
					</button>
				</td>
				<td class="pageCell"
					title="<fmt:message key='learners.page'/>">
				</td>
				<td class="navCell pagePlus1Cell">
					<button class="btn btn-xs btn-secondary"
							onClick="javascript:shiftLearnerGroupList(1)"
							title="<fmt:message key='learner.group.forward.1'/>">
						<i class="fa-solid fa-fw fa-forward"></i>
					</button>
				</td>
				<td class="navCell pagePlus10Cell">
					<button class="btn btn-xs btn-secondary"
							onClick="javascript:shiftLearnerGroupList(10)"
							title="<fmt:message key='learner.group.forward.10'/>">
						<i class="fa-solid fa-fw fa-step-forward"></i>
					</button>
				</td>
				<td class="navCell sortCell text-end" role="button">
					<button class="btn btn-xs btn-secondary"
							onClick="javascript:sortLearnerGroupList()"
							title="<fmt:message key='learner.group.sort.button'/>">
						<i class="fa-solid fa-fw fa-caret-down"></i>
					</button>
				</td>
			</tr>
			<tr>
				<td colspan="6">
					<table class="dialogTable table table-condensed table-hover"></table>
				</td>
			</tr>
		</table>
		<div class="modal-footer">
			<button id="learnerGroupDialogForceCompleteAllButton" class="btn btn-secondary me-2">
				<span><fmt:message key="button.force.complete.all" /></span>
			</button>
			<button id="learnerGroupDialogForceCompleteButton" class="learnerGroupDialogSelectableButton btn btn-secondary me-2">
				<span><fmt:message key="button.force.complete" /></span>
			</button>

			<button id="learnerGroupDialogViewButton" class="learnerGroupDialogSelectableButton btn btn-secondary me-2">
				<span><fmt:message key="button.view.learner" /></span>
			</button>
			<button id="learnerGroupDialogEmailButton" class="learnerGroupDialogSelectableButton btn btn-secondary me-2">
				<span><fmt:message key="button.email" /></span>
			</button>
			<button id="learnerGroupDialogCloseButton" class="btn btn-primary me-2">
				<span><fmt:message key="button.close" /></span>
			</button>
		</div>
	</div>

	<div id="classDialogContents" class="dialogContainer">
		<div id="classDialogTable">
			<div class="row">
				<div id="leftLearnerTable" class="col-6">
					<table id="classLearnerTable" class="table table-borderless">
						<tr class="table-active">
							<td class="dialogTitle" colspan="6"><fmt:message
									key="lesson.learners" /></td>
						</tr>
						<tr>
							<td colspan="5">
								<div class="input-group mb-3">
									<input type="text" class="form-control dialogSearchPhrase" placeholder="<fmt:message key='search.learner.textbox' />"
										   aria-label="<fmt:message key='search.learner.textbox' />">
									<span class="dialogSearchPhraseIcon input-group-text" title="<fmt:message key='search.learner.textbox' />">
							  		<i class="fa-solid fa-sm fa-search"></i>
							  </span>
								</div>
							</td>
							<td>
								<button class="btn btn-xs btn-secondary dialogSearchPhraseClear"
										onClick="javascript:classClearSearchPhrase()"
										title="<fmt:message key='learners.search.phrase.clear.tooltip' />">
									<i class="fa-solid fa-fw fa-xmark"></i>
								</button>
							</td>
						</tr>
						<tr>
							<td class="navCell pageMinus10Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Learner', -10)"
										title="<fmt:message key='learner.group.backward.10'/>">
									<i class="fa-solid fa-fw fa-step-backward"></i>
								</button>

							</td>
							<td class="navCell pageMinus1Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Learner', -1)"
										title="<fmt:message key='learner.group.backward.1'/>">
									<i class="fa-solid fa-fw fa-backward"></i>
								</button>
							</td>
							<td class="pageCell"
								title="<fmt:message key='learners.page'/>">
							</td>
							<td class="navCell pagePlus1Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Learner', 1)"
										title="<fmt:message key='learner.group.forward.1'/>">
									<i class="fa-solid fa-fw fa-forward"></i>
								</button>
							</td>
							<td class="navCell pagePlus10Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Learner', 10)"
										title="<fmt:message key='learner.group.forward.10'/>">
									<i class="fa-solid fa-fw fa-step-forward"></i>
								</button>
							</td>
							<td class="navCell sortCell text-end" role="button">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:sortClassList('Learner')"
										title="<fmt:message key='learner.group.sort.button'/>">
									<i class="fa-solid fa-fw fa-caret-down"></i>
								</button>
							</td>
						</tr>
						<tr>
							<td colspan="6">
								<table class="dialogTable table table-condensed table-hover"></table>
							</td>
						</tr>
						<tr>
							<td colspan="6">
								<button id="addAllLearnersButton"
										class="btn btn-sm btn-secondary float-end"
										onClick="javascript:addAllLearners()">
									<fmt:message key="button.edit.class.add.all" />
								</button>
							</td>
						</tr>
					</table>
				</div>
				<div id="rightMonitorTable" class="col">
					<table id="classMonitorTable" class="table table-borderless">
						<tr class="table-active">
							<td class="dialogTitle" colspan="6"><fmt:message
									key="lesson.monitors" /></td>
						</tr>
						<tr>
							<td id="classMonitorSearchRow" colspan="6">&nbsp;</td>
						</tr>
						<tr>
							<td class="navCell pageMinus10Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Monitor', -10)"
										title="<fmt:message key='learner.group.backward.10'/>">
									<i class="fa-solid fa-fw fa-step-backward"></i>
								</button>

							</td>
							<td class="navCell pageMinus1Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Monitor', -1)"
										title="<fmt:message key='learner.group.backward.1'/>">
									<i class="fa-solid fa-fw fa-backward"></i>
								</button>
							</td>
							<td class="pageCell"
								title="<fmt:message key='learners.page'/>">
							</td>
							<td class="navCell pagePlus1Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Monitor', 1)"
										title="<fmt:message key='learner.group.forward.1'/>">
									<i class="fa-solid fa-fw fa-forward"></i>
								</button>
							</td>
							<td class="navCell pagePlus10Cell">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:shiftClassList('Monitor', 10)"
										title="<fmt:message key='learner.group.forward.10'/>">
									<i class="fa-solid fa-fw fa-step-forward"></i>
								</button>
							</td>
							<td class="navCell sortCell text-end" role="button">
								<button class="btn btn-xs btn-secondary"
										onClick="javascript:sortClassList('Monitor')"
										title="<fmt:message key='learner.group.sort.button'/>">
									<i class="fa-solid fa-fw fa-caret-down"></i>
								</button>
							</td>
						</tr>
						<tr>
							<td colspan="6">
								<table class="dialogTable table table-condensed table-hover"></table>
							</td>
						</tr>
						<tr>
							<td colspan="6"></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>

	<div id="forceBackwardsDialogContents" class="dialogContainer">
		<div id="forceBackwardsMsg"></div>
		<div class="pull-right mt-3">

			<button id="forceBackwardsRemoveContentNoButton" class="btn btn-primary me-1">
				<span><fmt:message key="force.complete.remove.content.no"/></span>
			</button>

			<button id="forceBackwardsRemoveContentYesButton" class="btn btn-danger me-1">
				<span><fmt:message key="force.complete.remove.content.yes" /></span>
			</button>

			<button id="forceBackwardsCloseButton" class="btn btn-secondary">
				<span><fmt:message key="button.close" /></span>
			</button>
		</div>
	</div>

	<div id="emailProgressDialogContents" class="dialogContainer">
		<div id="emailProgressDialogTable">
			<div>
				<table id="emailProgressTable" class="table">
					<tr class="table-active">
						<td class="dialogTitle" colspan="6"><fmt:message key="progress.email.will.be.sent.on"/></td>
					</tr>
					<tr>
						<td colspan="6">
							<table class="dialogTable table table-condensed table-hover"></table>
						</td>
					</tr>
				</table>
			</div>
			<div class="row mt-2">
				<div class="col-6 form-group">
					<label for="emaildatePicker"><fmt:message key="progress.email.select.date"/></label>
					<input type="text" class="form-control" name="emaildatePicker" id="emaildatePicker" value="" autocomplete="off" />
				</div>
			</div>

			<div class="row mt-2">
				<div class="col-6 text-end">
					<button id="addEmailProgressDateButton"
							class="btn btn-sm btn-primary"
							onClick="javascript:addEmailProgressDate()">
						<fmt:message key="progress.email.add.date"/>
					</button>
				</div>
				<div class="col-6 text-end">
					<button id="addEmailProgressSeriesButton"
							class="btn btn-sm btn-secondary"
							onClick="javascript:addEmailProgressSeries(true)">
						<fmt:message key="progress.email.generate.date.list"/>
					</button>
				</div>
			</div>
		</div>
	</div>

	<div id="confirmationDialog" class="modal dialogContainer fade" tabindex="-1" role="dialog">
		<div class="modal-dialog  modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-body">
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" id="confirmationDialogCancelButton">Cancel</button>
					<button type="button" class="btn btn-primary" id="confirmationDialogConfirmButton">Confirm</button>
				</div>
			</div>
		</div>
	</div>

	<div class="toast-container position-fixed top-0 start-50 translate-middle-x p-3" id="toast-container">
	</div>

	<div id="toast-template" class="toast align-items-center bg-white" role="alert" aria-live="assertive" aria-atomic="true">
		<div class="d-flex">
			<div class="toast-body"></div>
			<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
		</div>
	</div>
	</body>
</lams:html>
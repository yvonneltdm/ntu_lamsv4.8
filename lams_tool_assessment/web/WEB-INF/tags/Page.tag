<%@ tag body-content="scriptless"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<%@ attribute name="type" required="true" rtexprvalue="true"%>
<%@ attribute name="style" required="false" rtexprvalue="true"%>
<%@ attribute name="title" required="false" rtexprvalue="true"%>
<%@ attribute name="titleHelpURL" required="false" rtexprvalue="true"%>
<%@ attribute name="headingContent" required="false" rtexprvalue="true"%>
<%@ attribute name="usePanel" required="false" rtexprvalue="true"%>
<%@ attribute name="hideProgressBar" required="false" rtexprvalue="true"%>

<c:if test="${empty usePanel}">				
	<c:set var="usePanel">true</c:set>
</c:if>

<c:choose>

	<c:when test='${type == "navbar"}'>
	<%-- Combined tab and navigation bar used in authoring and monitoring --%>
		<div class="row no-gutter no-margin">
		<div class="col-xs-12">
		<div class="container" id="content">
		<jsp:doBody />
		</div>
		</div>
		</div>
	</c:when> 

	<c:when test='${type == "learner"}'>
	<%-- Learner --%>

	<%-- Try to get authoring preview/learning/monitoring from the tool activity so we don't show the progress bar in monitoring --%>
	<c:if test="${empty mode}">
		<c:set var="mode" value="${param.mode}" />
	</c:if>
	<c:if test="${empty mode}">
		<c:if test="${empty sessionMapID}">
			<c:set var="sessionMapID" value="${param.sessionMapID}"/>
		</c:if>
		<c:set var="mode" value="${sessionScope[sessionMapID].mode}" />
	</c:if>

	<%--  only have sidebar and presence in learner main window, not in popup windows --%>
	<c:if test="${ not hideProgressBar && ( empty mode || mode == 'author' || mode == 'learner') }">
	
		<%-- Links placed in body instead of head. Ugly, but it works. --%>
		<link rel="stylesheet" href="<lams:LAMSURL/>css/progressBar.css" type="text/css" />
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/snap.svg.js"></script>
		<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/progressBar.js"></script>
		
		<c:if test="${empty lessonID}">
			<c:set var="lessonID" value="${param.lessonID}" />
		</c:if>

		<c:if test="${empty lessonID}">
			<%-- Desperately try to get tool session ID from the tool activity --%>
			<c:if test="${empty toolSessionId}">
				<c:set var="toolSessionId" value="${toolSessionID}" />
			</c:if>
			<c:if test="${empty toolSessionId}">
				<c:set var="toolSessionId" value="${param.toolSessionId}" />
			</c:if>
			<c:if test="${empty toolSessionId}">
				<c:set var="toolSessionId" value="${param.toolSessionID}" />
			</c:if>
 			<c:if test="${empty toolSessionId}">
				<c:if test="${empty sessionMapID}">
					<c:set var="sessionMapID" value="${param.sessionMapID}"/>
				</c:if>
 				<c:if test="${not empty sessionMapID}">
					<c:set var="toolSessionId" value="${sessionScope[sessionMapID].toolSessionID}" />
					<c:if test="${empty toolSessionId}">
						<c:set var="toolSessionId" value="${sessionScope[sessionMapID].toolSessionId}" />
					</c:if>
				</c:if>
			</c:if>
			<c:if test="${empty toolSessionId}">
				<c:set var="toolForm" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
				<c:if test="${not empty toolForm}"> 
				    <c:set var="toolSessionId"><c:catch var="exception">${toolForm.toolSessionID}</c:catch></c:set>
				</c:if>
			</c:if>
		</c:if>
		
		<script type="text/javascript">
			var allowRestart = false,
				restartLessonConfirmation = "Are you sure you want to start the lesson from the beginning?",
				toolSessionId = '${toolSessionId}',
				lessonId = '${lessonID}',
				mode = '${mode}',
				
				LAMS_URL = '<lams:LAMSURL/>',
				APP_URL = LAMS_URL + 'learning/',
						
				bars = {
					'learnerMainBar' : {
						'containerId' : 'progressBarDiv'
					}
				};

			function restartLesson(){
				if (confirm(restartLessonConfirmation)) {
					window.location.href = APP_URL + 'learner.do?method=restartLesson&lessonID=' + lessonId;
				}
			}

			function viewNotebookEntries(){
				openPopUp(APP_URL + "notebook.do?method=viewAll&lessonID=" + lessonId,
						"Notebook",
						570,796,
						"no");
				hideSlideMenu(); /* For touch screen */
			}
		
			function closeWindow() {
	 			top.window.close();
			}

			function toggleSlideMenu() {	
				if ( $("nav.sidebar").hasClass("expandmenu") ) {
					hideSlideMenu();
				}
				else 
					$("nav.sidebar").addClass("expandmenu");
			}

			function hideSlideMenu() {	
				hideProgressBars();
				$("nav.sidebar").removeClass("expandmenu");
			}
			
			function onProgressBarLoaded() {
				$('#exitlabel').html(LABELS.EXIT);
				$('#notebooklabel').html(LABELS.NOTEBOOK);
				$('#supportlabel').html(LABELS.SUPPORT_ACTIVITIES);
				$('#progresslabel').html(LABELS.PROGRESS_BAR);
				if ( allowRestart ) {
					$('#restartlabel').html(LABELS.RESTART);
					restartLessonConfirmation = LABELS.CONFIRM_RESTART;
					$('#restartitem').show();
				}
				$('#sidebar').show();
			}
			
			$(document).ready(function() {
				var showControlBar = 1; // 0/1/2 none/full/keep space
				var showIM = true;
				if ( window.name.match("LearnerActivity") || window.parent.name.match("LearnerActivity")) { 
					// popup window
					showControlBar = 0;
					showIM = false;
				} else 	if ( window.frameElement ) { // parallel
					var myId = window.frameElement.id;
					if ( myId ) {
						if ( myId == 'lamsDynamicFrame0' ) {
							showIM = false;
						} else if ( myId == 'lamsDynamicFrame1') {
							showControlBar = 2;
						}
					}
				}

				if ( lessonId != "" || toolSessionId != "" ) {
					$.ajax({
						url : APP_URL + 'learner.do',
						data : {
							'method'   : 'getLessonDetails',
							'lessonID' : lessonId,
							'toolSessionID' : toolSessionId,
						},
						cache : false,
						dataType : 'json',
						success : function(result) {
							
							lessonId = result.lessonID;
							
							if ( showControlBar == 1  ) {
								allowRestart = result.allowRestart;
								$('.lessonName').html(result.title);
								fillProgressBar('learnerMainBar');
								$('#navcontent').addClass('navcontent');
							} else if ( showControlBar == 2 ) {
								$('#navcontent').addClass('navcontent');
							}
							
							var presenceEnabledPatch = result.presenceEnabledPatch;
							var presenceImEnabled = result.presenceImEnabled;
							if ( showIM && (presenceEnabledPatch || presenceImEnabled) ) {
								presenceURL = APP_URL+"presenceChat.jsp?presenceEnabledPatch="+presenceEnabledPatch
										+"&presenceImEnabled="+presenceImEnabled+"&lessonID="+lessonId;
								$('#presenceEnabledPatchDiv').load(presenceURL, function( response, status, xhr ) {
									if ( status == "error" ) {
										alert("Unable to load IM: " + xhr.status);
									} 
								});
							}
						}
					});
				}
			});
			
		</script>
	
		<nav class="navbar navbar-default sidebar" id="sidebar" role="navigation" onMouseEnter="javascript:toggleSlideMenu()" onMouseLeave="javascript:hideSlideMenu()" style="display:none" >
		    <div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-sidebar-navbar-collapse-1">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand visible-xs hidden-sm hidden-md hidden-lg" href="#"><span class="lessonName"></span></a>
				</div>
				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-sidebar-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li><a href="#" class="hidden-xs visible-sm visible-md visible-lg slidesidemenu" onClick="javascript:toggleSlideMenu()">
							<i class="pull-right fa fa-bars" style="color:#337ab7"></i>
							<p class="lessonName"></p></a></li>
						<li><a href="#" onClick="javascript:closeWindow()" ><span id="exitlabel">Exit</span><i class="pull-right fa fa-times"></i></a></li>
						<li><a href="#" onClick="javascript:viewNotebookEntries(); return false;" ><span id="notebooklabel">Notebook</span><i class="pull-right fa fa-book"></i></a></li>
						<li id="restartitem" style="display:none"><a href="#" onClick="javascript:restartLesson()"><span id="restartlabel">Restart</span><i class="pull-right fa fa-recycle"></i></a></li>
						<li id="supportitem" style="display:none"><a href="#" class="slidesidemenu" onClick="javascript:toggleSlideMenu(); return false;">
							<span id="supportlabel">Support Activities</span><i class="pull-right fa fa-th-large"></i></a>
							<div id="supportPart" class="progressBarContainer"></div>
						<li><a href="#" class="slidesidemenu" onClick="javascript:toggleSlideMenu(); return false;">
							<span id="progresslabel">My Progress</span><i class="pull-right fa fa-map"></i></a>
							<div id="progressBarDiv" class="progressBarContainer"></div></li>
					</ul>
				</div>
			</div>
		</nav>

	</c:if> <%--  end of sidebar stuff - only used if in learner screen --%>

		<div id="navcontent" class="content">
			<div class="row no-gutter no-margin">
			<div class="col-xs-12">
			<div class="container">
				<c:choose>
				<c:when test="${usePanel}">
					<div class="panel panel-default panel-${type}-page">
						<c:if test="${not empty title}">
							<div class="panel-heading">
								<div class="panel-title panel-${type}-title">
									<c:out value="${title}" escapeXml="true" />
									<c:if test="${not empty titleHelpURL}">
										<span class="pull-right">${titleHelpURL}</span>
									</c:if>
								</div>
								<c:if test="${not empty headingContent}">
									<c:out value="${headingContent}" escapeXml="true" />
								</c:if>
							</div>
						</c:if>
						
						<div class="panel-body panel-${type}-body">
							<jsp:doBody />
						</div>
					</div>
					
					<%--  only have sidebar and presence in learner --%>
					<c:if test="${ not hideProgressBar && ( empty mode || mode == 'author' || mode == 'learner') }">
					<div id="presenceEnabledPatchDiv"></div>
					</c:if>
				</c:when>
				<c:otherwise>
					<jsp:doBody />
				</c:otherwise>
				</c:choose>						
			</div>
				</div>
			</div>
		</div>

	</c:when>

	<c:otherwise>
	<!-- Standard Screens  --> 
		<div class="row no-gutter no-margin">
		<div class="col-xs-12">
		<div class="container" id="content">

		<c:choose>
		<c:when test="${usePanel}">
		<div class="panel panel-default panel-${type}-page">
			<c:if test="${not empty title}">
				<div class="panel-heading">
					<div class="panel-title panel-${type}-title">
						<c:out value="${title}" escapeXml="true" />
						<c:if test="${not empty titleHelpURL}">
							<span class="pull-right">${titleHelpURL}</span>
						</c:if>
					</div>
					<c:if test="${not empty headingContent}">
						<c:out value="${headingContent}" escapeXml="true" />
					</c:if>
				</div>
			</c:if>
			
			<div class="panel-body panel-${type}-body">
				<jsp:doBody />
			</div>
		</div>
		</c:when>

		<c:otherwise>
		<jsp:doBody />
		</c:otherwise>
		</c:choose>						
		
		</div>
		</div>
		</div>
	</c:otherwise>
</c:choose>


		
			
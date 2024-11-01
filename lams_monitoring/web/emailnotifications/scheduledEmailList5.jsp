<!DOCTYPE html>

<%@ include file="/taglibs.jsp"%>

<lams:html>
<lams:head>
	<c:if test="${not empty param.sessionMapID}">
		<c:set var="sessionMapID" value="${param.sessionMapID}" />
	</c:if>	
	
	<link rel="stylesheet" href="<lams:LAMSURL/>css/components.css">
	<link rel="stylesheet" href="<lams:LAMSURL/>includes/font-awesome6/css/all.css">
	
	<c:choose>
		<c:when test="${lessonID != null}">
			<c:set var="returnUrlParams">getLessonView.do?newUI=true&lessonID=${lessonID}</c:set>
			<c:set var="deleteUrlParams">lessonID=${lessonID}</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="returnUrlParams">getCourseView.do?newUI=true&organisationID=${organisationID}</c:set>
			<c:set var="deleteUrlParams">organisationID=${organisationID}</c:set>
		</c:otherwise>
	</c:choose>
	
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/bootstrap5.bundle.min.js"></script>
	<script type="text/javascript">
	var deleteConfirmationMessage1 = '<spring:escapeBody javaScriptEscape='true'><fmt:message key="email.notification.delete.alert1"><fmt:param>%replace%</fmt:param></fmt:message></spring:escapeBody>';
	var deleteConfirmationMessage2 = '<spring:escapeBody javaScriptEscape='true'><fmt:message key="email.notification.delete.alert2"/></spring:escapeBody>';
	
	function deleteNotification(triggerName, scheduledate, deleteUrlParams) {
		var msg = deleteConfirmationMessage1;
		msg = msg.replace('%replace%', scheduledate);
		window.parent.showConfirm(msg+'<br><br>'+deleteConfirmationMessage2, function() {
 			$.ajax({
				async : false,
				url : '<c:url value="/emailNotifications/"/>deleteNotification.do?<csrf:token/>',
 				data : '${deleteUrlParams}&triggerName=' + triggerName,
 				type : "POST",
				success : function(json) {
 					if (json.deleteNotification == 'true') {
						window.location.reload();
					} else {
						window.parent.showToast(json.deleteNotification);
					}
				}
			});
 		});
	}
	
	</script>

</lams:head>

<body>
	<h4>
		<fmt:message key="email.notifications.scheduled.messages.list"/>
	</h4>

	<table class="table table-condensed table-striped">
		<thead>
			<tr>
				<th class="text-left" width="25%">
					<fmt:message key="email.notifications.scheduled.messages.list.scheduled.date"/>		
				</td>
				<th  class="text-left">
					<fmt:message key="email.notify.students.that"/>	
				</td>
				<th  class="text-left">
					<fmt:message key="email.notifications.scheduled.messages.list.email.body"/>		
				</td>
			</tr>
		</thead>
		<c:forEach var="emailJob" items="${scheduleList}">
			<tr>
				<td style="vertical-align: top;">
					<c:set var="tDate"><lams:Date value="${emailJob.triggerDate}"/></c:set>
					${tDate}<BR/>
					
					<a href="#" class="btn btn-danger btn-sm" 
						onclick="javascript:deleteNotification('${emailJob.triggerName}', '${tDate}', '${deleteUrlParams}');">
						<i class="fas fa-fw fa-trash"></i> <fmt:message key="email.notifications.delete" />
					</a>
				</td>
				<td  style="vertical-align: top;">
					<c:choose>
						<c:when test="${emailJob.searchType == 0}"><fmt:message key="email.notifications.user.search.property.0" /></c:when>
						<c:when test="${emailJob.searchType == 1}"><fmt:message key="email.notifications.user.search.property.1" /></c:when>
						<c:when test="${emailJob.searchType == 2}"><fmt:message key="email.notifications.user.search.property.2" /></c:when>
						<c:when test="${emailJob.searchType == 3}"><fmt:message key="email.notifications.user.search.property.3" /></c:when>
						<c:when test="${emailJob.searchType == 4}"><fmt:message key="email.notifications.user.search.property.4" /></c:when>
						<c:when test="${emailJob.searchType == 5}"><fmt:message key="email.notifications.user.search.property.5" /></c:when>
						<c:when test="${emailJob.searchType == 6}"><fmt:message key="email.notifications.user.search.property.6" /></c:when>
						<c:when test="${emailJob.searchType == 7}"><fmt:message key="email.notifications.user.search.property.7" /></c:when>
						<c:when test="${emailJob.searchType == 8}"><fmt:message key="email.notifications.user.search.property.8" /></c:when>
						<c:when test="${emailJob.searchType == 9}"><fmt:message key="email.notifications.user.search.property.9" /></c:when>
						<c:when test="${emailJob.searchType == 10}"><fmt:message key="email.notifications.user.search.property.10" /></c:when>
					</c:choose>
				</td>
				<td  style="vertical-align: top;">
					${emailJob.emailBody}
				</td>
			</tr>
		</c:forEach>
	</table>
	
	<a href="<c:url value='/emailNotifications/'/>${returnUrlParams}" class="btn btn-primary float-end">
		<fmt:message key="email.notifications.scheduled.messages.list.back" />
	</a>
</body>
</lams:html>
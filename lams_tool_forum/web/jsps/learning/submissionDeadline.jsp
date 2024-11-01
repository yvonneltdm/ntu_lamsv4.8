<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="title"><fmt:message key="activity.title" /></c:set>

<lams:SubmissionDeadline title="${title}"
                         toolSessionID="${sessionMap.toolSessionID}"
                         submissionDeadline="${sessionMap.submissionDeadline}"
                         finishSessionUrl="/learning/finish.do?sessionMapID=${sessionMapID}"
                         isLastActivity="${sessionMap.isLastActivity}" />
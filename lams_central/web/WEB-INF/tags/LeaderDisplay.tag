<%@ tag body-content="scriptless"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<%@ attribute name="idName" required="false" rtexprvalue="true"%>
<%@ attribute name="messageKey" required="false" rtexprvalue="true"%>
<%@ attribute name="username" required="false" rtexprvalue="true"%>
<%@ attribute name="userId" required="true" rtexprvalue="true"%>

<c:if test="${empty idName}">
	<c:set var="idName" value="alert-leader-${userId}" />
</c:if>

<c:if test="${empty messageKey}">
	<c:set var="messageKey" value="label.group.leader" />
</c:if>

<div id="${idName}" class="col-12 offset-sm-2 col-sm-8">
	<div class="alert alert-info leader-display text-center">
		<span class="fs-5 fw-semibold">
			<fmt:message key="${messageKey}">
				<fmt:param><c:out value="${username}" escapeXml="true"/></fmt:param>
			</fmt:message>
			&nbsp;
			<lams:Portrait userId="${userId}"/>
		</span>
	</div>
</div>

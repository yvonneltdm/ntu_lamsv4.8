<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>

<!DOCTYPE html>
<lams:html>
<lams:head>
		<title><fmt:message key="index.outcome.manage" /></title>
	<lams:css/>
	<link rel="stylesheet" href="css/jquery-ui-bootstrap-theme.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="css/outcome.css" type="text/css" media="screen" />
	
	<script type="text/javascript" src="includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="includes/javascript/jquery-ui.js"></script>
	<script type="text/javascript" src="includes/javascript/bootstrap.min.js"></script>
	<script type="text/javascript" src="includes/javascript/outcome.js"></script>
	<script type="text/javascript" src="includes/javascript/jquery.cookie.js"></script>
	<script type="text/javascript" src="includes/javascript/dialog.js"></script>
	<script type="text/javascript">
		var organisationId = '${param.organisationID}',
			LAMS_URL = '<lams:LAMSURL/>',
			
			decoderDiv = $('<div />'),
			LABELS = {
				<fmt:message key="outcome.manage.add" var="ADD_OUTCOME_TITLE_VAR"/>
				ADD_OUTCOME_TITLE : '<c:out value="${ADD_OUTCOME_TITLE_VAR}" />',
				<fmt:message key="outcome.manage.edit" var="EDIT_OUTCOME_TITLE_VAR"/>
				EDIT_OUTCOME_TITLE : '<c:out value="${EDIT_OUTCOME_TITLE_VAR}" />',
				<fmt:message key="outcome.manage.remove.confirm" var="REMOVE_OUTCOME_CONFIRM_LABEL_VAR"/>
				REMOVE_OUTCOME_CONFIRM_LABEL : decoderDiv.html('<c:out value="${REMOVE_OUTCOME_CONFIRM_LABEL_VAR}" />').text()
			};
	</script>
</lams:head>
<body class="stripes">
<lams:Page type="admin" >
	<div class="outcomeContainer">
		<div class="row">
			<div class="col-xs-5">
				<fmt:message key='outcome.manage.add.name' />
			</div>
			<div class="col-xs-3">
				<fmt:message key='outcome.manage.add.code' />
			</div>
			<div class="col-xs-2">
				<fmt:message key='outcome.manage.scope' />
			</div>
			<div class="col-xs-1">
			</div>
			<div class="col-xs-1">
			</div>
		</div>
		<c:forEach var="outcome" items="${outcomes}">
			<div class="row">
				<div class="col-xs-5">
					<c:out value="${outcome.name}" />
				</div>
				<div class="col-xs-3">
					<c:out value="${outcome.code}" />
				</div>
				<div class="col-xs-2">
					<c:choose>
						<c:when test="${empty outcome.organisation}">
							<fmt:message key='outcome.manage.scope.global' />
						</c:when>
						<c:otherwise>
							<fmt:message key='outcome.manage.scope.course' />
						</c:otherwise>
					</c:choose>
				</div>
				<div class="col-xs-1">
					<c:choose>
						<c:when test="${not empty outcome.organisation or canManageGlobal}">
							<i class="manageButton fa fa-pencil" title="<fmt:message key='outcome.manage.edit' />"
						   	   onClick="javascript:openEditOutcomeDialog(${outcome.outcomeId})" >
							</i>
						</c:when>
						<c:otherwise>
							<i class="manageButton fa fa-eye" title="<fmt:message key='outcome.manage.view' />"
						   	   onClick="javascript:openEditOutcomeDialog(${outcome.outcomeId})" >
							</i>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="col-xs-1">
					<c:if test="${not empty outcome.organisation or canManageGlobal}">
						<i class="manageButton fa fa-remove" title="<fmt:message key='outcome.manage.remove' />"
					   	   onClick="javascript:removeOutcome(${outcome.outcomeId})" >
						</i>
					</c:if>
				</div>
			</div>
		</c:forEach>
		<c:if test="${canManageGlobal and not empty outcomes}">
			<div id="exportButton" class="btn btn-default pull-left" onClick="javascript:exportOutcome()"
				 data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i><span> <fmt:message key="outcome.export" /></span>">
				<i class="fa fa-download"></i>
				<span class="hidden-xs">
					<fmt:message key="outcome.export" />
				</span>
			</div> 
		</c:if>
		<div id="addButton" class="btn btn-primary" onClick="javascript:openEditOutcomeDialog()">
			<i class="fa fa-plus"></i>
			<span><fmt:message key='outcome.manage.add' /></span>
		</div>
	</div>
</lams:Page>
</body>
</lams:html>
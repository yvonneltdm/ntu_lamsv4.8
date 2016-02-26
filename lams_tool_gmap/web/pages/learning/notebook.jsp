<!DOCTYPE html>
            
<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<lams:html>

	<lams:head>  
	
		<title>
			<fmt:message>pageTitle.monitoring.notebook</fmt:message>
		</title>
		
		<lams:css/>
		
		<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
		
	</lams:head>
	
	<body class="stripes">
	
			<div id="content">
			
			<h1>
				<c:out value="${gmapDTO.title}" escapeXml="true"/>
			</h1>
		
			<html:form action="/learning" method="post">
				<html:hidden property="toolSessionID" styleId="toolSessionID"/>
				<html:hidden property="markersXML" />
				<html:hidden property="mode" value="${mode}" />
				
				<p class="small-space-top">
					<lams:out value="${gmapDTO.reflectInstructions}" escapeHtml="true"/>
				</p>
		
				<html:textarea cols="60" rows="8" property="entryText"
					styleClass="text-area"></html:textarea>
		
				<div class="space-bottom-top align-right">
					<html:hidden property="dispatch" value="submitReflection" />
					<html:link href="#nogo" styleClass="button" styleId="finishButton" 
				          onclick="javascript:document.learningForm.submit();return false">
						<span class="nextActivity">
							<c:choose>
			 					<c:when test="${activityPosition.last}">
			 						<fmt:message key="button.submit" />
			 					</c:when>
			 					<c:otherwise>
			 		 				<fmt:message key="button.finish" />
			 					</c:otherwise>
				 			</c:choose>
			 			</span>
					</html:link>
				</div>
			</html:form>
				
			</div>
			<div class="footer"></div>
	</body>
</lams:html>

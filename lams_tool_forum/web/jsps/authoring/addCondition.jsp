<!DOCTYPE html>
		
<%@ include file="/common/taglibs.jsp"%>

<lams:html>
	<lams:head>
		<%@ include file="/common/header.jsp"%>
		<lams:css style="main" />

	</lams:head>
	<body class="tabpart">
		<!-- Basic Info Form-->
		<%@ include file="/common/messages.jsp"%>
		<html:form action="/authoring/saveOrUpdateCondition" method="post" styleId="forumConditionForm" focus="displayName" >
			<html:hidden property="orderId" />
			<h2 class="no-space-left">
				<fmt:message key="label.authoring.conditions.add.condition" />
			</h2>

			<div class="field-name">
            	<fmt:message key="label.authoring.conditions.condition.name" />
			</div>

			<div class="small-space-bottom">
         		<html:text property="displayName" size="51"/>
			</div>
			<%-- Text search form fields are being included --%>
			<lams:TextSearch wrapInFormTag="false" sessionMapID="${sessionMapID}"  />
			<h4 class="no-space-left"><fmt:message key="textsearch.topics" /></h4>
			<logic:iterate name="forumConditionForm" id="itemE" property="possibleItems">
			  	<html:multibox property="selectedItems">
			    	<bean:write name="itemE" property="value" />
			  	</html:multibox>
			    <bean:write name="itemE" property="label" />
			    <br />
			</logic:iterate>

		</html:form>

		<lams:ImgButtonWrapper>
			<a href="javascript:;" onclick="javascript:forumConditionForm.submit();" class="button-add-item"><fmt:message
					key="label.save" /> </a>
			<a href="javascript:;" onclick="javascript:window.parent.hideConditionMessage();"
				class="button space-left"><fmt:message key="button.cancel" /> </a>
		</lams:ImgButtonWrapper>
	</body>
</lams:html>

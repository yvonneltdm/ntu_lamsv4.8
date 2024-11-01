<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.usermanagement.AuthenticationMethod"
	import="org.lamsfoundation.lams.util.Configuration"
	import="org.lamsfoundation.lams.util.ConfigurationKeys"%>
<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="profileEditEnabled"><%=Configuration.get(ConfigurationKeys.PROFILE_EDIT_ENABLE)%></c:set>
<c:set var="partialProfileEditEnabled"><%=Configuration.get(ConfigurationKeys.PROFILE_PARTIAL_EDIT_ENABLE)%></c:set>
<c:set var="authenticationMethodId"><lams:user property="authenticationMethodId" /></c:set>
<c:set var="dbId"><%=AuthenticationMethod.DB%></c:set>
<%-- This gets overwritten for a client during build process (SP-3) --%>
<c:set var="editOnlyName" value="false" />
							
<lams:html>
<lams:head>
	<link rel="stylesheet" href="${lams}css/components.css">
    <link rel="stylesheet" href="${lams}includes/font-awesome6/css/all.css">
	<style type="text/css">
		body {
			overflow-x:hidden;
		}
	</style>

	<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/bootstrap5.bundle.min.js"></script>
	<lams:JSImport src="includes/javascript/profile.js" />
	<script type="text/javascript">
		$(document).ready( function() {
			if ('${submitted}' == true && $('#error').length == 0) {
				window.parent.location.reload();
			} 
			
			//update dialog's height and title
			updateMyProfileDialogSettings(
				'<spring:escapeBody javaScriptEscape="true"><fmt:message key="title.profile.edit.screen" /></spring:escapeBody>',
				'100%'
			);
		});
	</script>
</lams:head>

<body class="component no-decoration">
	<form:form action="/lams/saveprofile.do" modelAttribute='newForm' method="post" id='newForm'>
		<input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue/>"/>
		<input type="hidden" name="editNameOnly" value="${editOnlyName}" />

		<lams:errors5 path="*"/>

		<div style="clear: both;"></div>
		<div class="container">
			<div class="col-12 col-sm-8 col-sm-offset-2 col-md-8 col-md-offset-2 mx-auto">
							<c:if test="${authenticationMethodId eq dbId}">

								<div class="my-3">
									<span class="lead">
										<label class="form-label">
											<fmt:message key="label.username" />
										</label>: 
										<lams:user property="login" />
									</span>
								</div>
								
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.title" />:</label>
									<form:input path="title" size="32" maxlength="32"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.first_name" /> *:</label>
									<form:input path="firstName" size="50" maxlength="128"
										disabled="${!profileEditEnabled}" cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.last_name" /> *:</label>
									<form:input path="lastName" size="50" maxlength="128"
										disabled="${!profileEditEnabled}" cssClass="form-control" />
								</div>
								<c:if test="${!profileEditEnabled}">
									<form:hidden path="firstName" />
									<form:hidden path="lastName" />
								</c:if>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.email" /> *:</label>
									<form:input path="email" size="50" maxlength="128"
										disabled="${!profileEditEnabled and !partialProfileEditEnabled or editOnlyName}"
										cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_1" />:</label>
									<form:input path="addressLine1" size="50" maxlength="64"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_2" />:</label>
									<form:input path="addressLine2" size="50" maxlength="64"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_3" />:</label>
									<form:input path="addressLine3" size="50" maxlength="64"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.city" />:</label>
									<form:input path="city" size="50" maxlength="64"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.state" />:</label>
									<form:input path="state" size="50" maxlength="64"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.postcode" />:</label>
									<form:input path="postcode" size="10" maxlength="10"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.country" /> *:</label>

									<form:select path="country" disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-select">
										<form:option value="0">
											<fmt:message key="label.select.country" />
										</form:option>
										<c:forEach items="${countryCodes}" var="countryCode">
											<form:option value="${countryCode.key}">
												${countryCode.value}
											</form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.day_phone" />:</label>
									<form:input path="dayPhone" size="50" maxlength="64"
										disabled="${!profileEditEnabled and !partialProfileEditEnabled or editOnlyName}"
										cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.evening_phone" />:</label>
									<form:input path="eveningPhone" size="50" maxlength="64"
										disabled="${!profileEditEnabled and !partialProfileEditEnabled or editOnlyName}"
										cssClass="form-control" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.mobile_phone" />:</label>
									<form:input path="mobilePhone" size="50" maxlength="64"
										disabled="${!profileEditEnabled and !partialProfileEditEnabled or editOnlyName}"
										cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.fax" />:</label>
									<form:input path="fax" size="50" maxlength="64"
										disabled="${!profileEditEnabled and !partialProfileEditEnabled or editOnlyName}"
										cssClass="form-control" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.language" />:</label>
									<form:select path="localeId"
										disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-select">
										<c:forEach items="${locales}" var="locale">
											<form:option value="${locale.localeId}">
												<c:out value="${locale.description}" />
											</form:option>
										</c:forEach>
									</form:select>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.timezone.title" />:</label>
									<form:select path="timeZone" disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-select">
										<c:forEach items="${timezones}" var="timezone">
											<form:option value="${timezone}"><c:out value="${timezone}" /></form:option>
										</c:forEach>
									</form:select>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.theme" />:</label>
									<form:select path="userTheme" disabled="${!profileEditEnabled or editOnlyName}" cssClass="form-select">
										<c:forEach items="${themes}" var="theme">
											<form:option value="${theme.themeId}">${theme.name}</form:option>
										</c:forEach>
									</form:select>
								</div>
								
							</c:if>
							<br />

							<c:if test="${authenticationMethodId != dbId}">
								<form:hidden path="title" />
								<form:hidden path="firstName" />
								<form:hidden path="lastName" />
								<form:hidden path="email" />
								<form:hidden path="addressLine1" />
								<form:hidden path="addressLine2" />
								<form:hidden path="addressLine3" />
								<form:hidden path="city" />
								<form:hidden path="state" />
								<form:hidden path="postcode" />
								<form:hidden path="country" />
								<form:hidden path="dayPhone" />
								<form:hidden path="eveningPhone" />
								<form:hidden path="mobilePhone" />
								<form:hidden path="fax" />

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.username" /></label> 
									<lams:user property="login" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.first_name" /> *:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.firstName}"/>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.last_name" /> *:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.lastName}"/>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.email" /> *:</label>
									 <input
										type="text" class="form-control"
										value="${UserForm.email}"/>
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_1" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.addressLine1}"/>
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_2" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.addressLine2}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.address_line_3" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.addressLine3}" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.city" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.city}" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.state" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.state}" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.postcode" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.postcode}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.country" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.country}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.day_phone" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.dayPhone}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.evening_phone" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.eveningPhone}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.mobile_phone" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.mobilePhone}" />
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.fax" />:</label> 
									<input
										type="text" class="form-control"
										value="${UserForm.fax}" />
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.theme" />:</label>
									<form:select path="userTheme"
										disabled="${!profileEditEnabled}" cssClass="form-select">
										<c:forEach items="${themes}" var="theme">
											<form:option value="${theme.themeId}">${theme.name}</form:option>
										</c:forEach>
									</form:select>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.language" />:</label>
									<form:select path="localeId"
										disabled="${!profileEditEnabled}" cssClass="form-select">
										<c:forEach items="${locales}" var="locale">
											<form:option value="${locale.localeId}">
												<c:out value="${locale.description}" />
											</form:option>
										</c:forEach>
									</form:select>
								</div>

								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.timezone.title" />:</label>
									<form:select path="timeZone" disabled="${!profileEditEnabled}" cssClass="form-select">
										<c:forEach items="${timezones}" var="timezone">
											<form:option value="${timezone}"><c:out value="${timezone}" /></form:option>
										</c:forEach>
									</form:select>
								</div>
								<div class="mb-3">
									<label class="form-label"><fmt:message key="label.timezone.title" />:</label>
									<c:set var="timeZone">
										<input type="text" class="form-control" value="${UserForm.timeZone}" />
									</c:set>
									${timeZone}
								</div>
							</c:if>

					<div class="mb-4 float-end">
						<button type="button" class="btn btn-sm btn-secondary me-2" id="cancelEditProfile" onclick="history.go(-1);">
							<i class="fa-solid fa-ban me-1"></i>
							<fmt:message key="button.cancel" />
						</button>
						
						<c:if test="${profileEditEnabled or partialProfileEditEnabled}">
							<button type="submit" class="btn btn-sm btn-primary" name="submit"  id="saveEditProfile">
								<i class="fa-regular fa-floppy-disk me-1"></i> 
								<fmt:message key="button.save" />
							</button>
						</c:if>
					</div>
			</div>
		</div>
	</form:form>
</body>
</lams:html>

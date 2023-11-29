<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration" %>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys" %>
<%@ page import="org.lamsfoundation.lams.util.FileValidatorUtil" %>
<c:set var="UPLOAD_FILE_MAX_SIZE"><%=Configuration.get(ConfigurationKeys.UPLOAD_FILE_MAX_SIZE)%></c:set>
<c:set var="UPLOAD_FILE_MAX_SIZE_AS_USER_STRING"><%=FileValidatorUtil.formatSize(Configuration.getAsInt(ConfigurationKeys.UPLOAD_FILE_MAX_SIZE))%></c:set>
<c:set var="EXE_FILE_TYPES"><%=Configuration.get(ConfigurationKeys.EXE_EXTENSIONS)%></c:set>

<!-- Add a File Form-->
<div class="card lcard">
	<div class="card-header">
		<fmt:message key="label.learning.new.file" />
	</div>
	<div class="card-body">
	
	<lams:errors5/>
	
	<form:form action="saveOrUpdateItem.do" method="post" modelAttribute="resourceItemForm" id="resourceItemForm" focus="title">
		<form:hidden path="itemType" id="itemType" />
		<form:hidden path="mode" id="mode"/>
		<form:hidden path="sessionMapID"/>
	
		<div class="mb-3">
	    	<label for="title"><fmt:message key="label.authoring.basic.resource.title.input" /></label>:
			<form:input path="title" class="form-control" />
	  	</div>	
		<div class="mb-3">
	    	<div id="instructions-label">
	    		<fmt:message key="label.authoring.basic.instruction" />
	    	</div>
			<lams:CKEditor id="instructions-ckeditor" value="" contentFolderID="${learnerContentFolder}"
				ariaLabelledby="instructions-label"></lams:CKEditor>
	  	</div>	
	  	
		<input type="hidden" id="tmpFileUploadId" name="tmpFileUploadId" value="${resourceItemForm.tmpFileUploadId}" />
		<div id="image-upload-area" class="mt-4"></div>
		
		<lams:WaitingSpinner id="itemAttachmentArea_Busy"/>	
		<div id="buttons" class="float-end mt-2">
	 		<button type="button" name="goback" onclick="javascript:cancel()" class="btn btn-sm btn-secondary btn-icon-cancel" id="cancelButton">
				<fmt:message key="button.cancel" />
			</button>&nbsp;
			<button type="submit" class="btn btn-sm btn-secondary btn-disable-on-submit" id="submitButton">
				<i class="fa fa-plus"></i>
			 	<fmt:message key="button.add" />
			</button>
		</div>
	</form:form>
	
	<script type="text/javascript">
		var UPLOAD_FILE_LARGE_MAX_SIZE = '<c:out value="${UPLOAD_FILE_LARGE_MAX_SIZE}"/>',
			// convert Java syntax to JSON
	       EXE_FILE_TYPES = JSON.parse("[" + "${EXE_FILE_TYPES}".replace(/\.\w+/g, '"$&"') + "]"),
		   EXE_FILE_ERROR = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.attachment.executable"/></spring:escapeBody>';

		$(document).ready(function(){
			$('#title').focus();
			
			var extensionValidation = function(currentFile, files) {
			  var name = currentFile.data.name || currentFile.name,
			  	  extensionIndex = name.lastIndexOf('.'),
			  	  valid = extensionIndex < 0 || !EXE_FILE_TYPES.includes(name.substring(extensionIndex).trim());
			  if (!valid) {
				  uppy.info(EXE_FILE_ERROR, 'error', 10000);
			  }
			  
			  return valid;
		    }
			initFileUpload('${resourceItemForm.tmpFileUploadId}', extensionValidation, '<lams:user property="localeLanguage"/>');
		});	
					
		$('#resourceItemForm').submit(submitResourceForm)
							  .validate({
				ignore: 'hidden, div.cke_editable',
				errorClass: "text-danger",
				wrapper: "span",
				rules: {
				    title: {
				    	required: true
				    }
				},
				messages : {
					title : {
						required : '<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.resource.item.title.blank"/></spring:escapeBody> '
					}
				},
				errorPlacement: function(error, element) {
			       error.insertAfter(element);
			    }
		});	
	</script>
	
	</div>
</div>

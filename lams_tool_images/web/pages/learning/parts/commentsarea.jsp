<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams"><lams:LAMSURL/></c:set>
<c:if test="${not empty param.sessionMapID}"><c:set var="sessionMapID" value="${param.sessionMapID}" /></c:if>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="mode" value="${sessionMap.mode}" />
<c:set var="imageGallery" value="${sessionMap.imageGallery}" />
<c:set var="finishedLock" value="${sessionMap.finishedLock}" />
<c:set var="isImageSelected" value="${not empty sessionMap.currentImage}" />
<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />

<style>
	#new-image-input-area {
		clear: both;
	}
</style>

<lams:JSImport src="includes/javascript/common.js" />
<c:if test="${isImageSelected}">
	<script type="text/javascript">
		//vars for rating.js
		var AVG_RATING_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.average.rating"><fmt:param>@1@</fmt:param><fmt:param>@2@</fmt:param></fmt:message></spring:escapeBody>',
		YOUR_RATING_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.your.rating"><fmt:param>@1@</fmt:param><fmt:param>@2@</fmt:param><fmt:param>@3@</fmt:param></fmt:message></spring:escapeBody>',
		COMMENTS_MIN_WORDS_LIMIT = ${sessionMap.commentsMinWordsLimit},
		MAX_RATES = ${imageGallery.maximumRates},
		MIN_RATES = ${imageGallery.minimumRates},
		COUNT_RATED_ITEMS = ${sessionMap.countRatedItems},
		COMMENT_TEXTAREA_TIP_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.comment.textarea.tip"/></spring:escapeBody>',
		WARN_COMMENTS_IS_BLANK_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.resource.image.comment.blank"/></spring:escapeBody>',
		WARN_MIN_NUMBER_WORDS_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="warning.minimum.number.words"><fmt:param value="${sessionMap.commentsMinWordsLimit}"/></fmt:message></spring:escapeBody>',
		SESSION_ID = ${toolSessionID};
	</script>
	<lams:JSImport src="includes/javascript/rating5.js" />
</c:if>
<lams:JSImport src="includes/javascript/uploadImageLearning.js" relative="true" />
<script type="text/javascript">
	$(document).ready(function(){
		$('#voting-form-checkbox').click(function() {
			$('#voting-form').ajaxSubmit( {
				success: afterVotingSubmit  // post-submit callback
			});
		});
	});

	// post-submit callback 
	function afterVotingSubmit(responseText, statusText)  {
		var votingFormLabel;
		if ($('#voting-form-checkbox').is(':checked')) {
			votingFormLabel = "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learning.unvote'/></spring:escapeBody>";					
				 
		} else {
			votingFormLabel = "<spring:escapeBody javaScriptEscape='true'><fmt:message key='label.learning.vote.here'/></spring:escapeBody>";
		}
		$('#voting-form-label').text(votingFormLabel);
	}
</script>

<c:if test="${(imageGallery.allowRank || imageGallery.allowVote || imageGallery.allowShareImages)}">

	<%--Ranking area---------------------------------------%>
	
	<c:if test="${imageGallery.allowRank && isImageSelected}">
		<lams:Rating5 itemRatingDto="${sessionMap.itemRatingDto}" disabled="${finishedLock || (mode == 'teacher')}" isDisplayOnly="${sessionMap.isAuthor}"
				maxRates="${imageGallery.maximumRates}" countRatedItems="${sessionMap.countRatedItems}"
				minNumberWordsLabel="label.minimum.number.words"/>
	</c:if>

	<div id="extra-controls" class="mb-3">
				
		<%--Voting area--------------%>
		<c:if test="${imageGallery.allowVote && isImageSelected}">
			<form:form action="vote.do" method="post" modelAttribute="imageRatingForm" id="voting-form">
				<input type="hidden" name="sessionMapID" value="${sessionMapID}"/>
				<input type="hidden" name="imageUid" value="${sessionMap.currentImage.uid}"/>

				<div id="favourite-button" class="form-check float-end">
					<input type="checkbox" name="vote" class="form-check-input" id="voting-form-checkbox" 
							<c:if test="${finishedLock || (mode == 'teacher')}">disabled="disabled"</c:if>	
							<c:if test="${sessionMap.isVoted}">checked="checked"</c:if>	
					/>
						
					<label for="voting-form-checkbox" id="voting-form-label" class="form-check-label">
						<c:choose>
							<c:when test="${sessionMap.isVoted}">
								<fmt:message key='label.learning.unvote'/>
							</c:when>
							<c:otherwise>
								<fmt:message key='label.learning.vote.here'/>
							</c:otherwise>
						</c:choose>
					</label>
				</div>
			</form:form>
		</c:if>
			
		<%--"Check for new", "Add new image" and "Delete" buttons---------------%>		
		<div id="manage-image-buttons" class="btn-group" role="group" aria-label="Control buttons">	
			<c:if test="${imageGallery.allowShareImages && (mode != 'teacher')}">
				<button type="button" onclick="return checkNew()" class="btn btn-sm btn-outline-secondary btn-icon-refresh" id="check-for-new-button"> 
					<fmt:message key="label.check.for.new" /> 
				</button>
								
				<c:if test="${not finishedLock}">
					<button type="button" onclick="javascript:newImageInit('<lams:WebAppURL />authoring/newImageInit.do?sessionMapID=${sessionMapID}&bootstrap5=true&saveUsingLearningAction=true');"
							class="btn btn-outline-secondary btn-sm btn-icon-add" id="add-new-image-button"> 
						<fmt:message key="label.learning.add.new.image" />
					</button>
				</c:if>
					
				<c:if test="${sessionMap.isAuthor}">
					<button type="button" onclick="return deleteImage(${sessionMap.currentImage.uid});" class="btn btn-outline-secondary btn-sm btn-icon-remove" id="delete-button"> 
						<fmt:message key="label.learning.delete.image" /> 
					</button>
				</c:if>
			</c:if>
		</div>		
	</div>
</c:if>
<% 
 /**
  * Rating.tag
  *	Author: Andrey Balan
  *	Description: Shows rating stars widget
  */
 %>
<%@ tag body-content="scriptless" %>
<%@ taglib uri="tags-core" prefix="c" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %> 
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-function" prefix="fn" %>
<c:set var="lams"><lams:LAMSURL/></c:set>

<%@ attribute name="itemRatingDto" required="true" rtexprvalue="true" type="org.lamsfoundation.lams.rating.dto.ItemRatingDTO" %>

<%-- Optional attribute --%>
<%@ attribute name="disabled" required="false" rtexprvalue="true" %><%-- i.e. user has rating/comment rights but rating/comment should be disabled --%>
<%@ attribute name="isDisplayOnly" required="false" rtexprvalue="true" %><%-- i.e. user has no rating/comment rights  --%>
<%@ attribute name="maxRates" required="false" rtexprvalue="true" %>
<%@ attribute name="countRatedItems" required="false" rtexprvalue="true" %>
<%@ attribute name="yourRatingLabel" required="false" rtexprvalue="true" %>
<%@ attribute name="averageRatingLabel" required="false" rtexprvalue="true" %>
<%@ attribute name="minNumberWordsLabel" required="false" rtexprvalue="true" %>
<%@ attribute name="starsRateLabel" required="false" rtexprvalue="true" %>
<%@ attribute name="postCommentButtonLabel" required="false" rtexprvalue="true" %>
<%@ attribute name="hideCriteriaTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="showComments" required="false" rtexprvalue="true" %>
<%@ attribute name="showAllComments" required="false" rtexprvalue="true" %>
<%@ attribute name="allowRetries" required="false" rtexprvalue="true" %>
<%-- ID of HTML element where to scroll after refresh after comment was submitted --%>
<%@ attribute name="refreshOnComment" required="false" rtexprvalue="true" %>

<%-- Default value for message key --%>
<c:if test="${empty disabled}">
	<c:set var="disabled" value="false" scope="request"/>
</c:if>
<c:if test="${empty isDisplayOnly}">
	<c:set var="isDisplayOnly" value="false" scope="request"/>
</c:if>
<c:if test="${empty maxRates}">
	<c:set var="maxRates" value="0" scope="request"/>
</c:if>
<c:if test="${empty countRatedItems}">
	<c:set var="countRatedItems" value="0" scope="request"/>
</c:if>
<c:if test="${empty yourRatingLabel}">
	<c:set var="yourRatingLabel" value="label.your.rating" scope="request"/>
</c:if>
<c:if test="${empty averageRatingLabel}">
	<c:set var="averageRatingLabel" value="label.average.rating" scope="request"/>
</c:if>
<c:if test="${empty minNumberWordsLabel}">
	<c:set var="minNumberWordsLabel" value="label.comment.minimum.number.words" scope="request"/>
</c:if>
<c:if test="${empty starsRateLabel}">
	<c:set var="starsRateLabel" value="label.stars.rate" scope="request"/>
</c:if>
<c:if test="${empty postCommentButtonLabel}">
	<c:set var="postCommentButtonLabel" value="button.post.comment" scope="request"/>
</c:if>
<c:if test="${empty hideCriteriaName}">
	<c:set var="hideCriteriaTitle" value="false" scope="request"/>
</c:if>
<c:if test="${empty showAllComments}">
	<c:set var="showAllComments" value="false" scope="request"/>
</c:if>
<c:if test="${empty showComments}">
	<c:set var="showComments" value="true" scope="request"/>
</c:if>
<c:set var="isCommentsEnabled" value="${itemRatingDto.commentsEnabled && showComments}"/>
<c:if test="${empty refreshOnComment}">
	<c:set var="refreshOnComment" value="" scope="request"/>
</c:if>

<c:if test="${isCommentsEnabled}">
	<c:set var="userId"><lams:user property="userID" /></c:set>
	<c:forEach var="comment" items="${itemRatingDto.commentDtos}">
		<c:if test="${comment.userId == userId}">
			<c:set var="commentLeftByUser" value="${comment}"/>
		</c:if>
	</c:forEach>
</c:if>

<c:if test="${empty allowRetries}">
	<c:set var="allowRetries" value="false" scope="request"/>
</c:if>

<%--Rating stars area---------------------------------------%>

<div class="extra-controls-inner">
<div class="starability-holder">

	<c:set var="hasStartedRating" value="false"/>
	<c:forEach var="criteriaDto" items="${itemRatingDto.criteriaDtos}">
		<c:set var="hasStartedRating" value='${hasStartedRating || criteriaDto.userRating != ""}'/>
	</c:forEach>
	<c:set var="hasStartedRating" value='${hasStartedRating || not empty commentLeftByUser}'/>
	
	<c:forEach var="criteriaDto" items="${itemRatingDto.criteriaDtos}" varStatus="status">
		<c:set var="objectId" value="${criteriaDto.ratingCriteria.ratingCriteriaId}-${itemRatingDto.itemId}"/>
		<c:set var="isCriteriaRatedByUser" value='${criteriaDto.userRating != ""}'/>
		<c:set var="isWidgetDisabled" value="${disabled || isDisplayOnly || ((maxRates > 0) && (countRatedItems >= maxRates) && !hasStartedRating) || (isCriteriaRatedByUser && !allowRetries)}"/>
		<c:set var="dataRating">
			<c:choose>
				<c:when test='${isDisplayOnly || isCriteriaRatedByUser}'>
					<fmt:formatNumber value="${criteriaDto.averageRating-(criteriaDto.averageRating%1)}" pattern="#"></fmt:formatNumber>
					${isWidgetDisabled && (criteriaDto.averageRating%1) >= 0.5 ? '.5' : ''}
				</c:when>
				<c:otherwise>
					0
				</c:otherwise>
			</c:choose>
		</c:set>
		<c:set var="legend">
			<c:if test="${not hideCriteriaTitle}">
				<legend class="text-muted fw-bold">
					${criteriaDto.ratingCriteria.title}
				</legend>
			</c:if>
		</c:set>

		<c:choose>
			<c:when test='${isWidgetDisabled}'>
				${legend}
			
				<div class="starability starability-result" data-rating="${dataRating}">
					Rated: ${dataRating} stars
				</div>
			</c:when>
			
			<c:otherwise>
				<fieldset class="starability starability-grow starability-new" data-average="${dataRating}" data-id="${objectId}" aria-label="<fmt:message key="${starsRateLabel}"/>">
					${legend}
					
					<input type="radio" id="${objectId}-0" class="input-no-rate" name="${objectId}" value="0" aria-label="No rating." 
							${dataRating == 0? 'checked' : ''}/>
					
					<input type="radio" id="${objectId}-1" name="${objectId}" value="1" 
							${dataRating == 1? 'checked' : ''}/>
					<label for="${objectId}-1" title="Terrible">1 star</label>
					
					<input type="radio" id="${objectId}-2" name="${objectId}" value="2" 
							${dataRating == 2? 'checked' : ''}/>
					<label for="${objectId}-2" title="Not good">2 stars</label>
					
					<input type="radio" id="${objectId}-3" name="${objectId}" value="3" 
							${dataRating == 3? 'checked' : ''}/>
					<label for="${objectId}-3" title="Average">3 stars</label>
					
					<input type="radio" id="${objectId}-4" name="${objectId}" value="4" 
							${dataRating == 4? 'checked' : ''}/>
					<label for="${objectId}-4" title="Very good">4 stars</label>
					
					<input type="radio" id="${objectId}-5" name="${objectId}" value="5" 
							${dataRating == 5? 'checked' : ''}/>
					<label for="${objectId}-5" title="Amazing">5 stars</label>
					
					<span class="starability-focus-ring"></span>
				</fieldset>
			</c:otherwise>
		</c:choose>
			
		<c:choose>
			<c:when test="${isDisplayOnly}">
				<div class="starability-caption">
					<fmt:message key="${averageRatingLabel}" >
						<fmt:param>
							<fmt:formatNumber value="${criteriaDto.averageRating}" type="number" maxFractionDigits="1" />
						</fmt:param>
						<fmt:param>
							${criteriaDto.numberOfVotes}
						</fmt:param>
					</fmt:message>
				</div>
			</c:when>
				
			<c:otherwise>
				<div class="starability-caption" id="starability-caption-${objectId}"
					<c:if test="${!isCriteriaRatedByUser}">style="visibility: hidden;"</c:if>
				>
					<fmt:message key="${yourRatingLabel}" >
						<fmt:param>
							<span id="user-rating-${objectId}">
								<fmt:formatNumber value="${criteriaDto.userRating}" type="number" maxFractionDigits="1" />
							</span>
						</fmt:param>			
						<fmt:param>
							<span id="average-rating-${objectId}">
								<fmt:formatNumber value="${criteriaDto.averageRating}" type="number" maxFractionDigits="1" />
							</span>
						</fmt:param>
						<fmt:param>
							<span id="number-of-votes-${objectId}">${criteriaDto.numberOfVotes}</span>
						</fmt:param>
					</fmt:message>
				</div>
			</c:otherwise>
		</c:choose>
			
	</c:forEach>
</div>
</div>

<%--Comments area---------------------------------------%>
<c:if test="${isCommentsEnabled}">
	<div id="comments-area-${itemRatingDto.itemId}">
		<c:choose>
			<c:when test='${isDisplayOnly or (showAllComments and not empty commentLeftByUser)}'>
				<c:forEach var="comment" items="${itemRatingDto.commentDtos}">
					<div class="rating-comment">
						<c:out value="${comment.comment}" escapeXml="false" />
					</div>
				</c:forEach>
			</c:when>
			
			<c:when test='${not empty commentLeftByUser}'>
				<div class="rating-comment">
					<c:out value="${commentLeftByUser.comment}" escapeXml="false" />
				</div>
			</c:when>
			
			<c:when test='${not ( disabled || (maxRates > 0) && (countRatedItems >= maxRates) && !hasStartedRating )}'>
				<div id="add-comment-area-${itemRatingDto.itemId}">
			
					<!-- Comment min words limit -->
					<c:if test="${itemRatingDto.commentsMinWordsLimit ne 0}">
						<lams:Alert5 type="info" id="comment-limit-${itemRatingDto.itemId}" close="false">
							<fmt:message key="${minNumberWordsLabel}">
								: <fmt:param value="${itemRatingDto.commentsMinWordsLimit}"/>
							</fmt:message>
						</lams:Alert5>
					</c:if>
				
					<div class="d-flex align-items-center">
						<div class="flex-grow-1">
							<textarea name="comment" rows="2" id="comment-textarea-${itemRatingDto.itemId}" class="form-control comment-textarea"
									placeholder="<fmt:message key="label.rating.textarea.tip"/>"
									aria-label="<fmt:message key="label.rating.textarea.tip"/>"></textarea>
						</div>
						<div>
							<button class="btn btn-secondary btn-sm add-comment add-comment-new ms-2"
									data-item-id="${itemRatingDto.itemId}"
									data-comment-criteria-id="${itemRatingDto.commentsCriteriaId}"
									data-show-all-comments="${showAllComments}"
									data-refresh-on-submit="${refreshOnComment}"
									aria-label="<fmt:message key="${postCommentButtonLabel}"/>">
								<i class="fa-solid fa-paper-plane"></i>
							</button>
						</div>
					</div>
				</div>			
			</c:when>
		</c:choose>
	</div>	
</c:if>

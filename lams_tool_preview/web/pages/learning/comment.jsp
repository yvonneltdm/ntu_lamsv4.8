<c:set var="maxRates" value="${rateAllUsers > 0 ? rateAllUsers : criteriaRatings.ratingCriteria.maximumRates}"/>
<c:set var="minRates" value="${rateAllUsers > 0 ? rateAllUsers : criteriaRatings.ratingCriteria.minimumRates}"/>

<!-- ********************  CSS ********************** -->
	<link href="${lams}css/rating.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="${lams}css/jquery.tablesorter.pager5.css">
	<link rel="stylesheet" href="${lams}css/jquery.tablesorter.theme.bootstrap5.css">
	<link rel="stylesheet" href="<lams:WebAppURL/>/includes/css/learning.css'/>">

<!-- ********************  javascript ********************** -->
	<script type="text/javascript">
		//vars for rating.js
		var MAX_RATES = 0, // only applies to stars
		MIN_RATES = 0, // only applies to stars
		MAX_RATINGS_FOR_ITEM = 0, // only applies to stars
		COUNT_RATED_ITEMS = 0, // only applies to stars
		COMMENTS_MIN_WORDS_LIMIT = ${criteriaRatings.ratingCriteria.commentsMinWordsLimit},
		COMMENT_TEXTAREA_TIP_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.comment.textarea.tip"/></spring:escapeBody>',
		WARN_COMMENTS_IS_BLANK_LABEL = '<spring:escapeBody javaScriptEscape="true"><fmt:message key="warning.comment.blank"/></spring:escapeBody>',
		WARN_MIN_NUMBER_WORDS_LABEL = "<spring:escapeBody javaScriptEscape='true'><fmt:message key='warning.minimum.number.words'><fmt:param value='${criteriaRatings.ratingCriteria.commentsMinWordsLimit}'/></fmt:message></spring:escapeBody>",
		SESSION_ID = ${toolSessionId}; 

		var	commentsSaved = true,
			commentsLocked = false;
			commentsOnOtherPages = ${countRatedItems};
			numCommentsOnPage = 0;
	</script>
	<script src="${lams}includes/javascript/jquery.tablesorter.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/jquery.tablesorter-widgets.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/jquery.tablesorter-pager.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/rating5.js" type="text/javascript" ></script> 	
	<script src="${lams}includes/javascript/portrait5.js" type="text/javascript" ></script>
	<script type="text/javascript">
	$(document).ready(function(){

		$(".tablesorter").tablesorter({
			theme: 'bootstrap',
		    widthFixed: true,
		    sortInitialOrder: 'desc',
		    headerTemplate : '{content} {icon}',
		    widgets: ['uitheme', 'zebra'],
	        headers: { 
	            1: { 
	                sorter: false 
	            }, 
	            2: {
	                sorter: false 
	            } 
	        }
		});
		
		$(".tablesorter").each(function() {
			$(this).tablesorterPager({
				savePages: false,
			    container: $(this).find(".ts-pager"),
			    output: '{startRow} to {endRow} ({totalRows})',
				cssPageDisplay: '.pagedisplay',
				cssPageSize: '.pagesize',
				cssDisabled: 'disabled',
				customAjaxUrl: function(table, url) {
					if ( commentsSaved ) {
						return url;
					} else { 
						<!-- Save comments first - this will retrigger the page call. Have to wait for submitEntrys ajax call to complete or end up not showing the comments when the pagesize is changed -->
						submitEntry(null, true);
						return "";
					}
				}, 
				ajaxUrl : "<c:url value='/learning/getUsers.do'/>?page={page}&size={size}&{sortList:column}&sessionMapID=${sessionMapID}&toolContentId=${peerreview.contentId}&toolSessionId=${toolSessionId}&criteriaId=${criteriaRatings.ratingCriteria.ratingCriteriaId}&userId=<lams:user property='userID' />",
				ajaxProcessing: function (data) {
			    	if (data && data.hasOwnProperty('rows')) {
			    		var rows = [],
			            json = {};
			    		
			    		if (data.rows.length == 0) {
			    			$(".tablesorter,.pager").hide();
			    			$("#no-users-info").show();
			    		}
						
						numCommentsOnPage = 0;
			    		commentsOnOtherPages = data.countRatedItems;
						var isDisabled = "${finishedLock}";
						var maxReached = false;
						<c:if test='${maxRates ne 0}'>
						if ( ! isDisabled ) {
							maxReached = ( ${maxRates} > 0 && commentsOnOtherPages >= ${maxRates} );
						}
						</c:if>

						for (i = 0; i < data.rows.length; i++){
							var userData = data.rows[i];
							var itemId = userData["itemId"];
							
							rows += '<tr>';

							rows += '<td class="username" width="20%"><div class="float-start me-2">';
							rows += definePortrait( userData["itemDescription2"], itemId, 'small', true, '${lams}' );
							rows += '</div><span class="portrait-sm-lineheight" id="username-' + itemId + '">';
							rows += userData["itemDescription"];
							rows += '</span>'
							rows += '</td>';
							
							rows += '<td class="comment" id="comments-area-' + itemId + '" width="80%">';
								
							var commentPostedByUser = userData["comment"];
							
							if ( isDisabled ) {
								rows += '<div class="rating-comment">';
								rows += 	commentPostedByUser;
								rows += '</div>';
										
							} else {
								rows += '<div id="add-comment-area-' + itemId + '">';	
								rows += '<div class="g-0">';
								rows += '';
								rows += '<div class="col-12 col-sm-11 ">';										
								rows +=		'<textarea name="comment-textarea-'+itemId+'" rows="4" id="comment-textarea-'+ itemId + '" class="form-control" aria-labelledby="theader-comment username-' + itemId + '"';
								<c:if test="${minRates ne 0 || maxRates ne 0}">
									rows += ' onkeyup="return updatedComment(this);"';
									if ( maxReached && commentPostedByUser == '' ) {
										rows += ' style="display:none"';
									}
								</c:if>
									rows += '>';
								if ( commentPostedByUser != '' ) {
									commentPostedByUser = commentPostedByUser.replace(/<BR>/gi, '\n');
									rows += commentPostedByUser;
									commentsOnOtherPages--;
									numCommentsOnPage++;
								} 
								rows += '</textarea>';
								rows += '</div>';
								rows += '';
								rows += '</div>';											
							}
								
							rows += '</td>';
							rows += '</tr>';
						}
			            
						json.total = data.total_rows;
						json.rows = $(rows);
						return json;
			    	}
				}
			})
			
			// bind to pager events
			.bind('pagerInitialized pagerComplete', function(event, options){
				commentsSaved = false;
				initializeStarability();
				checkButtons();
			});

		});
	 });
	
	function checkButtons() {
		<c:choose>
		<c:when test="${minRates ne 0 || maxRates ne 0}">
			if ( numCommentsOnPage + commentsOnOtherPages < ${minRates} ) {
				hideButtons();
			} else {
				showButtons();
			}
		</c:when>
		<c:otherwise>
			showButtons();
		</c:otherwise>
		</c:choose>
	}

	function updateCommentCount() {
		var newNumCommentsOnPage = 0;
		// save the modified values
		$('textarea').each(function() {
			if ( this.value != '' )
				newNumCommentsOnPage++;
		});
		numCommentsOnPage = newNumCommentsOnPage;
	}
	
	<c:if test="${minRates ne 0 || maxRates ne 0}">
	function updatedComment(comment) {		
		// if the data has been saved to the database, don't clear it!
		if ( comment.value == '' && comment.defaultValue != '' ) {
			alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.edit.not.remove"/></spring:escapeBody>');
			comment.value = comment.defaultValue;
			return;
		}
		
		updateCommentCount();
		if ( ${maxRates} > 0 ) {
			if ( (numCommentsOnPage + commentsOnOtherPages) >= ${maxRates} ) {
				commentsLocked = true;
				$('textarea').each(function() {
					if ( this.value == '' ) {
						this.style.display = 'none';
					}
				});
			} else if ( commentsLocked ){
				commentsLocked = false;
				$('textarea').each(function() {
					this.style.display='block';
				});
			}
		}
		$('#countRatedItemsSpan').html('<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.rate.limits.topic.reminder"/></spring:escapeBody>'.replace('{0}', numCommentsOnPage + commentsOnOtherPages));
		checkButtons();
		return true;
	}
	</c:if>

	function submitEntry(next, skipNumberValidation){	
		hideButtons();
		if (!skipNumberValidation && (numCommentsOnPage + commentsOnOtherPages < ${minRates})) {
			alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="label.rate.limits.reminder.min"/></spring:escapeBody>'.replace('{0}',${minRates}) );
			return false;
		}
			
		var validationFailed = false, 
			commentsToSave = 0,
			data = {
				sessionMapID: '${sessionMapID}', 
				toolContentId: '${peerreview.contentId}',
				criteriaId: '${criteriaRatings.ratingCriteria.ratingCriteriaId}'
			};
		
		// save the modified values
		$('textarea').each(function() {
			var comment = validComment(this.id, true, true);
			if ( ! ( typeof comment === "undefined" )  ) {
				if (comment!=this.defaultValue) {
					data[this.id] = comment;
					commentsToSave++;
				}
			} else  {
				validationFailed = true;
				return false; // validation failed! abort!
			}
		});
		if ( validationFailed ) {
			checkButtons();
			return false;
		}

		if ( commentsToSave > 0 ) {
			$.ajax({ 
				data: data, 
		        type: 'POST', 
	 	        url: '<c:url value="/learning/submitCommentsAjax.do?"/>', 
		        success: function (response) {
	    			var countCommentsSaved = response.countCommentsSaved;
					if ( ! ( countCommentsSaved > 0 ) ) {
	       				alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.unable.save.comments"/></spring:escapeBody>');
	       				showButtons();
	       				return false;
					} else {
						return moveOn(next);
					}
				}
			});
		} else {
			return moveOn(next);
		}
		
		return false;
	}
	
	function moveOn(next) {
		commentsSaved = true;
		if ( ! ( typeof next === "undefined" || next == null ) ) {
			return nextprev(next);
		} else {
			$(".tablesorter").trigger('pagerUpdate');
			showButtons();
			return true;
		}
	}
    </script>

	<!-- Rating limits info -->
	<c:if test="${minRates ne 0 || maxRates ne 0}">	
		<lams:Alert5 type="info" id="rate-limits-reminder" close="false">
				<c:choose>
					<c:when test="${rateAllUsers > 0}">
						<fmt:message key="label.rate.all.users"></fmt:message>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${criteriaRatings.ratingCriteria.minimumRates ne 0 and criteriaRatings.ratingCriteria.maximumRates ne 0}">
								<fmt:message key="label.rate.limits.reminder">
									<fmt:param value="${criteriaRatings.ratingCriteria.minimumRates}"/>
									<fmt:param value="${criteriaRatings.ratingCriteria.maximumRates}"/>
								</fmt:message>
							</c:when>
							<c:when test="${criteriaRatings.ratingCriteria.minimumRates ne 0 and criteriaRatings.ratingCriteria.maximumRates eq 0}">
								<fmt:message key="label.rate.limits.reminder.min">
									<fmt:param value="${criteriaRatings.ratingCriteria.minimumRates}"/>
								</fmt:message>
							</c:when>
							<c:when test="${criteriaRatings.ratingCriteria.minimumRates eq 0 and criteriaRatings.ratingCriteria.maximumRates ne 0}">
								<fmt:message key="label.rate.limits.reminder.max">
									<fmt:param value="${criteriaRatings.ratingCriteria.maximumRates}"/>
								</fmt:message>
							</c:when>
						</c:choose>
					</c:otherwise>
				</c:choose>

				<BR/>
				<span id="countRatedItemsSpan">
					<fmt:message key="label.rate.limits.topic.reminder">
						<fmt:param value="<span id='count-rated-items'>${countRatedItems}</span>"/>
					</fmt:message>
				</span>
		</lams:Alert5>
	</c:if>
	
<div class="card lcard">
	<div class="card-header">
		<c:out value="${criteriaRatings.ratingCriteria.title}" escapeXml="true" />
	</div>

	<form action="<c:url value="/learning/submitComments.do?"/>" method="get" id="editForm">
		<input type="hidden" name="sessionMapID" value="${sessionMapID}"/>
		<input type="hidden" name="toolContentId" value="${peerreview.contentId}"/>
		<input type="hidden" name="criteriaId" value="${criteriaRatings.ratingCriteria.ratingCriteriaId}"/>
		<input type="hidden" name="next" id="next" value=""/>		

		<lams:TSTable5 numColumns="2">
			<th class="username" title="<fmt:message key='label.sort.by.user.name'/>" style="width:25%" > 
				<fmt:message key="label.user.name" />
			</th>
			<th class="comment" id="theader-comment"> 
				<fmt:message key="label.comment" />
			</th>
		</lams:TSTable5>
	</form>
								
	<div id="no-users-info" class="alert alert-warning">
		<fmt:message key="label.no.users" />
	</div>
</div>
<%@ page import="org.lamsfoundation.lams.comments.CommentConstants"%>
<c:set var="maxThreadUid" value="0"/>
<c:set var="minThreadLike" value="-1"/>
<c:set var="messageTablename" value=""/>
<c:set var="indent" value="20"/>
<c:set var="show"><spring:escapeBody javaScriptEscape="true"><fmt:message key="label.show" /></spring:escapeBody></c:set>
<c:set var="hide"><spring:escapeBody javaScriptEscape="true"><fmt:message key="label.hide" /></spring:escapeBody></c:set>
<c:set var="prompt"><spring:escapeBody javaScriptEscape="true"><fmt:message key="label.showhide.prompt" /></spring:escapeBody></c:set>
<c:set var="tableCommand">expandable:true,initialState:'expanded',
	expanderTemplate:'<button type="button" class="btn btn-sm btn-light text-nowrap my-2 py-0 px-1 ms-2"><span style="margin-left:20px">${prompt}</span></button>',
	stringCollapse:'${hide}',
	stringExpand:'${show}',
	clickableNodeNames:false,
	indent:${indent},
	onNodeInitialized:function() {
		if (this.level() >= 2) {
			this.collapse();
		}
	}
</c:set>
<c:set var="tableCommandSticky">expandable:true,initialState:'collapsed',
	expanderTemplate:'<button type="button" class="btn btn-sm btn-light text-nowrap my-2 py-0 px-1 ms-2"><span style="margin-left:20px">${prompt}</span></a>',
	stringCollapse:'${hide}',
	stringExpand:'${show}',
	clickableNodeNames:false,
	indent:${indent},
	onNodeInitialized:function() {
		if (this.level() >= 2) {
			this.collapse();
		}
	} 
</c:set>

<script type="text/javascript">
	// The treetable code uses the clicks to expand and collapse the replies but then 
	// the buttons will not work. So stop the event propogating up the event chain. 
	$(".button").click(function (e) {
    	e.stopPropagation();
	});
	$(".comment").click(function (e) {
		e.stopPropagation();
	});
	
	function createReply(commentUid, url) {
		if ( document.getElementById('reply') ) {
			alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="message.complete.or.cancel.reply"/></spring:escapeBody>');
		} else {
			// set up the new reply area
			var replyDiv = document.createElement("div");
			replyDiv.id = 'reply';
			replyDiv.className = 'pt-5 clearfix';

			var parentDiv = document.getElementById('pb-msg'+commentUid);
			parentDiv.appendChild(replyDiv);
			
			$.ajaxSetup({ cache: true });
			$(replyDiv).load(url);
		}
	}
	
	function createEdit(commentUid, url) {
		if ( document.getElementById('edit') ) {
			alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="message.complete.or.cancel.edit"/></spring:escapeBody>');
		} else {
			// set up the new edit area
			// set up the new reply area
			var editDiv = document.createElement("div");
			editDiv.id = 'edit';
			editDiv.className = 'pt-5 clearfix';

			var parentDiv = document.getElementById('pb-msg'+commentUid);
			parentDiv.appendChild(editDiv);

			$.ajaxSetup({ cache: true });
			$(editDiv).load(url);
		}
	}

	function hideEntry(commentUid, url) {
		$.ajax({ // create an AJAX call...
		    type: 'GET', 
		    url: url
		})
	    .done(function (response) {
			reloadThread(response, '<lams:LAMSURL />','<fmt:message key="error.cannot.redisplay.please.refresh"/>','<fmt:message key="error.please.refresh"/>');
	    });
	}

	function makeSticky(commentUid, url) {
		$.ajax({ // create an AJAX call...
		    type: 'GET', 
		    url: url
		})
	    .done(function (response) {
	    	refreshComments
	    	
			reloadThread(response, '<lams:LAMSURL />','<fmt:message key="error.cannot.redisplay.please.refresh"/>','<fmt:message key="error.please.refresh"/>');
	    });
	}

	function updateLike(commentUid, url, incValue) {
		$.ajax({ // create an AJAX call...
		    type: 'POST', 
		    url: url
		})
	    .done(function (response) {
    		var serverCommentUid = response.commentUid;
       		
    		if ( ! commentUid || serverCommentUid != commentUid ) {
				alert('<spring:escapeBody javaScriptEscape="true"><fmt:message key="error.cannot.redisplay.please.refresh"/></spring:escapeBody>');
  			} else if ( response.status ) {
  				var currentCount = $('#msglikeCount'+commentUid).html();
  				currentCount = +currentCount + incValue;
	       		$('#msglikeCount'+commentUid).html(currentCount);
			}
		});
	}
		
	function likeEntry(commentUid) {
		updateLike(
			commentUid,
			'<lams:LAMSURL />comments/like.do?sessionMapID=${sessionMapID}&commentUid='+commentUid,
			1
		);		
		$( '#msglikebutton'+commentUid ).removeClass( 'fa-regular fa-faded' ).addClass( 'fa-solid' );
		$( '#msglikebutton'+commentUid ).prop( 'onclick', null );
		
		<c:if test='${sessionMap.likeAndDislike}'> 
			$( '#msgdislikebutton'+commentUid ).css( "display", "none" );
		</c:if>
	}

	<c:if test='${sessionMap.likeAndDislike}'> 
		function dislikeEntry(commentUid) {
			updateLike(
				commentUid, 
				'<lams:LAMSURL />comments/dislike.do?sessionMapID=${sessionMapID}&commentUid='+commentUid,
				-1
			);		
			$( '#msgdislikebutton'+commentUid ).removeClass( 'fa-regular fa-faded' ).addClass( 'fa-solid' );
			$( '#msgdislikebutton'+commentUid ).prop( 'onclick', null );
			$( '#msglikebutton'+commentUid ).css( "display", "none" );
		}
	</c:if>


    jQuery(document).ready(function() {
		jQuery("time.timeago").timeago();
    });
</script>

<c:set var="isSticky" value="false"/>
<c:forEach var="commentDto" items="${commentThread}">
	<c:set var="msgLevel" value="${commentDto.level}" />
	<c:set var="hidden" value="${commentDto.comment.hideFlag}" />
	
	<c:if test='${(msgLevel <= 1)}'>
		<c:set var="maxThreadUid" value="${commentDto.comment.uid}"/>
		<c:if test="${minThreadLike == -1 || minThreadLike > commentDto.comment.likeCount}">
			<c:set var="minThreadLike" value="${commentDto.comment.likeCount}"/>
		</c:if>
	</c:if>

	<c:set var="isSticky" value="${commentDto.comment.sticky}"/>
	<c:set var="isMonitor" value="${commentDto.comment.monitor}"/>
	<c:choose>
	<c:when test='${(msgLevel == 1)}'>
		<%-- same test & command appears at bottom of script --%>
		<c:if test='${messageTablename != ""}'>
			</table>
			<script> 
				$("#${messageTablename}").treetable({${isSticky?tableCommandSticky:tableCommand}});
 			</script>	
			</div>
		</c:if>
		<c:set var="messageTablename" value="tree${commentDto.comment.uid}"/>
		<div id="thread${commentDto.comment.uid}" class="clearfix">
		<table id="${messageTablename}" class="col-12">
		<tr data-tt-id="${commentDto.comment.uid}"><td>	
	</c:when>
	<c:otherwise>
		<tr data-tt-id="${commentDto.comment.uid}" data-tt-parent-id="${commentDto.comment.parent.uid}"><td>	
	</c:otherwise>
	</c:choose>
	
	<%@ include file="msgview.jsp"%>
	
	<c:if test='${(msgLevel >= 1)}'>
		</td></tr>	
	</c:if>

</c:forEach>

<c:if test='${messageTablename != ""}'>
	</table>
	<script>
		$("#${messageTablename}").treetable({${isSticky?tableCommandSticky:tableCommand}});
	</script>	
	</div>
</c:if>

<c:if test='${maxThreadUid > 0 && ! noMorePages}'>
	<div class="text-center d-none">
		<c:set var="more">
			<lams:LAMSURL />/comments/viewTopic.do?pageLastId=${maxThreadUid}&likeCount=${minThreadLike}&pageSize=${sessionMap.pageSize}&sessionMapID=${sessionMapID}
		</c:set>
		<a href="<c:out value="${more}"/>" class="btn btn-sm btn-light">
			<fmt:message key="label.show.more.messages" />
		</a>
	</div>
	
	<div class="text-center">
		<button type="button" onclick="javascript:refreshComments();" class="btn btn-light btn-sm btn-icon-refresh"> 
			<fmt:message key="label.refresh"/>
		</button>
	</div>
</c:if>

					

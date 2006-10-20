<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ taglib uri="tags-tiles" prefix="tiles"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<html>
	<head>
		<title><fmt:message key="activity.title" /></title>
		<%@ include file="/common/header.jsp"%>
		<script type="text/javascript">
			var removeItemAttachmentUrl = "<html:rewrite page="/learning/deleteAttachment.do" />";
		</script>		
		<script type="text/javascript" src="${tool}includes/javascript/message.js"></script>
		<script type="text/javascript">
			function removeAtt(mapID){
				removeItemAttachmentUrl =  removeItemAttachmentUrl + "?sessionMapID="+ mapID;
				removeItemAttachment();
			}
		</script>		
		
	</head>
	<body class="stripes">
			<tiles:insert attribute="body" />
	</body>
</html>

<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>

<!DOCTYPE html>
<lams:html>
<lams:head>
	<title>Collection management</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<lams:css/>
	<link type="text/css" href="<lams:LAMSURL/>css/free.ui.jqgrid.min.css" rel="stylesheet">
	<style>
		#addCollectionDiv {
			margin-top: 10px;
			padding-top: 10px;
			border-top: black thin solid;
		}
		
		#addCollectionDiv input {
			width: 80%;
			margin-right: 10px;
			display: inline-block;
		}
		
		#addCollectionDiv button {
			float: right;
		}
		
		.ui-jqgrid-title {
			display: inline-block;
			width: 100%;
			height: 30px;
		}
		
		.edit-button {
			position: absolute;
			right: 50px;
		}
				
		.grid-question-count {
			margin-left: 10px;
		}
		
		.grid-collection-private {
			margin-left: 10px;
			color: red;
		}
	</style>
	
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery-ui.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/bootstrap.min.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/free.jquery.jqgrid.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			
			// create a grid for each collection
			$('.collection-grid').each(function(){
				var collectionGrid = $(this);
				
				collectionGrid.jqGrid({
					guiStyle: "bootstrap",
					iconSet: 'fontAwesome',
					// data comes from data-collection-* attributes of <table> tag which is a base for the grid
					caption: collectionGrid.data('collectionName'),
				    datatype: "xml",
				    url: "<lams:LAMSURL />qb/collection/getCollectionGridData.do?collectionUid=" + collectionGrid.data('collectionUid'),
				    height: "100%",
				    autowidth:true,
					shrinkToFit: true,
				    cellEdit: false,
				    cmTemplate: { title: false, search: false },
				    sortorder: "asc", 
				    sortname: "name", 
				    pager: true,
				    rowList:[10,20,30,40,50,100],
				    rowNum: 10,
				    colNames:[
				    	"ID",
				    	"Name",
				    	"Stats"
				    ],
				    colModel:[
				      {name:'id', index:'uid', sortable:true,  width: 10},
				      {name:'name', index:'name', sortable:true, search:true, autoencode:true},
				      // formatter gets just question uid and creates a button
				      {name:'stats', index:'stats', classes: "stats-cell", sortable:false, width: 10, align: "center", formatter: statsLinkFormatter}
				      ],
					beforeSelectRow: function(rowid, e) {
						// do not select rows at all
					    return false;
					},
				    loadError: function(xhr,st,err) {
				    	collectionGrid.clearGridData();
					   	alert("Error!");
				    }
				}).jqGrid('filterToolbar');
			});
		});
		
		// Creates a button to display question statistics
		function statsLinkFormatter(cellvalue){
			return "<i class='fa fa-bar-chart' onClick='javascript:window.open(\"<lams:LAMSURL/>qb/stats/show.do?qbQuestionUid=" + cellvalue 
					+ "\", \"_blank\")' title='Show stats'></i>";
		}
		
		// add a new collection
		function addCollection() {
			var name = $('#addCollectionDiv input').val().trim(),
				lower = name.toLowerCase();
			// check if a collection with same name already exists
			$('.collection-grid').each(function(){
				if ($(this).data('collectionName').trim().toLowerCase() == lower) {
					alert('Collection with such name already exists');
					name = null;
					return false;
				}
			});
			if (name) {
				$.ajax({
					'url'  : '<lams:LAMSURL />qb/collection/addCollection.do',
					'type' : 'POST',
					'dataType' : 'text',
					'data' : {
						'name' : name
					},
					'cache' : false
				}).done(function(){
					document.location.reload();
				});
			}
		}
	</script>
</lams:head>
<body class="stripes">
<lams:Page title="Collection management" type="admin">
	<c:forEach var="collection" items="${collections}">
		<div class="panel-body">
			<c:set var="collectionName">
				<c:out value="${collection.name}" />
				<span class="grid-question-count">(${questionCount[collection.uid]} questions)</span>
				<c:if test="${collection.personal}">
					<span class="grid-collection-private"><i class="fa fa-lock"></i> Private</span>
				</c:if>
				<button class="btn btn-primary btn-xs edit-button"
						onClick="javascript:document.location.href=`<lams:LAMSURL />qb/collection/showOne.do?collectionUid=${collection.uid}`">
					Edit
				</button>
			</c:set>
				
			<%-- jqGrid placeholder with some useful attributes --%>
			<table class="collection-grid" data-collection-uid="${collection.uid}"
			 	   data-collection-name='${collectionName}' >
			</table>

		</div>
	</c:forEach>
	<div id="addCollectionDiv">
		<input placeholder="Enter new collection name" class="form-control" />
		<button class="btn btn-primary" onClick="javascript:addCollection()">Add collection</button>
	</div>
</lams:Page>
</body>
</lams:html>
<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div id="crudNodeWrapper">
			
			<div id="crudNodeTop">
				<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" title="Save" onClick="$('#reportAddForm').submit()" />
				<g:link action="index">
					<img src="${resource(dir:'images',file:'close.png')}" alt="Close" title="Close" />
				</g:link>
				<img src="${resource(dir:'images',file:'minus-grey.png')}" alt="Delete" title="Delete Unavailable" />
			</div>
			
			<div id="crudNode" class="report">
				
				<div id="titlerow">
					<img src="${resource(dir:'images',file:'report-default-64.png')}" alt="Report Image" />
					New Report
				</div>
				
				<g:form name="reportAddForm" action="addReportSubmit">
					<div id="title">
						<div class="inputhead">Title:</div>
						<input id="titleInput" type="text" value="${report?.title}" name="title" />
					</div>
					<div id="comments">
						<div class="inputhead">Comment:</div>
						<textarea id="newcomment" name="newcomment"></textarea>
					</div>
				</g:form>
			</div>
		</div>
		<g:if test="${EDIT}">
			<div style="display:none">
				<div id="attributeTemplate">
					<g:render template="/shared/attributerow" model="[edit:true]" />
				</div>
			</div>
		</g:if>
	</body>
</html>
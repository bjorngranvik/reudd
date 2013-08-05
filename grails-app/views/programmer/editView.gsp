<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div id="crudNodeWrapper">
			
			<div id="crudNodeTop">
				<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" title="Save" onClick="$('#viewEditForm').submit()" />
				<g:link action="listViews">
					<img src="${resource(dir:'images',file:'close.png')}" alt="Close" title="Close" />
				</g:link>
				<g:link action="deleteView" params="[id:view.id]">
					<img class="imagelink" src="${resource(dir:'images',file:'minus.png')}" alt="Delete" title="Delete" onClick="return confirm('Delete View?')" />
				</g:link>
			</div>
			
			<div id="crudNode" class="view">
				
				<div id="titlerow">
					<img src="${resource(dir:'images',file:'view-default-64.png')}" alt="View Image" />
					View for ${view.type.name}
				</div>
				
				<g:form name="viewEditForm" action="editViewSubmit">
				
					<div id="coveredAttributes">
						<div class="inputhead">Covered Attributes:</div>
						<input id="titleInput" type="text" value="${view.coveredAttributes}" name="coveredAttributes" />
					</div>
				
					<input type="hidden" name="id" value="${view.id}">
					
					<div id="viewBody">
						<textarea name="body" id="bodyInput">${view.body}</textarea>
					</div>
				</g:form>
				
			</div>
		</div>
	</body>
</html>
<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div id="reportNodeTop" class="topButtons">
			<g:if test="${params.action == 'show'}">
				<g:link action="edit" params="[id:report.id]">
					<img class="imagelink" src="${resource(dir:'images',file:'edit.png')}" alt="Edit" />
				</g:link>
			</g:if>
			<g:elseif test="${params.action == 'edit'}">
				<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" onClick="$('#reportEditForm').submit()" />
				<g:link action="delete" params="[id:report?.id]">
					<img class="imagelink" src="${resource(dir:'images',file:'delete.png')}" alt="Delete" onClick="return confirm('Delete Report?')" />
				</g:link>
			</g:elseif>
			<g:else>
				<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" onClick="$('#reportEditForm').submit()" />
			</g:else>
		</div>
		<div class="reportNode">
			<g:form name="reportEditForm" action="update">
				<g:if test="${EDIT}">
					<g:hiddenField name="id" value="${report?.id}" />
				</g:if>
				<div id="title">
					<div class="inputhead">Title:</div>
					<g:if test="${EDIT || ADD}">
						<input id="titleInput" type="text" value="${report?.title}" name="title" />
					</g:if>
					<g:else>
						${report?.title}
					</g:else>
				</div>
				<g:if test="${params.mode!='user'}">
					<div id="body">
						<div class="inputhead">Body:</div>
						<g:if test="${EDIT || ADD}">
							<textarea id="bodyInput" name="body">${report?.body}</textarea>
						</g:if>
						<g:else>
							${report?.body}
						</g:else>
					</div>
				</g:if>
				<div id="comments">
					<div class="inputhead">Comment:</div>
					<div>
						${report?.comments}
					</div>
					<g:if test="${ADD || EDIT}">
						<div>
							<textarea id="newcomment" name="newcomment"></textarea>
						</div>
					</g:if>
				</div>
			</g:form>
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
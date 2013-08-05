<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div id="crudNodeWrapper">
			
			<div id="crudNodeTop">
				<g:if test="${params.action == 'showType'}">
					<g:link action="editType" params="[id:node.id]">
						<img class="imagelink" src="${resource(dir:'images',file:'edit.png')}" alt="Edit" title="Edit" />
					</g:link>
				</g:if>
				<g:else test="${params.action == 'editType'}">
					<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" title="Save" onClick="$('#nodeEditForm').submit()" />
				</g:else>
				<g:link action="listTypes">
					<img class="imagelink" src="${resource(dir:'images',file:'close.png')}" alt="Close" title="Close" />
				</g:link>
				<g:link action="deleteType" params="[id:node.id]">
					<img class="imagelink" src="${resource(dir:'images',file:'minus.png')}" alt="Delete" title="Delete" onClick="return confirm('Delete TypeNode?')" />
				</g:link>
			
			</div>
			
			<div id="crudNode">
				
				<div id="titlerow">
					<img src="${resource(dir:'images',file:'node-default-64.png')}" alt="Node Image" />
					${node.name}
				</div>
				
				<g:form name="nodeEditForm" action="updateType">
					<g:hiddenField name="id" value="${node.id}" />
					<div id="tags">
						<div class="inputhead">Name:</div>
						<g:if test="${EDIT}">
							<input id="name" type="text" value="${node.name}" name="name" />
						</g:if>
						<g:else>
							${node.name}
						</g:else>
					</div>
					<div id="settings">
						<div class="inputhead">Settings:</div>
						<g:if test="${EDIT}">
							<input id="settingsinput" type="text" value="${node.settings}" name="settings" />
						</g:if>
						<g:else>
							${node.settings}
						</g:else>
					
					</div>
					<div id="attributes">
						<div class="inputhead">Attributes:</div>
						<g:each var="item" in="${node?.attributes}">
							<g:render template="/shared/attributerow" model="[item:item,edit:EDIT]" />
						</g:each>
						<g:if test="${EDIT}">
							<div class="imageaddlink">
								<img class="imagelink" onClick="ReUDD.addAttributeRow();" src="${resource(dir:'images',file:'plus-small.png')}" alt="Plus" title="Add Row" />
							</div>
						</g:if>
					</div>
					<div id="typeComments">
						<div class="inputhead">Comments:</div>
						<g:each var="comment" in="${node?.comments}">
							<div class="typeComment">
								${comment}
							</div>
						</g:each>
						<script>
							$('#typeComments .typeComment:even').addClass('evenRow')
						</script>
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
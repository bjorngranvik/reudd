<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
		<g:javascript src="jquery.autocomplete.min.js" />
		<link rel="stylesheet" href="${resource(dir:'css',file:'jquery.autocomplete.css')}" />
    </head>
    <body>
		<div id="crudNodeWrapper">
			<div id="crudNodeTop">
				<g:if test="${params.action == 'showNode'}">
					<g:link action="editNode" params="[id:node.id]">
						<img class="imagelink" src="${resource(dir:'images',file:'edit.png')}" alt="Edit" title="Edit"/>
					</g:link>
				</g:if>
				<g:else test="${params.action == 'editNode'}">
					<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" title="Save" onClick="validateAndSubmitForm();" />
				</g:else>
				<g:link action="listNodes">
					<img src="${resource(dir:'images',file:'close.png')}" alt="Close" title="Close" />
				</g:link>
				<g:if test="${params.action != 'addNode'}">
					<g:link action="deleteNode" params="[id:node.id]">
						<img src="${resource(dir:'images',file:'minus.png')}" alt="Delete" title="Delete" onClick="return confirm('Delete node?')" />
					</g:link>
				</g:if>
				<g:else>
					<img src="${resource(dir:'images',file:'minus-grey.png')}" alt="Delete" title="Delete Unavailable" />
				</g:else>
			</div>
			<div id="crudNode">
			
				<script>
					function validateAndSubmitForm() {
						var errors = '';
						if ($('#tagnames')[0].value == '') {
							errors += "No type specified\n";
						}
					
						var submit = true
						if (errors != '') {
							errors += '\nSubmit anyway?';
							submit = confirm(errors)
						}
						if (submit) {
							$('#nodeEditForm').submit();
						}
					}
				</script>
				
				<div id="titlerow">
					<img src="${resource(dir:'images',file:'node-default-64.png')}" alt="Node Image" />
					${node?.id ? node : "New Node"}
				</div>
			
				<g:if test="${errorMessages}">
					<div id="errorMessages">
						<div class="title">
							<img src="${resource(dir:'images',file:'error.png')}" alt="Error" /> Errors found
						</div>
						<div class="list">
							<ul>
								<g:each var="error" in="${errorMessages}">
									<li>${error}</li>
								</g:each>
							</ul>
						</div>
					</div>
				</g:if>
			
				<g:form name="nodeEditForm" action="updateNode" onSubmit="return validateForm();">
					<g:if test="${EDIT}">
						<g:hiddenField name="id" value="${node.id}" />
					</g:if>
				
					<div id="tags">
						<div class="inputhead">
							Types: 
							<g:if test="${ADD || EDIT}">
								<img class="imagelink" onClick="ReUDD.fillInAttributes();" src="${resource(dir:'images',file:'fill-in-small.png')}" alt="Fill" title="Fill With Previously Used Attributes & Relationships" />
								<img class="imagelink" onClick="toggleTypeComment();" src="${resource(dir:'images',file:'speechbubble-small.png')}" alt="Fill" title="Add Comment to Type" />
							</g:if>
						</div>
						<g:if test="${ADD || EDIT}">
							<input id="tagnames" type="text" name="tagnames" <g:if test="${SHOW}">disabled</g:if>
								value="<g:each var="type" in="${node?.types}">${type.name}, </g:each>"
								onClick="ReUDD.showTypeAdmin();" onFocus="this.blur();" />
						</g:if>
						<g:else>
							<g:each var="type" in="${node.types}">${type.name}, </g:each>
						</g:else>
						<g:if test="${ADD || EDIT}">
							<g:render template="typeadmin" />
							<g:render template="typecomment" />
						</g:if>
					</div>
				
					<div id="attributes">
						<div class="inputhead">Attributes:</div>
						<g:each var="type" in="${node?.types}">
							<g:if test="${type.hasView()}">
								<div class="customView">
									<g:include controller="user" action="renderViewBody" 	
										params="[viewId:type.viewNode.id,nodeId:node.id,edit:(EDIT || ADD)]"/>
								</div>
							</g:if>
						</g:each>
						
						
						<g:each var="item" in="${node?.attributes}">
							<g:if test="${!node.hasViewThatCoversAttribute(item.key)}">
								<g:if test="${true in (node.types*.getRequiredAttributes()*.contains(item.key))}">
									<g:render template="/shared/attributerow" model="[item:item,edit:(EDIT || ADD),required:true]" />
								</g:if>
								<g:else>
									<g:render template="/shared/attributerow" model="[item:item,edit:(EDIT || ADD),required:false]" />
								</g:else>
							</g:if>
						</g:each>
						<g:if test="${node?.attributes?.size() == 0 && (ADD || EDIT) || !node}">
							<g:render template="/shared/attributerow" model="[edit:true]" />
						</g:if>
						<g:if test="${ADD || EDIT}">
							<div class="imageaddlink">
								<img class="imagelink" id="addAttributeRowLink" onClick="ReUDD.addAttributeRow();" src="${resource(dir:'images',file:'plus-small.png')}" alt="Plus" title="Add Row" />
							</div>
						</g:if>
						
					</div>
				
					<div id="relationships">
						<div class="inputhead">Relationships:</div>
						
						<g:each var="item" in="${node?.outRelationships}">
							<g:render template="relationshiprow" 
								model="[item:item,edit:EDIT,dataNodes:dataNodes,direction:'out',node:node]" />
						</g:each>
						
						
						<g:each var="item" in="${node?.inRelationships}">
							<g:render template="relationshiprow" 
								model="[item:item,edit:EDIT,dataNodes:dataNodes,direction:'in',node:node]" />
						</g:each>
						
						<g:if test="${node?.outRelationships?.size() == 0 && node?.inRelationships?.size() == 0 && (EDIT || ADD) || !node}">
							<g:render template="relationshiprow" 
								model="[edit:true,dataNodes:dataNodes,direction:'out',node:node]" />
						</g:if>
						
						<g:if test="${ADD || EDIT}">
							<script>
								$('.relationshiprow input.key').autocomplete('${createLink(controller:"user", action:"getUsedRelationshipNamesList")}');
							</script>
							<div class="imageaddlink">
								<img class="imagelink" id="addRelationshipRowLink" onClick="ReUDD.addRelationshipRow();" src="${resource(dir:'images',file:'plus-small.png')}" alt="Plus" title="Add Row" />
							</div>
						</g:if>
					</div>
				</g:form>
				
			</div>
		</div>
		<g:if test="${ADD || EDIT}">
			<div style="display:none;">
				<div id="attributeTemplate">
					<g:render template="/shared/attributerow" model="[edit:true]" />
				</div>
				<div id="relationshipTemplate">
					<g:render template="relationshiprow" model="[edit:true,dataNodes:dataNodes,direction:'out']" />
				</div>
				<div id="typeNodeRequiredAttributes">
					<g:each var="type" in="${typeNodes}">
						<g:each var="attr" in="${type.getRequiredAttributes()}">
							<g:if test="${!type.hasViewThatCoversAttribute(attr) || ADD}">
								<div class="requiredAttributesFor${type.name}">
									<g:render template="/shared/attributerow" model="[key:attr,edit:true,disabled:true,required:true]" />
								</div>
							</g:if>
						</g:each>
					</g:each>
				</div>
			</div>
		</g:if>
	</body>
</html>
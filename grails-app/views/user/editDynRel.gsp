<html>
	<head>
		<meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
	</head>
	<body>
		<div id="dynamicRelationshipTop" class="topButtons">
			<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" onClick="$('#dynamicRelationshipForm').submit()" />
		</div>
		
		<g:form name="dynamicRelationshipForm" action="updateDynRel">
			<input type="hidden" name="id" value="${dynamicRelationship.id}">
			<input type="hidden" name="nodeId" value="${params.nodeId}">
			<h3>${dynamicRelationship}</h3>
			<div id="attributes">
				<g:each var="item" in="${dynamicRelationship.attributes}">
					<g:render template="/shared/attributerow" model="[item:item,edit:EDIT]" />
					</div>
				</g:each>
				<g:if test="${dynamicRelationship?.attributes?.size() == 0 && EDIT}">
					<g:render template="/shared/attributerow" model="[edit:true]" />
				</g:if>
				<div class="imageaddlink">
					<img class="imagelink" onClick="ReUDD.addAttributeRow();" src="${resource(dir:'images',file:'plus-small.png')}" alt="Plus" />
				</div>
			</div>
		</g:form>
		
		<div style="display:none">
			<div id="attributeTemplate">
				<g:render template="/shared/attributerow" model="[edit:true]" />
			</div>
		</div>
	</body>
</html>
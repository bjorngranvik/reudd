<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
	
    <body>
		<div id="dataNodeList">
			<g:if test="${searchString}">
				<h1>Search Results For: ${searchString}</h1>
			</g:if>
			
			<g:if test="${dataNodes}">
				<g:if test="${!searchString}">
					<h1>
						Listing Nodes
						<g:if test="${params.type && params.type != '*'}">
							of type ${params.type.decodeURL()}
						</g:if>
					</h1>
				</g:if>
				<g:form name="dataNodeListForm" action="deleteManyNodes">
					<div class="topbuttons">
					<input type="submit" value="Delete Selected" onClick="return confirm('Delete selected nodes?')">
					</div>
					<input type="hidden" name="type" value="${params.type}">
					<table class="dataNodeTable greytable">
						<tr class="title">
							<td></td>
							<td>Node</td>
							<td>Types</td>
							<td>Attributes</td>
							<td>Relationships</td>
						</tr>
						<g:each var="dataNode" in="${dataNodes}">
							<tr>
								<td>
									<input type="checkbox" name="selectedIds" value="${dataNode.id}" />
								</td>
								<td>
									<g:link action="showNode" id="${dataNode.id}">
										${dataNode}
									</g:link>
								</td>
								<td>
									<g:each var="type" in="${dataNode.types}">
										${type.name}<br/>
									</g:each>
									<g:if test="${dataNode.types.size() == 0}">
										&nbsp;
									</g:if>
								</td>
								<td>
									<g:each var="attribute" in="${dataNode.attributes}">
										${attribute.key} : ${attribute.value}<br/>
									</g:each>
									<g:if test="${dataNode.attributes.size() == 0}">
										&nbsp;
									</g:if>
								</td>
								<td>
									<g:each var="relation" in="${dataNode.outRelationships}">
										${relation.name}
										<img src="${resource(dir:'images',file:'arrow-right-green-small.png')}" alt="->"/>
										<g:link action="showNode" id="${relation.endNode.id}">
											${relation.endNode}
										</g:link><br/>
									</g:each>
									<g:each var="relation" in="${dataNode.inRelationships}">
										${relation.name}
										<img src="${resource(dir:'images',file:'arrow-left-blue-small.png')}" alt="<-"/>
										<g:link action="showNode" id="${relation.startNode.id}">
											${relation.startNode}
										</g:link><br/>
									</g:each>
									<g:if test="${dataNode.outRelationships.size() == 0 && dataNode.inRelationships.size() == 0}">
										&nbsp;
									</g:if>
								</td>
							</tr>
						</g:each>
					</table>
				</g:form>
			</g:if>
		</div>
	</body>
</html>
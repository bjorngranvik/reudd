<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div id="dataNodeList">
			<g:if test="${allNodes.size() > 0}">
				<h1>Types</h1>
				<g:form name="typeNodeListForm" action="deleteManyTypes">
					<div class="topbuttons">
						<input type="submit" value="Delete Selected" onClick="return confirm('Delete selected nodes?')">
					</div>
					<table class="dataNodeTable greytable">
						<tr class="title">
							<td>&nbsp;</td>
							<td>Type</td>
							<td>Nodes</td>
							<td>View</td>
							<td>Edit</td>
							<td>Total Rel. in</td>
							<td>Avg. Rel. in</td>
							<td>Total Rel. out</td>
							<td>Avg. Rel. out</td>
							<td>View</td>
						</tr>
						<g:each var="node" in="${allNodes}">
							<tr>
								<td>
									<input type="checkbox" name="selectedIds" value="${node.id}" />
								</td>
								<td>
									<g:link action="showType" id="${node.id}">
										<g:if test="${node.name}">
											${node.name}
										</g:if>
										<g:else>
											Node-${node.id}
										</g:else>
									</g:link>
								</td>
								<td>
									${node.countDataNodes()}
								</td>
								<td>
									${node.getViewCount()}
								</td>
								<td>
									${node.getEditCount()}
								</td>
								<td>
									${node.calcTotalIncomingRelations()}
								</td>
								<td>
									${node.calcAverageIncomingRelations()}
								</td>
								<td>
									${node.calcTotalOutgoingRelations()}
								</td>
								<td>
									${node.calcAverageOutgoingRelations()}
								</td>
								<td class="center">
									<g:if test="${node.hasView()}">
										<g:link action="editView" params="[id:node.viewNode.id]">
											<img src="${resource(dir:'images',file:'circle-green-small.png')}" alt="Yes" title="Yes" />
										</g:link>
									</g:if>
									<g:else>
										<g:link action="addView" params="[typeId:node.id]">
											<img src="${resource(dir:'images',file:'circle-red-small.png')}" alt="No" title="No" />
										</g:link>
									</g:else>
								</td>
							</tr>
						</g:each>
					</table>
				</g:form>
			</g:if>
			<g:else>
				There are currently no data nodes.
			</g:else>
		</div>
	</body>
</html>
<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div id="viewList">
			<g:form name="typeNodeListForm" action="deleteManyViews">
				<h1>Views</h1>
				<div class="topbuttons">
					<input type="submit" value="Delete Selected" onClick="return confirm('Delete selected views?')">
				</div>
				<table class="greytable">
					<tr class="title">
						<td>&nbsp;</td>
						<td>Type</td>
						<td>Implemented</td>
					</tr>
					<g:each var="view" in="${viewNodes}">
						<tr>
							<td>
								<input type="checkbox" name="selectedIds" value="${view.id}" />
							</td>
							<td>
								<g:link action="editView" params="[id:view.id]">
									${view.type.name}
								</g:link>
							</td>
							<td class="center">
								<g:if test="${view.hasBody()}">
									<img src="${resource(dir:'images',file:'circle-green-small.png')}" alt="Yes" title="Yes" />
								</g:if>
								<g:else>
									<img src="${resource(dir:'images',file:'circle-red-small.png')}" alt="No" title="No" />
								</g:else>
							</td>
						</tr>
					</g:each>
				</table>
			</g:form>
		</div>
	</body>
</html>
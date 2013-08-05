<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div id="reportList">
			<g:form name="typeNodeListForm" action="deleteManyReports">
				<h1>Reports</h1>
				<div class="topbuttons">
					<input type="submit" value="Delete Selected" onClick="return confirm('Delete selected reports?')">
				</div>
				<table class="greytable">
					<tr class="title">
						<td>&nbsp;</td>
						<td>Title</td>
						<td>Implemented</td>
						<td>Comments</td>
					</tr>
					<g:each var="report" in="${reportNodes}">
						<tr>
							<td>
								<input type="checkbox" name="selectedIds" value="${report.id}" />
							</td>
							<td>
								<g:link action="editReport" params="[id:report.id]">
									${report.title}
								</g:link>
							</td>
							<td class="center">
								<g:if test="${report.hasBody()}">
									<img src="${resource(dir:'images',file:'circle-green-small.png')}" alt="Yes" title="Yes" />
								</g:if>
								<g:else>
									<img src="${resource(dir:'images',file:'circle-red-small.png')}" alt="No" title="No" />
								</g:else>
							</td>
							<td class="center">
								${report.comments.size()}
							</td>
						</tr>
					</g:each>
				</table>
			</g:form>
		</div>
	</body>
</html>
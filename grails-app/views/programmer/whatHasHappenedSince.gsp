<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div id="nodeRelationshipTypes">
			<h1>What's New?</h1>
			<g:form name="whatHasHappenedSinceForm" action="whatHasHappenedSince">
				<label for="dateInput">Date:</label>
				<input id="dateInput" type="text" name="date" value="${sinceDate}" />
				<label for="formatInput">Format:</label>
				<input id="formatInput" type="text" name="format" value="yyMMdd HH:mm:ss" />
				<input type="submit" value="Check it out!">
			</g:form>
			<div id="whatsnew">
				<table class="greytable">
					<tr class="title">
						<td>Item</td>
						<td>Kind</td>
						<td>Created</td>
						<td>Last Update</td>
					</tr>
					<g:each var="item" in="${itemList}">
						<tr>
							<td>${item.item}</td>
							<td>${item.type}</td>
							<td>${item.item.created.format('yyMMdd HH:mm:ss')}</td>
							<td>${item.item.lastUpdate.format('yyMMdd HH:mm:ss')}</td>
						</tr>
					</g:each>
				</table>
			</div>
		</div>
	</body>
</html>
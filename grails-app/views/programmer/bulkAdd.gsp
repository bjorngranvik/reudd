<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div class="bulkAdd">
			<h1>Add Bulk</h1>
			<g:form name="bulkAddForm" action="bulkAddSubmit" enctype="multipart/form-data" method="post">
				<div>
					<input type="file" name="file"/>
				</div>
				<div>
					<g:submitButton value="Add bulk!" name="addBulk" />
				</div>
			</g:form>
		</div>
	</body>
</html>
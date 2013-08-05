<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div id="crudNodeWrapper">
			
			<div id="crudNodeTop">
				<img class="imagelink" src="${resource(dir:'images',file:'check.png')}" alt="Save" title="Save" onClick="$('#reportEditForm').submit()" />
				<g:link action="listReports">
					<img src="${resource(dir:'images',file:'close.png')}" alt="Close" title="Close" />
				</g:link>
				<g:link action="deleteReport" params="[id:report.id]">
					<img class="imagelink" src="${resource(dir:'images',file:'minus.png')}" alt="Delete" title="Delete" onClick="return confirm('Delete Report?')" />
				</g:link>
			</div>
			
			<div id="crudNode" class="report">
				
				<div id="titlerow">
					<img src="${resource(dir:'images',file:'report-default-64.png')}" alt="Report Image" />
					${report.title}
				</div>
				
				<g:form name="reportEditForm" action="editReportSubmit">
					<input type="hidden" name="id" value="${report.id}">
					<div id="title">
						<div class="inputhead">Title:</div>
						<input id="titleInput" type="text" value="${report?.title}" name="title" />
					</div>
					
					<div>
						<div class="inputhead">Copy Template:</div>
						<select id="templateSelector">
							<g:each var="template" in="${templates}">
								<option value="template-id-${template.id}">${template}</option>
							</g:each>
						</select>
						<reudd:img name="check" alt="copy" class="imagelink" onClick="copyTemplateBody();"/>
						<script>
							function copyTemplateBody() {
								var templateId = $('#templateSelector')[0].value
								var templateBody = $('#'+templateId)[0].innerHTML
								$('#bodyInput')[0].value = templateBody.trim()
							}
						</script>
						<g:each var="template" in="${templates}">
							<div id="template-id-${template.id}" style="display:none;">
								${template.body}
							</div>
						</g:each>
					</div>
					
					<div id="reportBody">
						<div class="inputhead">Body:</div>
						<textarea name="body" id="bodyInput">${report.body}</textarea>
					</div>
					
					<div id="comments">
						<div class="inputhead">Comments:</div>
						
						<textarea name="newcomment" id="addnewcomment"></textarea>
						
						<table id="reportCommentsTable" cellspacing="0" cellborder="0">
							<g:each var="comment" in="${report.comments}">
								<tr class="comment">
									<td class="left">
										<div class="author">
											${comment.author}
										</div>
										<div class="date">
											<g:formatDate format="yyyy-MM-dd HH:mm:ss" date="${comment.date}" />
										</div>
									</td>
									<td class="right">
										<div class="text">
											${comment.comment}
										</div>
									</td>
								</tr>
							</g:each>
							<script>
								$('#reportCommentsTable .comment:even').addClass('evenRow')
							</script>
						</table>
					</div>
				</g:form>
			</div>
		</div>
	</body>
</html>
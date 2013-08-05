<html>
    <head>
        <meta name="layout" content="main"></meta>
		<g:javascript src="jquery-1.3.2.min.js" />
    </head>
    <body>
		<div class="report">
			<h1>${report.title}</h1>
			<div id="table">
				<g:if test="${success}">
					<g:if test="${report.hasBody()}">
						<table class="greytable">
							<g:each var="row" in="${reportRows}">
								<tr>
									<g:each var="col" in="${row}">
										<td>
											${col ? col : "&nbsp;"}
										</td>
									</g:each>
								</tr>
							</g:each>
						</table>
					</g:if>
					<g:else>
						<div id="errorMessages">
						This report haven't been implemented yet...
						</div>
					</g:else>
				</g:if>
				<g:else>
					<div id="errorMessages">
					There was an error generating the report...
					</div>
				</g:else>
			</div>
			<div id="comments">
				<h2>Comments</h2>
				<g:form name="addReportComment" action="addReportCommentSubmit">
					<input type="hidden" name="id" value="${report.id}">
					<textarea name="newcomment" id="addnewcomment"></textarea>
					<img class="imagelink" src="${resource(dir:'images',file:'plus.png')}" alt="Add" title="Add Comment" onClick="$('#addReportComment').submit()" />
				</g:form>
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
		</div>
	</body>
</html>
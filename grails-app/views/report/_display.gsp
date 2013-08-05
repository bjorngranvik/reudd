<div>
	<h3>${report.title}</h3>
	<div>
		<g:if test="${success}">
			<table class="greytable">
				<g:each var="row" in="${reportRows}">
					<tr>
						<g:each var="col" in="${row}">
							<td>
								${col}
							</td>
						</g:each>
					</tr>
				</g:each>
			</table>
		</g:if>
		<g:else>
			There was an error generating the report...
		</g:else>
	</div>
	<div>
		<g:each var="comment" in="${report.comments}">
			<div>
				${comment}
			</div>
		</g:each>
	</div>
</div>
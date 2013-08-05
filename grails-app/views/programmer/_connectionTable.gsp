<table>
	<g:each var="item" in="${itemMap}" status="row">
		<tr>
			<td class="percent">${item.value}%</td>
			<td class="image"><img src="${resource(dir:'images',file:'connection.png')}" alt="&lt;=&gt;" /></td>
			<td class="typename">
				<g:remoteLink action="showNodeConnections" params="[type:item.key.name,lvl:lvl,row:oldRow+row]" update="showConnections${oldRow+row}">${item.key.name}</g:remoteLink>
			</td>
		</tr>
		<tr>
			<td class="subcol" colspan="3">
				<div id="showConnections${oldRow}${row}"></div>
			</td>
		</tr>
	</g:each>
</table>

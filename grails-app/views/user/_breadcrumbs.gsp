: 
<g:if test="${params.controller=='user'}">
	<g:link action='index'>
		Home
	</g:link>
	<g:each var="crumb" status="crumbIndex" in="${session.breadcrumbs}">
		:
		<g:link action='showNode' params="[id:crumb.id,crumbIndex:crumbIndex]">
			${crumb}
		</g:link>
	</g:each>
</g:if>
<g:else>
	No crumbs for you!
</g:else>
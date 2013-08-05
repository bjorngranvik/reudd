<g:each in="${usedAttributes}">
	<g:render template="/shared/attributerow" model="[key:it,edit:true,disabled:false]" />
</g:each>
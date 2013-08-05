<g:each in="${usedOutRelationships}">
	<g:render template="relationshiprow" model="[edit:true,dataNodes:dataNodes,direction:'out',name:it]"/>
</g:each>
<g:each in="${usedInRelationships}">
	<g:render template="relationshiprow" model="[edit:true,dataNodes:dataNodes,direction:'in',name:it]"/>
</g:each>
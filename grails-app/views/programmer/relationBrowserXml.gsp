<?xml version="1.0" encoding="UTF-8"?>
<RelationViewerData>
	<Settings appTitle="Node Space" startID="${dataNodes[0].id}" defaultRadius="150" maxRadius="180">
		<RelationTypes>
			<DirectedRelation color="0xAAAAAA" lineSize="4" letterSymbol=""/>
		</RelationTypes>
		<NodeTypes>
			<Node/>
		</NodeTypes>
	</Settings>
	<Nodes>
		<g:each var="node" in="${dataNodes}">
			<Node id="${node.id}" name="${node}">
				<![CDATA[<g:each var="attribute" in="${node.attributes}">${attribute.key} : ${attribute.value}
</g:each>
				]]>
			</Node>
		</g:each>
	</Nodes>
	<Relations>
		<g:each var="node" in="${dataNodes}">
			<DirectedRelation fromID="REF" toID="${node.id}" labelText="dataNode" />
			<g:each var="relation" in="${node.outRelationships}">
				<DirectedRelation fromID="${node.id}" toID="${relation.endNode.id}" labelText="${relation.name}" />
			</g:each>
		</g:each>	
	</Relations>
</RelationViewerData>


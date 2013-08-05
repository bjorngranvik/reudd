<g:if test="${direction == 'out'}">
	<g:set var="keyName" value="outRelationshipNames" />
	<g:set var="valueName" value="outRelationshipTargets" />
	<g:set var="imageLink" value="${resource(dir:'images',file:'arrow-right-green-small.png')}" />
	<g:set var="alterImage" value="-&gt;" />
</g:if>
<g:elseif test="${direction == 'in'}">
	<g:set var="keyName" value="inRelationshipNames" />
	<g:set var="valueName" value="inRelationshipTargets" />
	<g:set var="imageLink" value="${resource(dir:'images',file:'arrow-left-blue-small.png')}" />
	<g:set var="alterImage" value="&lt;-" />
</g:elseif>
<div class="relationshiprow">
	<g:if test="${edit}">
		<div class="imageleft">
			<img class="imagelink" onClick="ReUDD.deleteRow(this);" src="${resource(dir:'images',file:'minus-small.png')}" alt="delete" title="Delete Row" />
		</div>
		<div class="key">
			<input class="key" type="text" value="${item?.name}${name}" name="${keyName}" onKeyDown="ReUDD.relationshipCtrlClick(event)" />
		</div>
		<div class="imagemiddle">
			<img onClick="ReUDD.toggleRelEditMenu(this);" class="imagelink" src="${imageLink}" alt="${alterImage}"/>
			<style>
				.relEditMenu {
					display:none;
					height:60px;
					width:102px;
					position:relative;
					top:0px;
					left:-45px;
					border:1px solid;
					margin-right:-102px;
					margin-bottom:-62px;
					background-color:#FFFFFF;
				}
				.relEditMenu ul {
					list-style:none;
				}
				.relEditMenu li {
					padding:3px 5px 3px 5px;
				}
				.relEditMenu li:hover {
					background-color:#EEEEEE;
					cursor:pointer;
				}
			</style>
			<div class="relEditMenu">
				<ul>
					<li onClick="ReUDD.setRelDirectionOut(this);">
						Outgoing
						<img src="${resource(dir:'images',file:'arrow-right-green-small.png')}" alt="--&gt;"/>
					</li>
					<li onClick="ReUDD.setRelDirectionIn(this);">
						Incoming
						<img src="${resource(dir:'images',file:'arrow-left-blue-small.png')}" alt="&lt;--"/>
					</li>
					<g:if test="${item}">
						<li onClick="ReUDD.editRelationshipsAttributes('${createLink(action:'editDynRel',params:[id:item?.id,nodeId:node?.id])}');">
							Edit Attributes
						</li>
					</g:if>
				</ul>
			</div>
		</div>
		<div class="value">
			<select class="value" name="${valueName}" onKeyDown="ReUDD.relationshipCtrlClick(event)">
				<option value="">Select Node</option>
				<g:each var="dataNode" in="${dataNodes}">
					<option <g:if test="${!name && dataNode.id == item?.getOtherNode(node)?.id}">selected="selected"</g:if> value="${dataNode.id}">${dataNode}</option>
				</g:each>
			</select>
		</div>
	</g:if>
	<g:else>
		<div class="key pad2px">
			${item?.name}
		</div>
		<div class="imagemiddle">
			<img src="${imageLink}" alt="${alterImage}"/>
		</div>
		<div class="value pad2px">
			<g:link action="showNode" id="${item.getOtherNode(node).id}">
				${item.getOtherNode(node)}
			</g:link>
		</div>
	</g:else>
	<div class="clear"></div>
</div>
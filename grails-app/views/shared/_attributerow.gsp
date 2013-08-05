<div class="attribute">
	<g:if test="${edit}">
		<div class="imageleft">
			<g:if test="${required}">
				<img src="${resource(dir:'images',file:'placeholder-small.png')}" />
			</g:if>
			<g:else>
				<img class="imagelink" onClick="ReUDD.deleteRow(this);" src="${resource(dir:'images',file:'minus-small.png')}" alt="delete" title="Delete Row" />
			</g:else>
			
		</div>
		<div class="key">
			<g:if test="${required}">
				${key}${item?.key}
			</g:if>
			<g:else>
				<input class="key" type="text" name="attributeKeys" onKeyDown="ReUDD.attributeCtrlClick(event)"
					value="${key}${item?.key}" <g:if test="${disabled}">disabled</g:if> />
			</g:else>
		</div>
		<div class="imagemiddle">
			<img src="${resource(dir:'images',file:'colon-small.png')}" alt=":"/>
		</div>
		<div class="value">
			<g:if test="${required}">
				<input class="value" type="text" name="attribute-${key}${item?.key}" 
					onKeyDown="ReUDD.attributeCtrlEnterClick()"
					value="${item?.value}" <g:if test="${disabled}">disabled</g:if> />
			</g:if>
			<g:else>
				<input class="value" type="text" name="attributeValues" onKeyDown="ReUDD.attributeCtrlClick(event)"
					value="${item?.value}" <g:if test="${disabled}">disabled</g:if> />
			</g:else>
		</div>
	</g:if>
	<g:else>
		<div class="key pad2px">
			${item?.key}
		</div>
		<div class="imagemiddle">
			<img src="${resource(dir:'images',file:'colon-small.png')}" alt=":"/>
		</div>
		<div class="value pad2px">
			${item?.value}
		</div>
	</g:else>
	<div class="clear"></div>
</div>
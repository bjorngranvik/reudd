<g:if test="${params.controller=='user'}">
	<div class="leftmenu">
		<div class="menutitlerow">
			<div class="title">
				Nodes
			</div>
			<div class="image">
				<g:link action="addNode">
					<img class="imagelink" src="${resource(dir:'images',file:'plus.png')}" alt="[Add]" title="Add New Node" />
				</g:link>
			</div>
			<div class="clear"></div>
		</div>
		<div class="list">
			<ul>
                <!-- todo:bg Menu structure a bit odd with import always present at top. Improve. -->
                <li><g:link action="importFile">Import</g:link></li>
				<g:if test="${menuItemsTypes && menuItemsTypes.size() != 0}">
                    <li><g:link action="dataModel">Data Model</g:link></li>
					<li>
						<g:link action="listNodes" params="[type:'*']">All Types</g:link>
					</li>
				</g:if>
				<g:each var="type" in="${menuItemsTypes}">
					<li>
						<g:link action="listNodes" params="[type:type.name.encodeAsURL()]">${type.name}</g:link>
					</li>
				</g:each>
			</ul>

		</div>
	</div>
	<g:if test="${menuItemsTypes && menuItemsTypes.size() != 0}">
		<div class="leftmenu topmargin20">
			<div class="menutitlerow">
				<div class="title">
					Reports
				</div>
				<div class="image">
					<g:link action="addReport">
						<img class="imagelink" src="${resource(dir:'images',file:'plus.png')}" alt="[Add]" title="Add New Report" />
					</g:link>
				</div>
				<div class="clear"></div>
			</div>
			<div class="list">
				<ul>
					<g:each var="report" in="${menuItemsReports}">
						<li><g:link action="displayReport" params="[id:report.id]">${report.title}</g:link></li>
					</g:each>
				</ul>
			</div>
		</div>
		<div class="leftmenu topmargin20">
			<div class="menutitlerow">
				<div class="title">
					Search
				</div>
				<div class="clear"></div>
			</div>
			<div class="list">
				<g:form name="search" action="search">
					<input type="text" id="searchString" name="searchString" value="${searchString}" />
				</g:form>
			</div>
		</div>
	</g:if>
</g:if>
<g:elseif test="${params.controller=='programmer'}">
	<div class="leftmenu">
		<div class="menutitlerow">
			<div class="title">
				Programmer
			</div>
			<div class="clear"></div>
		</div>
		<div class="list">
			<ul>
				<li><g:link action="whatHasHappenedSince">Whats New?</g:link></li>
				<li><g:link action="tagCloudTypes">Tag Cloud</g:link></li>
				<li><g:link action="listTypes">Show Types</g:link></li>
				<li><g:link action="listViews">Show Views</g:link></li>
				<li><g:link action="listReports">Show Reports</g:link></li>
				<li><g:link action="nodeConnections">Node Connections</g:link></li>
				<g:if test="${params.action=='nodeConnections'}">
					<ul>
						<g:each var="type" in="${typeNodes}">
							<li><g:link action="nodeConnections" params="[type:type.name]">${type.name}</g:link></li>
						</g:each>
					</ul>
				</g:if>
				<li><g:link action="navigatedPaths">Navigated Paths</g:link></li>
			</ul>
		</div>
	</div>
</g:elseif>
<g:else>
	<div class="leftmenu">
		<h3>No menu items</h3>
	</div>
</g:else>
<!--TODO: This style needs to be renamed-->
<div id="jayway-logo">
    Sponsored by <a href="http://diversify.se">
   		<img src="${resource(dir:'images',file:'diversify.png')}" alt="Diversify Logo" />
   	</a>
	Originally sponsored by <a href="http://jayway.com">
		<img src="${resource(dir:'images',file:'jayway.png')}" alt="Jayway Logo" />
	</a>
</div>

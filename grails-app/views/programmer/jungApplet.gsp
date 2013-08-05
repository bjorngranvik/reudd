<html>
    <head>
        <meta name="layout" content="main"></meta>
    </head>
    <body>
		<div class="jungApplet">
			<applet code="org.reudd.applets.JungApplet" archive="file:///Users/sergeanten/lth/exjobb/workspace/ReUDD/web-app/jars/jungApplet.jar", width=1000, height=500 >
				<param name="location" value="${createLink(controller:'analyze', action:'jungAppletJson')}">
			</applet>
		</div>
	</body>
</html>
<html>
<head>
    <g:javascript library="d3"/>
    <r:require modules="d3"/>
    <r:layoutResources/>
    <g:javascript src="graph-renderer.js"/>
    <meta name="layout" content="main"></meta>
</head>

<body>
<div class="nodeConnections">
    <h1>Node Connections</h1>

    <div class="center">
        <script language="JavaScript">
            renderGraph(${data});
        </script>

    </div>
</div>
</body>
</html>

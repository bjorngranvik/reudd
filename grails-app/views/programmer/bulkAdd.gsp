<html>
<head>
    <meta name="layout" content="main"></meta>
</head>

<body>
<div class="bulkAdd">
    <h1>Add Bulk</h1>
    <g:form name="bulkAddForm" action="bulkAddSubmit" enctype="multipart/form-data" method="post">
        <div>
            <input type="file" name="file"/>
        </div>

        <div>
            <g:submitButton value="Add bulk!" name="addBulk"/>
        </div>
    </g:form>

    <p>
        <!-- //todo:BG I wanted to use UL and LI but those are dependent on the left menu. Clean up when we do revamp of UI. -->
        </br>
        <b>Examples</b></br>
        Ready to be imported:</br>
        - <a href="/example-import/example-import-data.csv">CSV</a>
    </p>

    <p>
    </br>
        Export as CSV using ";" as field delimiter and import above:</br>
        - <a href="/example-import/example-import-data.xls">Excel</a></br>
<!-- todo:bg Google spreadsheet only exports using comma as field delimiter when we need semicolon. Should user be able and specify delimiter on import? -->
<!--        - <a href="https://docs.google.com/spreadsheet/ccc?key=0AnV464Slx2z0dDF0UjZtMHlfQ25XZWUtVk9IUlBSU2c#gid=0">Google spreadsheet</a></br>-->
    </p>
</div>
</body>
</html>
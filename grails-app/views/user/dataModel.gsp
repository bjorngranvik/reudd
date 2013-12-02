<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Data Model</title>
    <style type="text/css">
    svg .type {
        fill: yellow;
        stroke: orange;
        stroke-width: 5;
    }

    .node {
        stroke: #black;
        stroke-width: 2px;
        font-family: serif;
        font-size: 12px;
        color: balck;
    }

    .link {
        stroke: #999;
        stroke-opacity: 1;
        font-family: serif;
        font-size: 12px;
        color: black;
    }

    </style>
    <g:javascript library="d3"/>
    <r:require modules="d3"/>
    <r:layoutResources/>
    <g:javascript library="prototype"/>
    <meta name="layout" content="main"/>
</head>

<body>
<div id="domainModel">
    <h1>Data Model</h1>

    <div class="center">
        <script>
            var data = ${data};

            var width = 780,
                    height = 600;

            var color = d3.scale.category20();

            var force = d3.layout.force()
                    .gravity(.03)
                    .distance(200)
                    .charge(-400)
                    .size([width, height]);

            var svg = d3.select("body").append("svg")
                    .attr("width", width)
                    .attr("height", height);


            force
                    .nodes(data.nodes)
                    .links(data.links)
                    .start();

            force.linkDistance(function () {
                width / force.nodes.length
            });

            var link = svg.selectAll(".link")
                    .data(data.links)
                    .enter().append("line")
                    .attr("class", "link")
                    .style("stroke-width", function (d) {
                        return Math.sqrt(d.value);
                    });

            var node = svg.selectAll(".node")
                    .data(data.nodes)
                    .enter().append("g")
                    .attr("class", "node")
                    .call(force.drag);

            node.append("circle")
                    .attr("r", 60)
                    .style("fill", function (d) {
                        return color(d.colourGroup);
                    });

            node.append("text")
                    .attr("dy", ".35em")
                    .attr("text-anchor", "middle")
                    .text(function (d) {
                        return d.name;
                    });

            node.append("title")
                    .text(function (d) {
                        return d.name;
                    });

            force.on("tick", function () {
                link.attr("x1", function (d) {
                    return d.source.x;
                })
                        .attr("y1", function (d) {
                            return d.source.y;
                        })
                        .attr("x2", function (d) {
                            return d.target.x;
                        })
                        .attr("y2", function (d) {
                            return d.target.y;
                        });

                node.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });
            force.drag().on("dragstart", dragstart);

            function dragstart(d) {
                d.fixed = true;
                d3.select(this).classed("fixed", true);
            }

        </script>
    </div>
</div>
</body>
</html>

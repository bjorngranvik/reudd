grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
grails.project.dependency.resolver = "ivy"

grails.project.dependency.resolution = {
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve true

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo name: "Neo4j", root: "http://m2.neo4j.org/releases"

    }


    dependencies {
        runtime 'org.neo4j:neo4j-community:1.9.5'
        compile "org.neo4j:neo4j-rest-graphdb:1.9"
        compile "com.google.guava:guava:15.0"
        test "org.mockito:mockito-core:1.9.5"
        test "org.hamcrest:hamcrest-all:1.3"

    }

    plugins {
        runtime ":neo4j:1.0.1"

        runtime ":jquery:1.8.3"
        runtime ":resources:1.2"

        compile ":d3:3.3.9.0"

        compile ":jetty:2.0.3"

    }
}

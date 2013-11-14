grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo "http://m2.neo4j.org/releases"
    }
    dependencies {
        compile 'net.sf.jung:jung-visualization:2.0.1'
        compile 'net.sf.jung:jung-graph-impl:2.0.1'
        runtime 'org.neo4j:neo4j-community:1.9.2'
        compile "org.neo4j:neo4j-rest-graphdb:1.9"

    }

    plugins {
        runtime ":neo4j:1.0.1"

        runtime ":jquery:1.8.3"
        runtime ":resources:1.2"


        //build ":tomcat:2.2.4"
        compile ":jetty:2.0.3"

    }
}

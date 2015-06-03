/*
 * Copyright (c) 2009-2013 Björn Granvik & Jonas Andersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.neo4j.graphdb.NotInTransactionException;

import java.io.File;

import org.neo4j.graphdb.Transaction;

import org.reudd.reports.ReportNodeTemplate;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Direction;
import org.reudd.reports.ReportNodeFactory;
import org.reudd.util.ReUddConstants;
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.GraphDatabaseService;

class BootStrap {

	GraphDatabaseService graphDatabaseService
	
	def init = { servletContext ->
		/*
		 *  Add getDynamicRelationship function to Node
		 */
		Node.metaClass.getDynamicRelationships = {
			def dynamicRelationships = []
			delegate.getRelationships().each {
				if (!it.getType().name().startsWith(ReUddConstants.PREFIX)) {
					dynamicRelationships += it
				}
			}
			dynamicRelationships
		}
		/*
		 *  Add getDynamicRelationship function to Node
		 */
		Node.metaClass.getDynamicRelationships = { Direction direction ->
			def dynamicRelationships = []
			delegate.getRelationships(direction).each {
				if (!it.getType().name().startsWith(ReUddConstants.PREFIX)) {
					dynamicRelationships += it
				}
			}
			dynamicRelationships
		}
		/*
		 *  Add getDynamicRelationship function to Node
		 */
		Node.metaClass.getDynamicRelationships = { String name, Direction direction ->
			def dynamicRelationships = []
			delegate.getRelationships(direction).each {
				if (it.getType().name().equals(name)) {
					dynamicRelationships += it
				}
			}
			dynamicRelationships
		}
		
		String.metaClass.isEmpty = {
			delegate == ""
		}
		
		String.metaClass.escapeSomeHtml = {
			delegate.replaceAll("å","&aring;").replaceAll("Å","&Aring;")
					.replaceAll("ä","&auml;").replaceAll("Ä","&Auml;")
					.replaceAll("ö","&ouml;").replaceAll("Ö","&Ouml;")
		}
		
		Transaction tx = graphDatabaseService.beginTx()
		try {
			ReportNodeFactory reportFactory = new ReportNodeFactory(graphDatabaseService)
			def reportTemplateFolder = new File("report-templates")
			def templateFiles = reportTemplateFolder.listFiles()
			templateFiles.each { File file ->
				if (file.isFile()) {
					def lines = file.readLines()
					def title = lines[0][2..-1].trim()
					def parameters = lines[1][2..-1].trim().split(",")*.trim()
					def body = lines[2..-1].join("\n")
					
					def reportTemplate = reportFactory.getOrCreateReportNodeTemplate(title)
					reportTemplate.title = title
					reportTemplate.parameters = parameters
					reportTemplate.body = body
					reportTemplate.save()
				}
			}
			tx.success()
		} finally {
			tx.finish()
		}
		
	}
	
	def destroy = {
		
	}
} 
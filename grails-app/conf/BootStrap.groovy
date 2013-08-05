/*
 * Copyright (c) 2009-2012 Björn Granvik & Jonas Andersson, http://reudd.org
 *
 * This file is part of ReUDD.
 *
 * ReUDD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Jonas Andersson, jonas@splanaden.se
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
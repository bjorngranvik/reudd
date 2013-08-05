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

package org.reudd.util;

import org.neo4j.graphdb.RelationshipType;

public enum ReUddRelationshipTypes implements RelationshipType {

	_REUDD_TYPE_NODES,
	_REUDD_TYPE_NODE,
	_REUDD_DATA_NODES,
	_REUDD_DATA_NODE,
	_REUDD_IS_OF_TYPE,
	_REUDD_STATISTICS_NODE,
	_REUDD_NODE_PATH,
	_REUDD_REPORT_NODES,
	_REUDD_REPORT_NODE,
	_REUDD_VIEW_NODES,
	_REUDD_VIEW_NODE,
	_REUDD_HAS_VIEW,
	_REUDD_REPORT_TEMPLATE

}

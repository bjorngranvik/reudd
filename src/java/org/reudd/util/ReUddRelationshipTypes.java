/*
 * Copyright (c) 2009-2015 Björn Granvik & Jonas Andersson
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

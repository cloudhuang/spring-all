/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.repository.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.query.PartTreeMongoQuery;
import org.springframework.data.mongodb.repository.query.QueryUtils;
import org.springframework.data.repository.core.support.QueryCreationListener;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

/**
 * {@link QueryCreationListener} inspecting {@link PartTreeMongoQuery}s and creating an index for the properties it
 * refers to.
 * 
 * @author Oliver Gierke
 */
class IndexEnsuringQueryCreationListener implements QueryCreationListener<PartTreeMongoQuery> {

	private static final Set<Type> GEOSPATIAL_TYPES = new HashSet<Type>(Arrays.asList(Type.NEAR, Type.WITHIN));
	private static final Log LOG = LogFactory.getLog(IndexEnsuringQueryCreationListener.class);

	private final MongoOperations operations;

	/**
	 * Creates a new {@link IndexEnsuringQueryCreationListener} using the given {@link MongoOperations}.
	 * 
	 * @param operations must not be {@literal null}.
	 */
	public IndexEnsuringQueryCreationListener(MongoOperations operations) {

		Assert.notNull(operations);
		this.operations = operations;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.QueryCreationListener#onCreation(org.springframework.data.repository.query.RepositoryQuery)
	 */
	public void onCreation(PartTreeMongoQuery query) {

		PartTree tree = query.getTree();
		Index index = new Index();
		index.named(query.getQueryMethod().getName());
		Sort sort = tree.getSort();

		for (Part part : tree.getParts()) {
			if (GEOSPATIAL_TYPES.contains(part.getType())) {
				return;
			}
			String property = part.getProperty().toDotPath();
			Order order = toOrder(sort, property);
			index.on(property, order);
		}

		// Add fixed sorting criteria to index
		if (sort != null) {
			for (Sort.Order order : sort) {
				index.on(order.getProperty(), QueryUtils.toOrder(order));
			}
		}

		MongoEntityInformation<?, ?> metadata = query.getQueryMethod().getEntityInformation();
		operations.indexOps(metadata.getCollectionName()).ensureIndex(index);
		LOG.debug(String.format("Created %s!", index));
	}

	private static Order toOrder(Sort sort, String property) {

		if (sort == null) {
			return Order.DESCENDING;
		}

		org.springframework.data.domain.Sort.Order order = sort.getOrderFor(property);
		return order == null ? Order.DESCENDING : order.isAscending() ? Order.ASCENDING : Order.DESCENDING;
	}
}
/*
 * Copyright 2010-2011 the original author or authors.
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
package org.springframework.data.mongodb.core.index;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Order;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Index implements IndexDefinition {

	public enum Duplicates {
		RETAIN, DROP
	}

	private final Map<String, Order> fieldSpec = new LinkedHashMap<String, Order>();

	private String name;

	private boolean unique = false;

	private boolean dropDuplicates = false;

	private boolean sparse = false;

	public Index() {
	}

	public Index(String key, Order order) {
		fieldSpec.put(key, order);
	}

	public Index on(String key, Order order) {
		fieldSpec.put(key, order);
		return this;
	}

	public Index named(String name) {
		this.name = name;
		return this;
	}

	public Index unique() {
		this.unique = true;
		return this;
	}

	public Index sparse() {
		this.sparse = true;
		return this;
	}

	public Index unique(Duplicates duplicates) {
		if (duplicates == Duplicates.DROP) {
			this.dropDuplicates = true;
		}
		return unique();
	}

	public DBObject getIndexKeys() {
		DBObject dbo = new BasicDBObject();
		for (String k : fieldSpec.keySet()) {
			dbo.put(k, (fieldSpec.get(k).equals(Order.ASCENDING) ? 1 : -1));
		}
		return dbo;
	}

	public DBObject getIndexOptions() {
		if (name == null && !unique) {
			return null;
		}
		DBObject dbo = new BasicDBObject();
		if (name != null) {
			dbo.put("name", name);
		}
		if (unique) {
			dbo.put("unique", true);
		}
		if (dropDuplicates) {
			dbo.put("dropDups", true);
		}
		if (sparse) {
			dbo.put("sparse", true);
		}
		return dbo;
	}

	@Override
	public String toString() {
		return String.format("Index: %s - Options: %s", getIndexKeys(), getIndexOptions());
	}
}

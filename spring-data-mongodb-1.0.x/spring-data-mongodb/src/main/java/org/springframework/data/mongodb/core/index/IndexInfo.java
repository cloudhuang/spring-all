/*
 * Copyright 2002-2011 the original author or authors.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Order;
import org.springframework.util.ObjectUtils;

public class IndexInfo {

	private final Map<String, Order> fieldSpec;
	private final List<IndexField> indexFields;

	private final String name;
	private final boolean unique;
	private final boolean dropDuplicates;
	private final boolean sparse;

	public IndexInfo(Map<String, Order> fieldSpec, List<IndexField> indexFields, String name, boolean unique,
			boolean dropDuplicates, boolean sparse) {

		this.fieldSpec = fieldSpec;
		this.indexFields = Collections.unmodifiableList(indexFields);
		this.name = name;
		this.unique = unique;
		this.dropDuplicates = dropDuplicates;
		this.sparse = sparse;
	}

	/**
	 * @deprecated use {@link #getIndexFields()} instead as this {@link Map} does not contain geo indexes.
	 * @return
	 */
	@Deprecated
	public Map<String, Order> getFieldSpec() {
		return fieldSpec;
	}

	/**
	 * Returns the individual index fields of the index.
	 * 
	 * @return
	 */
	public List<IndexField> getIndexFields() {
		return this.indexFields;
	}

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique;
	}

	public boolean isDropDuplicates() {
		return dropDuplicates;
	}

	public boolean isSparse() {
		return sparse;
	}

	@Override
	public String toString() {
		return "IndexInfo [indexFields=" + indexFields + ", name=" + name + ", unique=" + unique + ", dropDuplicates="
				+ dropDuplicates + ", sparse=" + sparse + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (dropDuplicates ? 1231 : 1237);
		result = prime * result + ObjectUtils.nullSafeHashCode(indexFields);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (sparse ? 1231 : 1237);
		result = prime * result + (unique ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IndexInfo other = (IndexInfo) obj;
		if (dropDuplicates != other.dropDuplicates) {
			return false;
		}
		if (indexFields == null) {
			if (other.indexFields != null) {
				return false;
			}
		} else if (!indexFields.equals(other.indexFields)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (sparse != other.sparse) {
			return false;
		}
		if (unique != other.unique) {
			return false;
		}
		return true;
	}
}

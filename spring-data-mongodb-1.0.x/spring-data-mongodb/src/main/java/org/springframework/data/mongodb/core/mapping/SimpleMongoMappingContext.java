/*
 * Copyright (c) 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.AbstractPersistentProperty;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.data.util.TypeInformation;

/**
 * 
 * @author Oliver Gierke
 */
public class SimpleMongoMappingContext extends
		AbstractMappingContext<SimpleMongoMappingContext.SimpleMongoPersistentEntity<?>, MongoPersistentProperty> {

	/* (non-Javadoc)
	 * @see org.springframework.data.mapping.BasicMappingContext#createPersistentEntity(org.springframework.data.util.TypeInformation)
	 */
	@Override
	protected <T> SimpleMongoPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
		return new SimpleMongoPersistentEntity<T>(typeInformation);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.mapping.BasicMappingContext#createPersistentProperty(java.lang.reflect.Field, java.beans.PropertyDescriptor, org.springframework.data.util.TypeInformation, org.springframework.data.mapping.BasicPersistentEntity)
	 */
	@Override
	protected SimplePersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor,
			SimpleMongoPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
		return new SimplePersistentProperty(field, descriptor, owner, simpleTypeHolder);
	}

	static class SimplePersistentProperty extends AbstractPersistentProperty<MongoPersistentProperty> implements
			MongoPersistentProperty {

		private static final List<String> ID_FIELD_NAMES = Arrays.asList("id", "_id");

		/**
		 * Creates a new {@link SimplePersistentProperty}.
		 * 
		 * @param field
		 * @param propertyDescriptor
		 * @param information
		 */
		public SimplePersistentProperty(Field field, PropertyDescriptor propertyDescriptor, MongoPersistentEntity<?> owner,
				SimpleTypeHolder simpleTypeHolder) {
			super(field, propertyDescriptor, owner, simpleTypeHolder);
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mapping.BasicPersistentProperty#isIdProperty()
		 */
		public boolean isIdProperty() {
			return ID_FIELD_NAMES.contains(field.getName());
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.core.mapping.MongoPersistentProperty#getKey()
		 */
		public String getFieldName() {
			return isIdProperty() ? "_id" : getName();
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.core.mapping.MongoPersistentProperty#getFieldOrder()
		 */
		public int getFieldOrder() {
			return Integer.MAX_VALUE;
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mapping.AbstractPersistentProperty#createAssociation()
		 */
		@Override
		protected Association<MongoPersistentProperty> createAssociation() {
			return new Association<MongoPersistentProperty>(this, null);
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.core.mapping.MongoPersistentProperty#isDbReference()
		 */
		public boolean isDbReference() {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.core.mapping.MongoPersistentProperty#getDBRef()
		 */
		public DBRef getDBRef() {
			return null;
		}
	}

	static class SimpleMongoPersistentEntity<T> extends BasicPersistentEntity<T, MongoPersistentProperty> implements
			MongoPersistentEntity<T> {

		/**
		 * @param information
		 */
		public SimpleMongoPersistentEntity(TypeInformation<T> information) {
			super(information);
		}

		/* (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.core.mapping.MongoPersistentEntity#getCollection()
		 */
		public String getCollection() {
			return MongoCollectionUtils.getPreferredCollectionName(getType());
		}
	}
}

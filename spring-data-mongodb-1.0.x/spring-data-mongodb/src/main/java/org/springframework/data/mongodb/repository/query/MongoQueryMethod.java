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
package org.springframework.data.mongodb.repository.query;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.geo.GeoPage;
import org.springframework.data.mongodb.core.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * TODO - Extract methods for {@link #getAnnotatedQuery()} into superclass as it is currently copied from Spring Data
 * JPA
 * 
 * @author Oliver Gierke
 */
public class MongoQueryMethod extends QueryMethod {

	@SuppressWarnings("unchecked")
	private static final List<Class<?>> GEO_NEAR_RESULTS = Arrays
			.asList(GeoResult.class, GeoResults.class, GeoPage.class);

	private final Method method;
	private final MongoEntityInformation<?, ?> entityInformation;

	/**
	 * Creates a new {@link MongoQueryMethod} from the given {@link Method}.
	 * 
	 * @param method
	 */
	public MongoQueryMethod(Method method, RepositoryMetadata metadata, EntityInformationCreator entityInformationCreator) {
		super(method, metadata);
		Assert.notNull(entityInformationCreator, "DefaultEntityInformationCreator must not be null!");
		this.method = method;
		this.entityInformation = entityInformationCreator.getEntityInformation(metadata.getReturnedDomainClass(method),
				getDomainClass());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryMethod#getParameters(java.lang.reflect.Method)
	 */
	@Override
	protected Parameters createParameters(Method method) {
		return new MongoParameters(method, isGeoNearQuery(method));
	}

	/**
	 * Returns whether the method has an annotated query.
	 * 
	 * @return
	 */
	public boolean hasAnnotatedQuery() {
		return getAnnotatedQuery() != null;
	}

	/**
	 * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation found
	 * nor the attribute was specified.
	 * 
	 * @return
	 */
	String getAnnotatedQuery() {

		String query = (String) AnnotationUtils.getValue(getQueryAnnotation());
		return StringUtils.hasText(query) ? query : null;
	}

	/**
	 * Returns the field specification to be used for the query.
	 * 
	 * @return
	 */
	String getFieldSpecification() {

		String value = (String) AnnotationUtils.getValue(getQueryAnnotation(), "fields");
		return StringUtils.hasText(value) ? value : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryMethod#getEntityInformation()
	 */
	@Override
	public MongoEntityInformation<?, ?> getEntityInformation() {

		return entityInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.QueryMethod#getParameters()
	 */
	@Override
	public MongoParameters getParameters() {
		return (MongoParameters) super.getParameters();
	}

	/**
	 * Returns whether te query is a geoNear query.
	 * 
	 * @return
	 */
	public boolean isGeoNearQuery() {

		return isGeoNearQuery(this.method);
	}

	private boolean isGeoNearQuery(Method method) {

		if (GEO_NEAR_RESULTS.contains(method.getReturnType())) {
			return true;
		}

		if (Iterable.class.isAssignableFrom(method.getReturnType())) {
			TypeInformation<?> from = ClassTypeInformation.fromReturnTypeOf(method);
			return GeoResult.class.equals(from.getComponentType().getType());
		}

		return false;
	}

	/**
	 * Returns the {@link Query} annotation that is applied to the method or {@code null} if none available.
	 * 
	 * @return
	 */
	private Query getQueryAnnotation() {
		return method.getAnnotation(Query.class);
	}

	TypeInformation<?> getReturnType() {
		return ClassTypeInformation.fromReturnTypeOf(method);
	}
}

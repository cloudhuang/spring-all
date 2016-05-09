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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.repository.Address;
import org.springframework.data.mongodb.repository.Person;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Unit tests for {@link StringBasedMongoQuery}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class StringBasedMongoQueryUnitTests {

	@Mock
	MongoOperations operations;
	@Mock
	RepositoryMetadata metadata;
	@Mock
	EntityInformationCreator creator;
	@Mock
	MongoDbFactory factory;

	MongoConverter converter;

	@Before
	public void setUp() {
		converter = new MappingMongoConverter(factory, new MongoMappingContext());
		when(operations.getConverter()).thenReturn(converter);
	}

	@Test
	public void bindsSimplePropertyCorrectly() throws Exception {

		Method method = SampleRepository.class.getMethod("findByLastname", String.class);
		MongoQueryMethod queryMethod = new MongoQueryMethod(method, metadata, creator);
		StringBasedMongoQuery mongoQuery = new StringBasedMongoQuery(queryMethod, operations);
		ConvertingParameterAccessor accesor = StubParameterAccessor.getAccessor(converter, "Matthews");

		org.springframework.data.mongodb.core.query.Query query = mongoQuery.createQuery(accesor);
		org.springframework.data.mongodb.core.query.Query reference = new BasicQuery("{'lastname' : 'Matthews'}");

		assertThat(query.getQueryObject(), is(reference.getQueryObject()));
	}

	@Test
	public void bindsComplexPropertyCorrectly() throws Exception {

		StringBasedMongoQuery mongoQuery = createQueryForMethod("findByAddress", Address.class);

		Address address = new Address("Foo", "0123", "Bar");
		ConvertingParameterAccessor accesor = StubParameterAccessor.getAccessor(converter, address);

		DBObject dbObject = new BasicDBObject();
		converter.write(address, dbObject);
		dbObject.removeField(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY);

		org.springframework.data.mongodb.core.query.Query query = mongoQuery.createQuery(accesor);
		BasicDBObject queryObject = new BasicDBObject("address", dbObject);
		org.springframework.data.mongodb.core.query.Query reference = new BasicQuery(queryObject);

		assertThat(query.getQueryObject(), is(reference.getQueryObject()));
	}

	@Test
	public void bindsMultipleParametersCorrectly() throws Exception {

		StringBasedMongoQuery mongoQuery = createQueryForMethod("findByLastnameAndAddress", String.class, Address.class);

		Address address = new Address("Foo", "0123", "Bar");
		ConvertingParameterAccessor accesor = StubParameterAccessor.getAccessor(converter, "Matthews", address);

		DBObject addressDbObject = new BasicDBObject();
		converter.write(address, addressDbObject);
		addressDbObject.removeField(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY);

		DBObject reference = new BasicDBObject("address", addressDbObject);
		reference.put("lastname", "Matthews");

		org.springframework.data.mongodb.core.query.Query query = mongoQuery.createQuery(accesor);
		assertThat(query.getQueryObject(), is(reference));
	}

	@Test
	public void bindsNullParametersCorrectly() throws Exception {

		StringBasedMongoQuery mongoQuery = createQueryForMethod("findByAddress", Address.class);
		ConvertingParameterAccessor accessor = StubParameterAccessor.getAccessor(converter, new Object[] { null });

		org.springframework.data.mongodb.core.query.Query query = mongoQuery.createQuery(accessor);
		assertThat(query.getQueryObject().containsField("address"), is(true));
		assertThat(query.getQueryObject().get("address"), is(nullValue()));
	}

	private StringBasedMongoQuery createQueryForMethod(String name, Class<?>... parameters) throws Exception {

		Method method = SampleRepository.class.getMethod(name, parameters);
		MongoQueryMethod queryMethod = new MongoQueryMethod(method, metadata, creator);
		return new StringBasedMongoQuery(queryMethod, operations);
	}

	private interface SampleRepository {

		@Query("{ 'lastname' : ?0 }")
		Person findByLastname(String lastname);

		@Query("{ 'address' : ?0 }")
		Person findByAddress(Address address);

		@Query("{ 'lastname' : ?0, 'address' : ?1 }")
		Person findByLastnameAndAddress(String lastname, Address address);
	}
}

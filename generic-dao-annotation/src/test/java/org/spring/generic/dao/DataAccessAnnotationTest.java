package org.spring.generic.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring.generic.CustomAnnotationConfiguration;
import org.spring.generic.dao.annonation.DataAccess;
import org.spring.generic.entity.Account;
import org.spring.generic.entity.Person;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CustomAnnotationConfiguration.class })
public class DataAccessAnnotationTest {
	@DataAccess(entity = Person.class)
	private GenericDao<Person> personGenericDao;
	@DataAccess(entity = Account.class)
	private GenericDao<Account> accountGenericDao;
	@DataAccess(entity = Person.class)
	private GenericDao<Person> anotherPersonGenericDao;

	@Test
	public void whenGenericDAOInitialized_thenNotNull() {
		assertThat(personGenericDao, is(notNullValue()));
		assertThat(accountGenericDao, is(notNullValue()));
		assertThat(anotherPersonGenericDao, is(notNullValue()));
	}

	@Test
	public void whenGenericDAOInjected_thenItIsSingleton() {
		assertThat(personGenericDao, sameInstance(anotherPersonGenericDao));
	}
}

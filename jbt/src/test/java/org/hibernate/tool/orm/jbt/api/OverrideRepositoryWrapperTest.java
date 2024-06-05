package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.internal.factory.OverrideRepositoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.RevengStrategyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableFilterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverrideRepositoryWrapperTest {

	private static final String HIBERNATE_REVERSE_ENGINEERING_XML =
			"<?xml version='1.0' encoding='UTF-8'?>                                 "+
			"<!DOCTYPE hibernate-reverse-engineering PUBLIC                         "+
			"      '-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN'         "+
			"      'http://hibernate.org/dtd/hibernate-reverse-engineering-3.0.dtd'>"+
			"<hibernate-reverse-engineering>                                        "+
			"    <table name='FOO'/>                                                "+
			"</hibernate-reverse-engineering>                                       ";

	private OverrideRepositoryWrapper overrideRepositoryWrapper = null;
	private OverrideRepository wrappedOverrideRepository = null;
	
	@BeforeEach
	public void beforeEach() {
		overrideRepositoryWrapper = OverrideRepositoryWrapperFactory.createOverrideRepositoryWrapper();
		wrappedOverrideRepository = (OverrideRepository)overrideRepositoryWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedOverrideRepository);
		assertNotNull(overrideRepositoryWrapper);
	}
	
	@Test
	public void testAddFile() throws Exception {
		File file = File.createTempFile("addFile", "tst");
		file.deleteOnExit();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(HIBERNATE_REVERSE_ENGINEERING_XML);
		fileWriter.close();
		Field tablesField = wrappedOverrideRepository.getClass().getDeclaredField("tables");
		tablesField.setAccessible(true);
		Object object = tablesField.get(wrappedOverrideRepository);
		List<?> tables = (List<?>)object;
		assertNotNull(tables);
		assertTrue(tables.isEmpty());
		overrideRepositoryWrapper.addFile(file);
		object = tablesField.get(wrappedOverrideRepository);
		tables = (List<?>)object;
		assertNotNull(tables);
		assertFalse(tables.isEmpty());
		Table table = (Table)tables.get(0);
		assertNotNull(table);
		assertEquals("FOO", table.getName());
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		RevengStrategyWrapper revWrapper = RevengStrategyWrapperFactory.createRevengStrategyWrapper();
		RevengStrategy rev = (RevengStrategy)revWrapper.getWrappedObject();
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		RevengStrategyWrapper delegatingStrategy = overrideRepositoryWrapper.getReverseEngineeringStrategy(revWrapper);
		assertNotNull(delegatingStrategy);
		assertSame(rev, delegateField.get(delegatingStrategy.getWrappedObject()));
	}
	
	@Test
	public void testAddTableFilter() throws Exception {
		TableFilter tableFilter = new TableFilter();
		TableFilterWrapper tableFilterWrapper = TableFilterWrapperFactory.createTableFilterWrapper(tableFilter);
		Field tableFiltersField = OverrideRepository.class.getDeclaredField("tableFilters");
		tableFiltersField.setAccessible(true);
		List<?> tableFilters = (List<?>)tableFiltersField.get(wrappedOverrideRepository);
		assertTrue(tableFilters.isEmpty());
		overrideRepositoryWrapper.addTableFilter(tableFilterWrapper);
		tableFilters = (List<?>)tableFiltersField.get(wrappedOverrideRepository);
		assertSame(tableFilter, tableFilters.get(0));		
	}
	
}

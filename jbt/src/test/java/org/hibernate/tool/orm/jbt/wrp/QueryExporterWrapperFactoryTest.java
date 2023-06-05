package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryExporterWrapperFactoryTest {

	private QueryExporter wrappedQueryExporter = null;
	private QueryExporterWrapperFactory.QueryExporterWrapper queryExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedQueryExporter = new QueryExporter();
		queryExporterWrapper = QueryExporterWrapperFactory.create(wrappedQueryExporter);
	}
	
	@Test
	public void testCreate() {
		assertNotNull(queryExporterWrapper);
		assertSame(wrappedQueryExporter, queryExporterWrapper.getWrappedObject());
	}
	
	@Test
	public void testSetQueries() {
		List<String> queries = Collections.emptyList();
		assertNotSame(queries, wrappedQueryExporter.getProperties().get(ExporterConstants.QUERY_LIST));
		queryExporterWrapper.setQueries(queries);
		assertSame(queries, wrappedQueryExporter.getProperties().get(ExporterConstants.QUERY_LIST));
	}	

	@Test
	public void testSetFileName() {
		assertNotEquals("foo", wrappedQueryExporter.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		queryExporterWrapper.setFilename("foo");
		assertEquals("foo", wrappedQueryExporter.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
	}
	
}

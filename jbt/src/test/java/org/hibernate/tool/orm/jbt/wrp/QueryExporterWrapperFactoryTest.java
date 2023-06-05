package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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

}

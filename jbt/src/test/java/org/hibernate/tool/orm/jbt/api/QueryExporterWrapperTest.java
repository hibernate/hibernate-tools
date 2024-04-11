package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.internal.factory.QueryExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryExporterWrapperTest {

	private QueryExporter wrappedQueryExporter = null;
	private QueryExporterWrapper queryExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedQueryExporter = new QueryExporter();
		queryExporterWrapper = QueryExporterWrapperFactory.createQueryExporterWrapper(wrappedQueryExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(queryExporterWrapper);
		assertSame(wrappedQueryExporter, queryExporterWrapper.getWrappedObject());
	}
	
}

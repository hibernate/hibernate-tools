package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.internal.export.query.QueryExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryExporterWrapperTest {

	private QueryExporterWrapper queryExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		queryExporterWrapper = new QueryExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(queryExporterWrapper);
		assertTrue(queryExporterWrapper instanceof QueryExporter);
	}
	
}
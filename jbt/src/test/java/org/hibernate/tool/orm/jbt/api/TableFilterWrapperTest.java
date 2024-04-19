package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.internal.factory.TableFilterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableFilterWrapperTest {

	private TableFilter wrappedTableFilter = null;
	private TableFilterWrapper tableFilterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedTableFilter = new TableFilter();
		tableFilterWrapper = TableFilterWrapperFactory.createTableFilterWrapper(wrappedTableFilter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedTableFilter);
		assertNotNull(tableFilterWrapper);
	}
	
}

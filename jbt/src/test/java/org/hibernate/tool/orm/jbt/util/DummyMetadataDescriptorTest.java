package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.junit.jupiter.api.Test;

public class DummyMetadataDescriptorTest {
	
	@Test
	public void testConstruction() {
		assertTrue(new DummyMetadataDescriptor() instanceof MetadataDescriptor);
	}
	
	@Test
	public void testGetProperties() {
		assertNull(new DummyMetadataDescriptor().getProperties());
	}
	
	@Test
	public void testCreateMetadata() {
		Metadata metadata = new DummyMetadataDescriptor().createMetadata();
		assertNotNull(metadata);
		assertEquals(Collections.emptySet(), metadata.getEntityBindings());
		assertEquals(Collections.emptySet(), metadata.collectTableMappings());
	}

}

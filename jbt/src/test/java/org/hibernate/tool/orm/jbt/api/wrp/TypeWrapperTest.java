package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeWrapperTest {

	private TypeConfiguration typeConfiguration = null;
	
	@BeforeEach
	public void beforeEach() {
		typeConfiguration = new TypeConfiguration();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(typeConfiguration);
		assertNotNull(TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(int.class)));
	}

	@Test
	public void testToString() {
		// first try type that is string representable
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertEquals(
				TypeWrapperTest.class.getName(), 
				classTypeWrapper.toString(TypeWrapperTest.class));
		// next try a type that cannot be represented by a string
		try {
			TypeWrapper arrayTypeWrapper = 
					TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
			arrayTypeWrapper.toString(new String[] { "foo", "bar" });
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'toString(Object)'"));
		}
	}
	
	@Test
	public void testGetName() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertEquals("class", classTypeWrapper.getName());
		// next try a array type
		TypeWrapper arrayTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
		assertEquals("[Ljava.lang.String;(foo)", arrayTypeWrapper.getName());
	}
	
	@Test
	public void testFromStringValue() {
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertEquals(
				TypeWrapperTest.class, 
				classTypeWrapper.fromStringValue(TypeWrapperTest.class.getName()));
		// next try type that is not string representable
		try {
			TypeWrapper arrayTypeWrapper = 
					TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
			arrayTypeWrapper.fromStringValue("just a random string");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'fromStringValue(Object)'"));
		}
	}
	
	@Test
	public void testIsEntityType() {
		// first try type that is not an entity type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isEntityType());
		// next try type that is an entity type
		TypeWrapper entityTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				new ManyToOneType((TypeConfiguration)null, null));
		assertTrue(entityTypeWrapper.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		// first try type that is not a one to one type
		try {
			TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
					typeConfiguration.getBasicTypeForJavaType(Class.class));
			classTypeWrapper.isOneToOne();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isOneToOne()'"));
		}
		// next try another type that is not a one to one type
		TypeWrapper entityTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				new ManyToOneType((TypeConfiguration)null, null));
		assertFalse(entityTypeWrapper.isOneToOne());
		// finally try a type that is a one to one type
		TypeWrapper oneToOneTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				new OneToOneType(
						null, null, null, false, null, false, false, null, null, false));
		assertTrue(oneToOneTypeWrapper.isOneToOne());
	}
	
	@Test
	public void testIsAnyType() {
		// first try type that is not a any type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isAnyType());
		// next try a any type
		TypeWrapper anyTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new AnyType(null, null, null, true));
		assertTrue(anyTypeWrapper.isAnyType());
	}
	
	@Test
	public void testIsComponentType() {
		// first try type that is not a component type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isComponentType());
		// next try a component type
		Component component = new Component(
				DummyMetadataBuildingContext.INSTANCE, 
				new RootClass(DummyMetadataBuildingContext.INSTANCE));
		component.setComponentClassName("java.lang.Object");
		TypeWrapper anyTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(
						new ComponentType(component, null, DummyMetadataBuildingContext.INSTANCE));
		assertTrue(anyTypeWrapper.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		// first try type that is not a collection type
		TypeWrapper classTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(
						typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isCollectionType());
		// next try a collection type
		TypeWrapper arrayTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class));
		assertTrue(arrayTypeWrapper.isCollectionType());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertNull(classTypeWrapper.getAssociatedEntityName());
		// next try a many to one type 
		TypeWrapper manyToOneTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				new ManyToOneType((TypeConfiguration)null, "foo"));
		assertEquals("foo", manyToOneTypeWrapper.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isIntegerType());
		// next try a integer type 
		TypeWrapper integerTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Integer.class));
		assertTrue(integerTypeWrapper.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isArrayType());
		// next try a bag type
		TypeWrapper bagTypeWrapper = TypeWrapperFactory.createTypeWrapper(new BagType(null, null));
		assertFalse(bagTypeWrapper.isArrayType());
		// finally try a array type
		TypeWrapper arrayTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
		assertTrue(arrayTypeWrapper.isArrayType());
	}
	
	@Test
	public void testIsInstanceOfPrimitiveType() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertFalse(classTypeWrapper.isInstanceOfPrimitiveType());
		// next try a string type
		TypeWrapper stringTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(String.class));
		assertFalse(stringTypeWrapper.isInstanceOfPrimitiveType());
		// finally try a integer type 
		TypeWrapper integerTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Integer.class));
		assertTrue(integerTypeWrapper.isInstanceOfPrimitiveType());
	}
	
	@Test
	public void testGetPrimitiveClass() {
		// first try a class type
		try {
			TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
					typeConfiguration.getBasicTypeForJavaType(Class.class));
			classTypeWrapper.getPrimitiveClass();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPrimitiveClass()'"));
		}
		// next try a integer type 
		TypeWrapper integerTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Integer.class));
		assertEquals(int.class, integerTypeWrapper.getPrimitiveClass());
	}

	@Test
	public void testGetRole() {
		// first try a class type
		try {
			TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
					typeConfiguration.getBasicTypeForJavaType(Class.class));
			classTypeWrapper.getRole();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getRole()'"));
		}
		// finally try a array type
		TypeWrapper arrayTypeWrapper = 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class));
		assertEquals("foo", arrayTypeWrapper.getRole());
	}

	@Test
	public void testGetReturnedClassName() {
		// first try a class type
		TypeWrapper classTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				typeConfiguration.getBasicTypeForJavaType(Class.class));
		assertEquals(Class.class.getName(), classTypeWrapper.getReturnedClassName());
		// next try an array type of string values
		TypeWrapper arrayTypeWrapper = TypeWrapperFactory.createTypeWrapper(
				new ArrayType("foo", "bar", String.class));
		assertEquals(String[].class.getName(), arrayTypeWrapper.getReturnedClassName());
		// next try a many to one type 
		TypeConfiguration typeConfiguration = new TypeConfiguration();
		MetadataBuildingContext metadataBuildingContext = DummyMetadataBuildingContext.INSTANCE;
		PersistentClass orgFooBarClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		orgFooBarClass.setEntityName("org.foo.bar");
		orgFooBarClass.setClassName(OrgFooBar.class.getName());
		metadataBuildingContext.getMetadataCollector().getEntityBindingMap().put(
				"org.foo.bar", 
				orgFooBarClass);
		typeConfiguration.scope(DummyMetadataBuildingContext.INSTANCE);
		TypeWrapper manyToOneTypeWrapper = TypeWrapperFactory.createTypeWrapper(
						new ManyToOneType(typeConfiguration, "org.foo.bar"));
		assertEquals(OrgFooBar.class.getName(), manyToOneTypeWrapper.getReturnedClassName());
	}
	
	public static class OrgFooBar {}

}

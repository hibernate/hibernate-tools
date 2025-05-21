/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2023-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.language.internal;

import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.tool.language.spi.MetamodelSerializer;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.tool.language.internal.JsonHelper.JsonAppender;

/**
 * Implementation of {@link MetamodelSerializer} that represents the {@link Metamodel} as a JSON array of mapped objects.
 */
public class MetamodelJsonSerializerImpl implements MetamodelSerializer {
	public static MetamodelJsonSerializerImpl INSTANCE = new MetamodelJsonSerializerImpl();

	/**
	 * Utility method that generates a JSON string representation of the mapping information
	 * contained in the provided {@link Metamodel metamodel} instance. The representation
	 * does not follow a strict scheme, and is more akin to natural language, as it's
	 * mainly meant for consumption by a LLM.
	 *
	 * @param metamodel the metamodel instance containing information on the persistence structures
	 *
	 * @return the JSON representation of the provided {@link Metamodel metamodel}
	 */
	@Override
	public String toString(Metamodel metamodel) {
		final List<String> entities = new ArrayList<>();
		final List<String> embeddables = new ArrayList<>();
		final List<String> mappedSupers = new ArrayList<>();
		for ( ManagedType<?> managedType : metamodel.getManagedTypes() ) {
			switch ( managedType.getPersistenceType() ) {
				case ENTITY -> entities.add( getEntityTypeDescription( (EntityType<?>) managedType ) );
				case EMBEDDABLE -> embeddables.add( getEmbeddableTypeDescription( (EmbeddableType<?>) managedType ) );
				case MAPPED_SUPERCLASS -> mappedSupers.add( getMappedSuperclassTypeDescription( (MappedSuperclassType<?>) managedType ) );
				default ->
						throw new IllegalStateException( "Unexpected persistence type for managed type [" + managedType + "]" );
			}
		}
		return toJson( Map.of(
				"entities", entities,
				"mappedSuperclasses", mappedSupers,
				"embeddables", embeddables
		) );
	}

	private static String toJson(Collection<String> strings) {
		return strings.isEmpty() ? "[]" : "[" + String.join( ",", strings ) + "]";
	}

	private static String toJson(Map<String, Object> map) {
		if ( map.isEmpty() ) {
			return "{}";
		}
		final StringBuilder sb = new StringBuilder( "{" );
		final JsonAppender appender = new JsonAppender( sb, false );
		for ( final var entry : map.entrySet() ) {
			appender.append( "\"" ).append( entry.getKey() ).append( "\":" );
			final Object value = entry.getValue();
			if ( value instanceof String strValue ) {
				appender.append( "\"" );
				appender.startEscaping();
				appender.append( strValue );
				appender.endEscaping();
				appender.append( "\"" );
			}
			else if ( value instanceof Collection<?> collection ) {
				//noinspection unchecked
				appender.append( toJson( (Collection<String>) collection ) );
			}
			else if ( value instanceof Number || value instanceof Boolean ) {
				appender.append( value.toString() );
			}
			else if ( value == null ) {
				appender.append( "null" );
			}
			else {
				throw new IllegalArgumentException( "Unsupported value type: " + value.getClass().getName() );
			}
			appender.append( "," );
		}
		return sb.deleteCharAt( sb.length() - 1 ).append( '}' ).toString();
	}

	private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
		if ( value != null ) {
			map.put( key, value );
		}
	}

	private static <T> String getEntityTypeDescription(EntityType<T> entityType) {
		final Map<String, Object> map = new HashMap<>( 5 );
		map.put( "name", entityType.getName() );
		map.put( "class", entityType.getJavaType().getTypeName() );
		putIfNotNull( map, "superType", superTypeDescriptor( (ManagedDomainType<?>) entityType ) );
		putIfNotNull( map, "identifierAttribute", identifierDescriptor( entityType ) );
		map.put( "attributes", attributeArray( entityType.getAttributes() ) );
		return toJson( map );
	}

	private static String superTypeDescriptor(ManagedDomainType<?> managedType) {
		final ManagedDomainType<?> superType = managedType.getSuperType();
		return superType != null ? superType.getJavaType().getTypeName() : null;
	}

	private static <T> String getMappedSuperclassTypeDescription(MappedSuperclassType<T> mappedSuperclass) {
		final Class<T> javaType = mappedSuperclass.getJavaType();
		final Map<String, Object> map = new HashMap<>( 5 );
		map.put( "name", javaType.getSimpleName() );
		map.put( "class", javaType.getTypeName() );
		putIfNotNull( map, "superType", superTypeDescriptor( (ManagedDomainType<?>) mappedSuperclass ) );
		putIfNotNull( map, "identifierAttribute", identifierDescriptor( mappedSuperclass ) );
		map.put( "attributes", attributeArray( mappedSuperclass.getAttributes() ) );
		return toJson( map );
	}

	private static <T> String identifierDescriptor(IdentifiableType<T> identifiableType) {
		final Type<?> idType = identifiableType.getIdType();
		if ( idType != null ) {
			final SingularAttribute<? super T, ?> id = identifiableType.getId( idType.getJavaType() );
			return id.getName();
		}
		else {
			return null;
		}
	}

	private static <T> String getEmbeddableTypeDescription(EmbeddableType<T> embeddableType) {
		final Class<T> javaType = embeddableType.getJavaType();
		final Map<String, Object> map = new HashMap<>( 4 );
		map.put( "name", javaType.getSimpleName() );
		map.put( "class", javaType.getTypeName() );
		putIfNotNull( map, "superType", superTypeDescriptor( (ManagedDomainType<?>) embeddableType ) );
		map.put( "attributes", attributeArray( embeddableType.getAttributes() ) );
		return toJson( map );
	}

	private static <T> List<String> attributeArray(Set<Attribute<? super T, ?>> attributes) {
		if ( attributes.isEmpty() ) {
			return List.of();
		}

		final ArrayList<String> result = new ArrayList<>( attributes.size() );
		for ( final Attribute<? super T, ?> attribute : attributes ) {
			String attributeDescription = "{\"name\":\"" + attribute.getName() +
					"\",\"type\":\"" + attribute.getJavaType().getTypeName();
			// add key and element types for plural attributes
			if ( attribute instanceof PluralAttribute<?, ?, ?> pluralAttribute ) {
				attributeDescription += "<";
				final PluralAttribute.CollectionType collectionType = pluralAttribute.getCollectionType();
				if ( collectionType == PluralAttribute.CollectionType.MAP ) {
					attributeDescription +=  ( (MapAttribute<?, ?, ?>) pluralAttribute ).getKeyJavaType().getTypeName() + ",";
				}
				attributeDescription += pluralAttribute.getElementType().getJavaType().getTypeName() + ">";
			}
			result.add( attributeDescription + "\"}" );
		}
		return result;
	}
}

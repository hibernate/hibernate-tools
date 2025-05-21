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
import java.util.List;
import java.util.Set;

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
		return "{" +
				"\"entities\":" + toJsonArray( entities ) +
				",\"mappedSuperclasses\":" + toJsonArray( mappedSupers ) +
				",\"embeddables\":" + toJsonArray( embeddables ) +
				'}';
	}

	static String toJsonArray(Collection<String> strings) {
		return strings.isEmpty() ? "[]" : "[" + String.join( ",", strings ) + "]";
	}

	static <T> String getEntityTypeDescription(EntityType<T> entityType) {
		return "{\"name\":\"" + entityType.getName() + "\"," +
				"\"class\":\"" + entityType.getJavaType().getTypeName() + "\"," +
				superTypeDescriptor( (ManagedDomainType<?>) entityType ) +
				identifierDescriptor( entityType ) +
				"\"attributes\":" + attributeArray( entityType.getAttributes() ) +
				"}";
	}

	static String superTypeDescriptor(ManagedDomainType<?> managedType) {
		final ManagedDomainType<?> superType = managedType.getSuperType();
		return superType != null ? "\"superType\":\"" + superType.getJavaType().getTypeName() + "\"," : "";
	}

	static <T> String getMappedSuperclassTypeDescription(MappedSuperclassType<T> mappedSuperclass) {
		final Class<T> javaType = mappedSuperclass.getJavaType();
		return "{\"name\":\"" + javaType.getSimpleName() + "\"," +
				"\"class\":\"" + javaType.getTypeName() + "\"," +
				superTypeDescriptor( (ManagedDomainType<?>) mappedSuperclass ) +
				identifierDescriptor( mappedSuperclass ) +
				"\"attributes\":" + attributeArray( mappedSuperclass.getAttributes() ) +
				"}";
	}

	static <T> String identifierDescriptor(IdentifiableType<T> identifiableType) {
		final Type<?> idType = identifiableType.getIdType();
		final String description;
		if ( idType != null ) {
			final SingularAttribute<? super T, ?> id = identifiableType.getId( idType.getJavaType() );
			description = "\"identifierAttribute\":\"" + id.getName() + "\",";
		}
		else {
			description = "";
		}
		return description;
	}

	static <T> String getEmbeddableTypeDescription(EmbeddableType<T> embeddableType) {
		final Class<T> javaType = embeddableType.getJavaType();
		return "{\"name\":\"" + javaType.getSimpleName() + "\"," +
				"\"class\":\"" + javaType.getTypeName() + "\"," +
				superTypeDescriptor( (ManagedDomainType<?>) embeddableType ) +
				"\"attributes\":" + attributeArray( embeddableType.getAttributes() ) +
				"}";
	}

	static <T> String attributeArray(Set<Attribute<? super T, ?>> attributes) {
		if ( attributes.isEmpty() ) {
			return "[]";
		}

		final StringBuilder sb = new StringBuilder( "[" );
		for ( final Attribute<? super T, ?> attribute : attributes ) {
			sb.append( "{\"name\":\"" ).append( attribute.getName() )
					.append( "\",\"type\":\"" ).append( attribute.getJavaType().getTypeName() );
			// add key and element types for plural attributes
			if ( attribute instanceof PluralAttribute<?, ?, ?> pluralAttribute ) {
				sb.append( '<' );
				final PluralAttribute.CollectionType collectionType = pluralAttribute.getCollectionType();
				if ( collectionType == PluralAttribute.CollectionType.MAP ) {
					sb.append( ( (MapAttribute<?, ?, ?>) pluralAttribute ).getKeyJavaType().getTypeName() )
							.append( "," );
				}
				sb.append( pluralAttribute.getElementType().getJavaType().getTypeName() ).append( '>' );
			}
			sb.append( "\"}," );
		}
		return sb.deleteCharAt( sb.length() - 1 ).append( ']' ).toString();
	}
}

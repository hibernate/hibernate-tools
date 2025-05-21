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

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.mapping.CollectionPart;
import org.hibernate.metamodel.mapping.EmbeddableValuedModelPart;
import org.hibernate.metamodel.mapping.EntityValuedModelPart;
import org.hibernate.metamodel.mapping.PluralAttributeMapping;
import org.hibernate.metamodel.mapping.ValuedModelPart;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.sqm.SqmExpressible;
import org.hibernate.query.sqm.SqmSelectionQuery;
import org.hibernate.query.sqm.tree.SqmExpressibleAccessor;
import org.hibernate.query.sqm.tree.SqmStatement;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.query.sqm.tree.from.SqmRoot;
import org.hibernate.query.sqm.tree.select.SqmJpaCompoundSelection;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.query.sqm.tree.select.SqmSelection;
import org.hibernate.tool.language.internal.JsonHelper.JsonAppender;
import org.hibernate.tool.language.spi.ResultsSerializer;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Selection;
import java.util.List;

import static org.hibernate.internal.util.NullnessUtil.castNonNull;

/**
 * Utility class to serialize query results into a JSON string format.
 */
public class ResultsJsonSerializerImpl implements ResultsSerializer {
	private final SessionFactoryImplementor factory;

	public ResultsJsonSerializerImpl(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

	@Override
	public <T> String toString(List<? extends T> values, SelectionQuery<T> query) {
		if ( values.isEmpty() ) {
			return "[]";
		}

		final StringBuilder sb = new StringBuilder();
		final JsonAppender jsonAppender = new JsonAppender( sb, true );
		char separator = '[';
		for ( final T value : values ) {
			sb.append( separator );
			renderValue( value, (SqmSelectionQuery<? super T>) query, jsonAppender );
			separator = ',';
		}
		sb.append( ']' );
		return sb.toString();
	}

	private <T> void renderValue(T value, SqmSelectionQuery<? super T> query, JsonAppender jsonAppender) {
		final SqmStatement<?> sqm = query.getSqmStatement();
		if ( !( sqm instanceof SqmSelectStatement<?> sqmSelect ) ) {
			throw new IllegalArgumentException( "Query is not a select statement." );
		}
		final List<SqmSelection<?>> selections = sqmSelect.getQuerySpec().getSelectClause().getSelections();
		assert !selections.isEmpty();
		if ( selections.size() == 1 ) {
			renderValue( value, selections.get( 0 ).getSelectableNode(), jsonAppender );
		}
		else {
			// wrap each result tuple in square brackets
			char separator = '[';
			for ( int i = 0; i < selections.size(); i++ ) {
				jsonAppender.append( separator );
				final SqmSelection<?> selection = selections.get( i );
				if ( value instanceof Object[] array ) {
					renderValue( array[i], selection.getSelectableNode(), jsonAppender );
				}
				else if ( value instanceof Tuple tuple ) {
					renderValue( tuple.get( i ), selection.getSelectableNode(), jsonAppender );
				}
				else {
					// todo : might it be a compound selection ?
					renderValue( value, selection.getSelectableNode(), jsonAppender );
				}
				separator = ',';
			}
			jsonAppender.append( ']' );
		}
	}

	private void renderValue(Object value, Selection<?> selection, JsonAppender jsonAppender) {
			if ( selection instanceof SqmRoot<?> root ) {
				final EntityPersister persister = factory.getMappingMetamodel()
						.getEntityDescriptor( root.getEntityName() );
				JsonHelper.toString(
						value,
						persister.getEntityMappingType(),
						factory.getWrapperOptions(),
						jsonAppender
				);
			}
			else if ( selection instanceof  SqmPath<?> path ) {
				// extract the attribute from the path
				final ValuedModelPart subPart = getSubPart( path.getLhs(), path.getNavigablePath().getLocalName() );
				if ( subPart != null ) {
					JsonHelper.toString( value, subPart, factory.getWrapperOptions(), jsonAppender, null );
				}
				else {
					jsonAppender.append( expressibleToString( path, value ) );
				}
			}
			else if ( selection instanceof  SqmJpaCompoundSelection<?> compoundSelection ) {
				final List<Selection<?>> compoundSelectionItems = compoundSelection.getCompoundSelectionItems();
				assert compoundSelectionItems.size() > 1;
				char separator = '[';
				for ( int j = 0; j < compoundSelectionItems.size(); j++ ) {
					jsonAppender.append( separator );
					renderValue( getValue( value, j ), compoundSelectionItems.get( j ), jsonAppender );
					separator = ',';
				}
				jsonAppender.append( ']' );
			}
			else if ( selection instanceof  SqmExpressibleAccessor<?> node ) {
				jsonAppender.append( expressibleToString( node, value ) );
			}
			else {
				jsonAppender.append( "\"" ).append( value.toString() ).append( "\"" ); // best effort
			}
	}

	private static String expressibleToString(SqmExpressibleAccessor<?> node, Object value) {
		//noinspection unchecked
		final SqmExpressible<Object> expressible = (SqmExpressible<Object>) node.getExpressible();
		final String result = expressible != null ?
				expressible.getExpressibleJavaType().toString( value ) :
				value.toString(); // best effort
		// avoid quoting numbers as they can be represented in JSON
		return value instanceof Number ? result : "\"" + result + "\"";
	}

	private static Object getValue(Object value, int index) {
		if ( value.getClass().isArray() ) {
			return ( (Object[]) value )[index];
		}
		else if ( value instanceof Tuple tuple ) {
			return tuple.get( index );
		}
		else {
			if ( index > 0 ) {
				throw new IllegalArgumentException( "Index out of range: " + index );
			}
			return value;
		}
	}

	private ValuedModelPart getSubPart(SqmPath<?> path, String propertyName) {
		if ( path instanceof SqmRoot<?> root ) {
			final EntityPersister entityDescriptor = factory.getMappingMetamodel()
					.getEntityDescriptor( root.getEntityName() );
			return entityDescriptor.findAttributeMapping( propertyName );
		}
		else {
			// try to derive the subpart from the lhs
			final ValuedModelPart subPart = getSubPart( path.getLhs(), path.getNavigablePath().getLocalName() );
			if ( subPart instanceof EmbeddableValuedModelPart embeddable ) {
				return embeddable.getEmbeddableTypeDescriptor().findAttributeMapping( propertyName );
			}
			else if ( subPart instanceof EntityValuedModelPart entity ) {
				return entity.getEntityMappingType().findAttributeMapping( propertyName );
			}
			else if ( subPart instanceof PluralAttributeMapping plural ) {
				final CollectionPart.Nature nature = castNonNull( CollectionPart.Nature.fromNameExact( propertyName ) );
				return switch ( nature ) {
					case ELEMENT -> plural.getElementDescriptor();
					case ID -> plural.getIdentifierDescriptor();
					case INDEX -> plural.getIndexDescriptor();
				};
			}
		}
		return null;
	}
}
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
package org.hibernate.tool.language.spi;

import org.hibernate.query.SelectionQuery;

import java.util.List;

/**
 * Contract used to serialize query results into a JSON string format,
 * with special care towards Hibernate-specific complexities like
 * laziness and circular associations.
 */
public interface ResultsSerializer {
	/**
	 * Serialize the given list of {@code values}, that have been returned by the provided {@code query} into a JSON string format.
	 *
	 * @param values list of values returned by the query
	 * @param query query object, used to determine the type of the values
	 * @param <T> the type of objects returned by the query
	 *
	 * @return JSON string representation of the values
	 */
	<T> String toString(List<? extends T> values, SelectionQuery<T> query);
}

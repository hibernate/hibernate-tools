/*
 * Copyright (C) 2023 Hibernate.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.hibernate.tool.entitynaming;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.util.NameConverter;

/**
 *
 * @author Daren
 */
public class RevengStrategyEntityNaming extends DelegatingStrategy {

  private List<SchemaSelection> schemas;

  public RevengStrategyEntityNaming(RevengStrategy delegate) {
    super(delegate);
    this.schemas = new ArrayList<>();
    schemas.add(new SchemaSelection(){
      @Override
      public String getMatchCatalog() {
        /* no h2 pattern matching on catalog*/
        return "test1";
      }

      @Override
      public String getMatchSchema() {
        return "PUBLIC";
      }

      @Override
      public String getMatchTable() {
        return ".*";
      }
    });
  }

  public List<SchemaSelection> getSchemaSelections() {
    return schemas;
  }

    }

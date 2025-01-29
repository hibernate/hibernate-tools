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

import org.hibernate.mapping.MetaAttribute;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Daren
 */
public class RevengStrategyEntityNaming extends DelegatingStrategy {

  private List<SchemaSelection> schemas;

  public RevengStrategyEntityNaming(RevengStrategy delegate) {
    super(delegate);
    this.schemas = new ArrayList<>();
    schemas.add(new SchemaSelection() {
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

  /**
   * Selection of entity field for Version annotation.
   */
  @Override
  public boolean useColumnForOptimisticLock(TableIdentifier identifier,
      String column) {
    return column.matches(".*Version$");
  }

  @Override
  public Map<String, MetaAttribute> tableToMetaAttributes(TableIdentifier tableIdentifier) {
    HashMap<String, MetaAttribute> metaData = new HashMap<>();
    getClassCode(tableIdentifier, metaData);
    getClassDesc(tableIdentifier, metaData);
    return metaData;
  }

  @Override
  public Map<String, MetaAttribute> columnToMetaAttributes(TableIdentifier identifier, String column) {
    HashMap<String, MetaAttribute> metaData = new HashMap<>();
    useInToString(identifier, column, metaData);
    useInEquals(identifier, column, metaData);
    return metaData;
	}

  private void getClassCode(TableIdentifier tableIdentifier, HashMap<String, MetaAttribute> metaData) {
    String tableName = tableIdentifier.getName();
    String code = "";
    switch (tableName) {
      case "dummy":
         code = "\n"
            + "    public void specialProc() {\n"
            + "    }\n";
        break;
      default:
    }
    if (code.length() > 0) {
      MetaAttribute ma = new MetaAttribute("class-code");
      ma.addValue(code);
      metaData.put("class-code", ma);
    }
  }

  private void getClassDesc(TableIdentifier tableIdentifier, HashMap<String, MetaAttribute> metaData) {
    String tableName = tableIdentifier.getName();
    String desc = "";
    switch (tableName) {
      case "dummy":
         desc = "Long description\n"
            + "for javadoc\n"
            + "of a particular entity.\n";
        break;
      default:
    }
    if (desc.length() > 0) {
      MetaAttribute ma = new MetaAttribute("class-description");
      ma.addValue(desc);
      metaData.put("class-description", ma);
    }
  }

  private void useInToString(TableIdentifier tableIdentifier, String column, HashMap<String, MetaAttribute> metaData) {
    String tableName = tableIdentifier.getName();
    String value= "";
    switch (tableName) {
      case "dummy":
        if( column.contentEquals("duData")) {
          value = "true";
        }
        break;
      default:
    }
    if (value.length() > 0) {
      MetaAttribute ma = new MetaAttribute("use-in-tostring");
      ma.addValue(value);
      metaData.put("use-in-tostring", ma);
    }
  }

  private void useInEquals(TableIdentifier tableIdentifier, String column, HashMap<String, MetaAttribute> metaData) {
    String tableName = tableIdentifier.getName();
    String value= "";
    switch (tableName) {
      case "dummy":
        if( column.contentEquals("duData")) {
          value = "true";
        } else if( column.contentEquals("duId")) {
          value = "true";
        }
        break;
      default:
    }
    if (value.length() > 0) {
      MetaAttribute ma = new MetaAttribute("use-in-equals");
      ma.addValue(value);
      metaData.put("use-in-equals", ma);
    }
  }
}

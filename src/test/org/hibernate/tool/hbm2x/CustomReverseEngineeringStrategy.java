/*
 * Copyright (C) 2015 Hibernate.
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
package org.hibernate.tool.hbm2x;

import org.hibernate.cfg.reveng.AssociationInfo;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.ForeignKey;

/**
 *
 * @author daren
 */
public class CustomReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

    public CustomReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
    }

    @Override
    public AssociationInfo foreignKeyToAssociationInfo(ForeignKey foreignKey) {

        return new AssociationInfo() {

            @Override
            public String getCascade() {
                //persist,merge,delete,refresh,all
                return null;
            }

            @Override
            public String getFetch() {
                //DEFAULT JOIN=eager SELECT=lazy
                return "join";
            }

            @Override
            public Boolean getUpdate() {
                return null;
            }

            @Override
            public Boolean getInsert() {
                return null;
            }
        };
    }

}

package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface TableFilterWrapper extends Wrapper {

	default void setExclude(boolean b) { ((TableFilter)getWrappedObject()).setExclude(b); }

	default void setMatchCatalog(String s) { ((TableFilter)getWrappedObject()).setMatchCatalog(s); }

	default void setMatchSchema(String s) { ((TableFilter)getWrappedObject()).setMatchSchema(s); }

	default void setMatchName(String s) { ((TableFilter)getWrappedObject()).setMatchName(s); }

	default Boolean getExclude() { return ((TableFilter)getWrappedObject()).getExclude(); }

	default String getMatchCatalog() { return ((TableFilter)getWrappedObject()).getMatchCatalog(); }

	default String getMatchSchema() { return ((TableFilter)getWrappedObject()).getMatchSchema(); }

}

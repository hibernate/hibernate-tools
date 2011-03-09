package org.hibernate.tool.hbmlint.detector;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.IssueCollector;

public abstract class RelationalModelDetector extends Detector {

	public void visit(Configuration cfg, IssueCollector collector) {
		for (Iterator iter = cfg.getTableMappings(); iter.hasNext();) {
			Table table = (Table) iter.next();
			this.visit(cfg, table, collector);
		}					
	}


	protected void visit(Configuration cfg, Table table, Column col, IssueCollector collector) {
				
	}

	protected void visitColumns(Configuration cfg, Table table, IssueCollector collector) {
		Iterator columnIter = table.getColumnIterator();
		while ( columnIter.hasNext() ) {
			Column col = ( Column ) columnIter.next();
			this.visit( cfg, table, col, collector );
		}		
	}

	/**
	 * @return true if visit should continue down through the columns 
	 */
	protected void visit(Configuration cfg, Table table, IssueCollector collector) {
		visitColumns(cfg, table, collector);
	}
	
}


package org.hibernate.tool.hbmlint;

import org.hibernate.cfg.Configuration;

public abstract class Detector {

	private Configuration cfg;
	
	public void initialize(Configuration configuration) {
		this.cfg = configuration;
	}
	
	protected Configuration getConfiguration() {
		return cfg;
	}

	public void visit(Configuration configuration, IssueCollector collector) {
		
	}
	
	abstract public String getName();
}

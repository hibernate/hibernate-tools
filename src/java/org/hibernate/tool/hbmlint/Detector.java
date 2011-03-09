package org.hibernate.tool.hbmlint;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;

public abstract class Detector {

	private Configuration cfg;
	private Settings settings;

	public void initialize(Configuration configuration, Settings settings) {
		this.cfg = configuration;
		this.settings = settings;		
	}
	
	protected Settings getSettings() {
		return settings;
	}
	
	protected Configuration getConfiguration() {
		return cfg;
	}

	public void visit(Configuration configuration, IssueCollector collector) {
		
	}
	
	abstract public String getName();
}

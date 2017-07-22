package org.hibernate.tool.hbm2x;

import org.hibernate.tool.hbmlint.HbmLint;
import org.hibernate.tool.util.MetadataHelper;

public class HbmLintExporter extends GenericExporter {

    private static final String TEXT_REPORT_FTL = "lint/text-report.ftl";
    
    public void start() {
    	// TODO: make non-hardwired 
    	setFilePattern( "hbmlint-result.txt" );
		setTemplateName( TEXT_REPORT_FTL );		
    	super.start();
    }
	protected void setupContext() {
		HbmLint hbmlint = HbmLint.createInstance();
		hbmlint.analyze( MetadataHelper.getMetadata(getConfiguration()) );
		getProperties().put("lintissues", hbmlint.getResults());
		super.setupContext();		
	}
	
	public String getName() {
		return "hbmlint";
	}


}

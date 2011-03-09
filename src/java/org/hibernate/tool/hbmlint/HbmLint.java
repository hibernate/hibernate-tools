package org.hibernate.tool.hbmlint;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.SchemaByMetaDataDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;

public class HbmLint implements IssueCollector {

	
	final Detector[] detectors;
	
	public HbmLint(Detector[] detectors) {
		this.detectors = detectors;
	}
	
	List results = new ArrayList();
	
	public void analyze(Configuration cfg) {
		
		Settings settings = cfg.buildSettings();
		
		for (int i = 0; i < detectors.length; i++) {
			detectors[i].initialize( cfg, settings );
			detectors[i].visit(cfg, this);
		}
					
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.tool.hbmlint.IssueCollector#reportProblem(org.hibernate.tool.hbmlint.Issue)
	 */
	public void reportIssue(Issue analyze) {
		results.add(analyze);
	}
	
	public List getResults() {
		return results;	
	}

	public static HbmLint createInstance() {
		return new HbmLint( 
			new Detector[] {
					new BadCachingDetector(),
					new InstrumentationDetector(),
					new ShadowedIdentifierDetector(),
					new SchemaByMetaDataDetector()
			});
		
	}

}

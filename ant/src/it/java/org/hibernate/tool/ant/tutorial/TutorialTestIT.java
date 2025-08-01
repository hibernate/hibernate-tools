package org.hibernate.tool.ant.tutorial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.hibernate.tool.it.ant.TestTemplate;
import org.junit.jupiter.api.Test;

public class TutorialTestIT extends TestTemplate {
	
    @Test
    public void testTutorial() throws Exception {
		setHibernateToolTaskXml(
				"        <hibernatetool destdir='generated'>                          \n" +
				"            <jdbcconfiguration propertyfile='hibernate.properties'/> \n" +
				"            <hbm2java/>                                              \n" +
				"        </hibernatetool>                                             \n"
		);
		setDatabaseCreationScript(new String[] {
				"create table PERSON (ID int not null, NAME varchar(20), primary key (ID))"
		});
		createProjectAndBuild();
    	verifyResult();
    }

	private void verifyResult() {
		File generatedOutputFolder = new File(getProjectDir(), "generated");
		assertTrue(generatedOutputFolder.exists());
		assertTrue(generatedOutputFolder.isDirectory());
		assertEquals(1, generatedOutputFolder.list().length);
		File generatedPersonJavaFile = new File(generatedOutputFolder, "Person.java");
		assertTrue(generatedPersonJavaFile.exists());
		assertTrue(generatedPersonJavaFile.isFile());
	}
	
}

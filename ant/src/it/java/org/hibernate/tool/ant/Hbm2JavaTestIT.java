package org.hibernate.tool.ant;

import org.hibernate.tool.it.ant.TestTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Hbm2JavaTestIT extends TestTemplate {

    @Test
    public void testJpaDefault() throws Exception {
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
        assertFolderExists("generated", 1);
        assertFileExists("generated/Person.java");
        String generatedPersonJavaFileContents = getFileContents("generated/Person.java");
        assertTrue(generatedPersonJavaFileContents.contains("import jakarta.persistence.Entity;"));
        assertTrue(generatedPersonJavaFileContents.contains("public class Person "));
    }

    @Test
    public void testNoAnnotations() throws Exception {
        setHibernateToolTaskXml(
                "        <hibernatetool destdir='generated'>                          \n" +
                "            <jdbcconfiguration propertyfile='hibernate.properties'/> \n" +
                "            <hbm2java ejb3='false'/>                                 \n" +
                "        </hibernatetool>                                             \n"
        );
        setDatabaseCreationScript(new String[] {
                "create table PERSON (ID int not null, NAME varchar(20), primary key (ID))"
        });
        createProjectAndBuild();
        assertFolderExists("generated", 1);
        assertFileExists("generated/Person.java");
        String generatedPersonJavaFileContents = getFileContents("generated/Person.java");
        assertFalse(generatedPersonJavaFileContents.contains("import jakarta.persistence.Entity;"));
        assertTrue(generatedPersonJavaFileContents.contains("public class Person "));
    }

    @Test
    public void testNoGenerics() throws Exception {
        setHibernateToolTaskXml(
                "        <hibernatetool destdir='generated'>                          \n" +
                "            <jdbcconfiguration propertyfile='hibernate.properties'/> \n" +
                "            <hbm2java jdk5='false'/>                                 \n" +
                "        </hibernatetool>                                             \n"
        );
        setDatabaseCreationScript(new String[] {
                "create table PERSON (ID int not null, NAME varchar(20), " +
                        "primary key (ID))",
                "create table ITEM (ID int not null,  NAME varchar(20), OWNER_ID int not null, " +
                        "primary key (ID), foreign key (OWNER_ID) references PERSON(ID))"
        });
        createProjectAndBuild();
        assertFolderExists("generated", 2);
        assertFileExists("generated/Person.java");
        String generatedPersonJavaFileContents = getFileContents("generated/Person.java");
        assertTrue(generatedPersonJavaFileContents.contains("public class Person "));
        assertFalse(generatedPersonJavaFileContents.contains("Set<Item>"));
        assertFileExists("generated/Item.java");
        String generatedItemJavaFileContents = getFileContents("generated/Item.java");
        assertTrue(generatedItemJavaFileContents.contains("public class Item "));
    }

    @Test
    public void testUseGenerics() throws Exception {
        setHibernateToolTaskXml(
                "        <hibernatetool destdir='generated'>                          \n" +
                "            <jdbcconfiguration propertyfile='hibernate.properties'/> \n" +
                "            <hbm2java/>                                              \n" +
                "        </hibernatetool>                                             \n"
        );
        setDatabaseCreationScript(new String[] {
                "create table PERSON (ID int not null, NAME varchar(20), " +
                        "primary key (ID))",
                "create table ITEM (ID int not null,  NAME varchar(20), OWNER_ID int not null, " +
                        "primary key (ID), foreign key (OWNER_ID) references PERSON(ID))"
        });
        createProjectAndBuild();
        assertFolderExists("generated", 2);
        assertFileExists("generated/Person.java");
        String generatedPersonJavaFileContents = getFileContents("generated/Person.java");
        assertTrue(generatedPersonJavaFileContents.contains("public class Person "));
        assertTrue(generatedPersonJavaFileContents.contains("Set<Item>"));
        assertFileExists("generated/Item.java");
        String generatedItemJavaFileContents = getFileContents("generated/Item.java");
        assertTrue(generatedItemJavaFileContents.contains("public class Item "));
    }

}

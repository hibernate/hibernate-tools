/*
 * Created on 2015-06-26 Copied from JdbcHbm2JavaEjb3Test and adjusted.
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.ant.JDBCConfigurationTask;
import org.hibernate.tool.test.TestHelper;

/**
 * @author daren
 *
 *
 */
public class CustomReverseEngineerStrategyTest extends JDBCMetaDataBinderTestCase {

    protected void setUp() throws Exception {
        //Taken from ancestors with minor adjustment.
        assertNoTables(); // If fails may need to remove create table statements from testdb.log and testdb.script

        if (getOutputDir() != null) {
            getOutputDir().mkdirs();
        }

        if (cfg == null) { // only do if we haven't done it before - to save time!

            try {
                executeDDL(getDropSQL(), true);
            } catch (SQLException se) {
                System.err.println("Error while dropping - normally ok.");
                se.printStackTrace();
            }

            JDBCConfigurationTask jdbcConfigurationTask = new JDBCConfigurationTask();
            jdbcConfigurationTask.setReverseStrategy(CustomReverseEngineeringStrategy.class.getName());
            jdbcConfigurationTask.init();
            cfg = (JDBCMetaDataConfiguration) jdbcConfigurationTask.getConfiguration();
            String[] sqls = getCreateSQL();
            executeDDL(sqls, false);
            cfg.readFromJDBC();
        }

        cfg.buildMappings(); //Rebuild the mappings to add the new entities.
        POJOExporter exporter = new POJOExporter(getConfiguration(), getOutputDir());
        exporter.setTemplatePath(new String[0]);
        exporter.getProperties().setProperty("ejb3", "true");
        exporter.getProperties().setProperty("jdk5", "true");
        exporter.start();
    }

    public void testFileExistence() {
        assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/Postcode.java"));
        assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/Address.java"));
    }

    public void testUniqueConstraints() {
        assertNotNull(findFirstString("@OneToMany(fetch=FetchType.LAZY", new File(getOutputDir(), "Postcode.java")));
        assertNotNull(findFirstString("@ManyToOne(fetch=FetchType.EAGER)", new File(getOutputDir(), "Address.java")));
    }

    public void testCompile() {

        File file = new File(getOutputDir().getAbsolutePath() + "/target");
        file.mkdir();

        ArrayList list = new ArrayList();
        List jars = new ArrayList();
//        jars.add("hibernate-jpa-2.0-api-1.0.1.Final.jar");

        TestHelper.compile(getOutputDir(), file, TestHelper.visitAllFiles(getOutputDir(), list), "1.5", TestHelper.buildClasspath(jars));
        assertFileAndExists(new File(file.getAbsoluteFile() + "/Postcode.class"));
        assertFileAndExists(new File(file.getAbsolutePath() + "/Address.class"));
        TestHelper.deleteDir(file);
    }

    protected String[] getCreateSQL() {

        return new String[]{
            "create table postcode ( id char not null, code varchar(20), name varchar(20), primary key (id), constraint o1 unique (code), constraint o2 unique (name) )",
            "create table address ( id char not null, street varchar(20), suburb varchar(20), postcodeid char not null, primary key (id), foreign key (postcodeid) references postcode(id) )"
        };
    }

    protected String[] getDropSQL() {

        return new String[]{
            "drop table address",
            "drop table postcode"
        };
    }

}

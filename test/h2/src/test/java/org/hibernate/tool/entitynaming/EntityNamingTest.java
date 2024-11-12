/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL),
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.entitynaming;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.RevengStrategyFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test lowercase database identifiers and specified entity package and class name.
 *
 * Settings: 1. Adjust hibernate.properties to clear hibernate.default_schema and
 * hibernate.default_catalog. Setting these values fail the classname/tablename matching logic
 * because of a call to nullifyDefaultCatalogAndSchema(table) in RootClassBinder.bind() before the
 * TableToClassName logic.
 *
 * 2. Create a custom RevengStrategy (eg RevengStrategyEntityNaming.java) with the required
 * SchemaSelection settings.
 *
 * @author Daren
 */
@TestInstance(Lifecycle.PER_CLASS)
public class EntityNamingTest {

  private static final Logger log = Logger.getLogger(EntityNamingTest.class);

  @TempDir
  public File outputDir = new File("output");

  static final String packageName = "com.entity";
  static final String SHAPE_HBM_FILE= "shape.hbm.xml";

  Properties hibernateProperties = new Properties();

  @BeforeAll
  public void setUp() {
    JdbcUtil.createDatabase(this);
    try {
      hibernateProperties.load(JdbcUtil.getAlternateHibernateProperties(this));
      if (log.isInfoEnabled()) {
        log.info(hibernateProperties.toString());
      }
    } catch (IOException ex) {
      throw new RuntimeException("Alternate hibernate.properties does not exist!", ex);
    }
  }

  @AfterAll
  public void tearDown() {
    JdbcUtil.dropDatabase(this);
  }

  @Test
  public void testGenerateJava() throws IOException {
    File[] revengFiles = new File[]{
      ResourceUtil.resolveResourceFile(this.getClass(), "reveng.xml")};

    RevengStrategy reveng = RevengStrategyFactory.createReverseEngineeringStrategy(
        null, revengFiles);

    reveng = new RevengStrategyEntityNaming(reveng);

    //Necessary to set the root strategy.
    RevengSettings revengSettings
        = new RevengSettings(reveng).setDefaultPackageName(packageName)
            .setDetectManyToMany(true)
            .setDetectOneToOne(true)
            .setDetectOptimisticLock(true);

    reveng.setSettings(revengSettings);
    MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
        .createReverseEngineeringDescriptor(reveng, hibernateProperties);

    Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
    exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
    exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    exporter.getProperties().setProperty("ejb3", "true");
    exporter.start();
    String packageDir = outputDir + File.separator
        + packageName.replace(".", File.separator);
    File dummy = new File(packageDir, "Dummy.java");
    assertTrue(dummy.exists());

    //Check @Version annotation preceeds the method
    String str = new String(Files.readAllBytes(dummy.toPath()));
    int stringPos = str.indexOf("@Version");
    String versionMatch = "@Version\n    @Column(name=\"duVersion\", nullable=false)\n    public byte getDuVersion() {";
    String versionInFile = str.substring(stringPos, stringPos + versionMatch.length());
    Assertions.assertEquals(versionMatch,versionInFile );
    //Check addition of code via class-code MetaAttribute in PojoExtraClassCode.ftl
    assertTrue(str.contains("specialProc()"));

    File order = new File(packageDir, "Order.java");
    assertTrue(order.exists());
    File orderItem = new File(packageDir, "OrderItem.java");
    assertTrue(orderItem.exists());
    str = new String(Files.readAllBytes(orderItem.toPath()));
    assertTrue(str.contains("private Integer oiId;"));
    assertTrue(str.contains("private Order order;"));
    //specialProc only added to dummy table.
    assertFalse(str.contains("specialProc()"));

    //Check @Version annotation preceeds the field.
    //Many hibernatetool.xxxName.toolclass may be loaded into the freemarker context as hibernate properties.
    //The toolclass methods are accessable by xxxName in the template ie {$xxxName.methodName()
    exporter.getProperties().put("hibernatetool.annotateField.toolclass", "org.hibernate.tool.entitynaming.FieldAnnotation");
    exporter.start();
    dummy = new File(packageDir, "Dummy.java");
    assertTrue(dummy.exists());
    str = new String(Files.readAllBytes(dummy.toPath()));
    stringPos = str.indexOf("@Version");
    versionMatch = "@Version\n    @Column(name=\"duVersion\", nullable=false)\n    private byte duVersion;";
    versionInFile = str.substring(stringPos, stringPos + versionMatch.length());
    Assertions.assertEquals(versionMatch,versionInFile );

    //Check toString()
    stringPos = str.indexOf("     * toString");
    String toStringMatch = "     * toString\n" +
    "     * @return String\n" +
    "     */\n" +
    "    public String toString() {\n" +
    "        StringBuffer buffer = new StringBuffer();\n" +
    "        buffer.append(getClass().getName()).append(\"@\").append(Integer." +
    "toHexString(hashCode())).append(\" [\");\n" +
    "        buffer.append(\"duData\").append(\"='\").append(getDuData()).append(\"' \");\n" +
    "        buffer.append(\"]\");\n" +
    "        return buffer.toString();\n" +
    "    }\n";
    String toStringInFile = str.substring(stringPos, stringPos + toStringMatch.length());
    Assertions.assertEquals(toStringMatch,toStringInFile );

    //Check equals()
    stringPos = str.indexOf("    public boolean");
    String equalsMatch = "    public boolean equals(Object other) {\n" +
    "        if ( (this == other ) ) return true;\n" +
    "        if ( (other == null ) ) return false;\n" +
    "        if ( !(other instanceof com.entity.Dummy) ) return false;\n" +
    "        com.entity.Dummy castOther = ( com.entity.Dummy ) other;\n" +
    "        return ( (this.getDuId()==castOther.getDuId()) || ( this.getDuId()!=null" +
    " && castOther.getDuId()!=null && this.getDuId().equals(castOther.getDuId()) ) )\n" +
    " && ( (this.getDuData()==castOther.getDuData()) || ( this.getDuData()!=null " +
    "&& castOther.getDuData()!=null && this.getDuData().equals(castOther.getDuData()) ) );\n" +
    "    }\n";
    String equalsInFile = str.substring(stringPos, stringPos + equalsMatch.length());
    Assertions.assertEquals(equalsMatch, equalsInFile);

    //Check hashcode()
    stringPos = str.indexOf("    public int hashCode()");
    String hashMatch = "    public int hashCode() {\n" +
    "        int result = 17;\n" +
    "        result = 37 * result + ( getDuId() == null ? 0 : this.getDuId().hashCode() );\n" +
    "        result = 37 * result + ( getDuData() == null ? 0 : this.getDuData().hashCode() );\n" +
    "        return result;\n" +
    "    }\n";
    String hashInFile = str.substring(stringPos, stringPos + hashMatch.length());
    Assertions.assertEquals(hashMatch, hashInFile);

    //Check class description
    stringPos = str.indexOf(" * Long description");
    String descMatch = " * Long description\n" +
    " * for javadoc\n" +
    " * of a particular entity.\n" +
    " */\n";
    String descFile = str.substring(stringPos, stringPos + descMatch.length());
    Assertions.assertEquals(descMatch, descFile);

    //The only untested template is for interfaces. For completeness I will add a test
    // for it here, even though it probably won't be used in reverse engineering.
    File[] hbmFiles = new File[]{
      ResourceUtil.resolveResourceFile(this.getClass(), SHAPE_HBM_FILE)};
    MetadataDescriptor nativeMetadataDesc= MetadataDescriptorFactory.createNativeDescriptor(
        null, hbmFiles, hibernateProperties);
    exporter = ExporterFactory.createExporter(ExporterType.JAVA);
    exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, nativeMetadataDesc);
    exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    exporter.getProperties().setProperty("ejb3", "true");
    exporter.start();

    File shape = new File(outputDir, "Shape.java");
    assertTrue(shape.exists());
    str = new String(Files.readAllBytes(shape.toPath()));
    stringPos = str.indexOf("public interface Shape");
    String interfaceMatch = "public interface Shape   {\n" +
    "\n" +
    "    /**\n" +
    "     * Test Field Description\n" +
    "     */\n" +
    "    public long getId();\n";
    String interfaceFile = str.substring(stringPos, stringPos + interfaceMatch.length());
    Assertions.assertEquals(interfaceMatch, interfaceFile);
  }
}

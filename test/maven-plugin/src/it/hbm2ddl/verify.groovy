import java.io.*;

File schema = new File(basedir, "target/generated-resources/schema.ddl");
if (!schema.isFile()) {
    throw new FileNotFoundException("Could not find generated schema file: " + schema);
}
